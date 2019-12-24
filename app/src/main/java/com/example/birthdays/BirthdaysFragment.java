package com.example.birthdays;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class BirthdaysFragment extends Fragment {
    ArrayList<BirthdayItem> recyclerViewItems = new ArrayList<>();
    private long idEventInfo;

    private static final String CONTACT_ID = ContactsContract.CommonDataKinds.Event.CONTACT_ID;
    private static final String DISPLAY_NAME_PRIMARY = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;
    private static final String START_DATE = ContactsContract.CommonDataKinds.Event.START_DATE;
    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    private static boolean READ_CONTACTS_GRANTED = false;
    private static final String TAG = "BirthdaysFragment";
    private static final int COUNT_OF_DAYS_BEFORE_NOTIFICATION = 3; // За сколько дней напомнить о дате
    private static final int TIME_OF_NOTIFICATION = 8; // В 24 формате

    public BirthdaysFragment () {}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.println("Fragment 1 onCreateView");

        View view = inflater.inflate(R.layout.birthdays_fragment, container, false);

        // получаем разрешения
        int hasReadContactPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS);
        // если разрешение уже было дано ранее
        if(hasReadContactPermission == PackageManager.PERMISSION_GRANTED){
            READ_CONTACTS_GRANTED = true;
        }
        else{
            // вызываем диалоговое окно для установки разрешений
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        }

        // если разрешение установлено, загружаем контакты
        if (READ_CONTACTS_GRANTED){
            RecyclerView birthdayItems = view.findViewById(R.id.birthdayItems);
            showContacts(birthdayItems);
        }

        new CheckContactsCreator(getContext()).startToCheckContactsList();
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE_READ_CONTACTS:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    READ_CONTACTS_GRANTED = true;
                }
        }

        if(READ_CONTACTS_GRANTED){
            RecyclerView birthdayItems = getActivity().findViewById(R.id.birthdayItems);
            showContacts(birthdayItems);
        }
        else{
            Toast.makeText(getActivity(), "Требуется установить разрешения", Toast.LENGTH_LONG).show();
        }
    }

    private void showContacts(RecyclerView birthdayItems){
        loadContacts(getContext());
        Collections.sort(recyclerViewItems, new EventsComparator());
        new AddMonthSeparator().add(BirthdayItem.class.getName(), recyclerViewItems);

        Adapter adapter = new BirthdayAdapter(recyclerViewItems, getContext());
        LayoutManager layoutManager = new LinearLayoutManager(getContext());
        birthdayItems.setAdapter(adapter);
        birthdayItems.setLayoutManager(layoutManager);
    }

    public void loadContacts(Context context){ // context необходимо передавать, так как эта функция еще в  CheckContactsWorker вызывается
        Log.d(TAG, "--------------loadContacts-----------");
        Uri content_uri = ContactsContract.Data.CONTENT_URI;

        String[] projection = new String[] {
                CONTACT_ID,
                DISPLAY_NAME_PRIMARY,
                START_DATE
        };

        String where = ContactsContract.Data.MIMETYPE + "= ? AND " +
                ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;

        String[] selectionArgs = new String[] {
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE
        };

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(content_uri, projection, where, selectionArgs, null);
        String type = EventItems.CONTACT_TYPE ;
        int notification;
        int noyear;

        DB db = new DB(context);
        db.open();
        db.updateStartSynchronization(); // обнуляем поле синхронизации

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                long id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(CONTACT_ID)));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME_PRIMARY));
                String birthday = cursor.getString(cursor.getColumnIndex(START_DATE));
                Bitmap previewPhoto = getContactPhoto(id, context);
                DateOfEvent dateOfEvent = new DateOfEvent(birthday);

                Log.d(TAG, "id = " + id);
                Log.d(TAG, "name = " + name);
                Log.d(TAG, "birthday = " + birthday);

                if(dateOfEvent.getYear() != 0)
                    noyear = 1;
                else
                    noyear = 0;

                Cursor cursorInfo = db.getEventInfo(id, type); // проверяем есть ли этот контакт в базе
                System.out.println("cursorInfo.getCount() = " + cursorInfo.getCount());
                if(cursorInfo.getCount() == 0){
                    notification = 0;
                    idEventInfo = db.addEventInfo(id, type, noyear, notification);
                    NotificationCreator nc = new NotificationCreator(id, idEventInfo, type, name, birthday, TIME_OF_NOTIFICATION, context);
                    nc.createNotificationInDay();
                    nc.createNotificationEarly(COUNT_OF_DAYS_BEFORE_NOTIFICATION);
                }else{
                    notification = db.getNotification(id, type);
                }
                System.out.println("!!!notification = " + notification);
                db.updateEventSynchronization(id, type);

                recyclerViewItems.add(new BirthdayItem(id, previewPhoto, name, dateOfEvent, noyear, notification, BirthdayItem.ITEM_TYPE_EVENT));
            }
            cursor.close();
            db.deleteEventSynchronization();

////////////////////////////////////////////////////////Тест синхронизации//////////////////////////////////////////////////
            /*Cursor cursorEvents = db.getEventInfoOfType(EventItems.CONTACT_TYPE);
            Log.d(TAG, "///////////////////////synchronization//////////////////////////////////////");
            if (cursorEvents.getCount() > 0) {
                while (cursorEvents.moveToNext()) {
                    int id = cursorEvents.getInt(cursorEvents.getColumnIndex(EventsContract.EventsInfo.COLUMN_ID));
                    String events_id = cursorEvents.getString(cursorEvents.getColumnIndex(EventsContract.EventsInfo.COLUMN_EVENTS_ID));
                    String typeEvent = cursorEvents.getString(cursorEvents.getColumnIndex(EventsContract.EventsInfo.COLUMN_TYPE));
                    String synchronization = cursorEvents.getString(cursorEvents.getColumnIndex(EventsContract.EventsInfo.COLUMN_SYNCHRONISATION));

                    Log.d(TAG, "id = " + id + ", events_id = " + events_id + ", typeEvent = " + typeEvent + ", synchronization = " + synchronization);
                }
            }
            cursorEvents.close();*/
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

            db.close();
        }
    }

    public Bitmap getContactPhoto(long id, Context context){
        ContentResolver cr = context.getContentResolver();

        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = cr.query(photoUri, new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(cursor.getColumnIndex(ContactsContract.Contacts.Photo.PHOTO));
                if (data != null) {
                    InputStream contactPhoto =  new ByteArrayInputStream(data);
                    Bitmap  bitmapPhoto = BitmapFactory.decodeStream(contactPhoto);
                    return bitmapPhoto;
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }
}
