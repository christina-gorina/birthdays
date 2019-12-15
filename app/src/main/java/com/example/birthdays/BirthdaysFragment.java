package com.example.birthdays;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;

public class BirthdaysFragment extends Fragment {
    private RecyclerView birthdayItems;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    ArrayList<BirthdayItem> recyclerViewItems = new ArrayList<>();

    private static final String CONTACT_ID = ContactsContract.CommonDataKinds.Event.CONTACT_ID;
    private static final String DISPLAY_NAME_PRIMARY = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;
    private static final String START_DATE = ContactsContract.CommonDataKinds.Event.START_DATE;

    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    private static boolean READ_CONTACTS_GRANTED = false;

    public BirthdaysFragment () {}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.println("Fragment 1 onCreateView");

        View view = inflater.inflate(R.layout.birthdays_fragment, container, false);
        // получаем разрешения
        int hasReadContactPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS);
        // если устройство до API 23, устанавливаем разрешение
        if(hasReadContactPermission == PackageManager.PERMISSION_GRANTED){
            READ_CONTACTS_GRANTED = true;
        }
        else{
            // вызываем диалоговое окно для установки разрешений
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        }

        // если разрешение установлено, загружаем контакты
        if (READ_CONTACTS_GRANTED){
            loadContacts();
        }

        birthdayItems = view.findViewById(R.id.birthdayItems);
        adapter = new BirthdayAdapter(recyclerViewItems, getContext());

        layoutManager = new LinearLayoutManager(getContext());
        birthdayItems.setAdapter(adapter);
        birthdayItems.setLayoutManager(layoutManager);

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
            loadContacts();
        }
        else{
            Toast.makeText(getActivity(), "Требуется установить разрешения", Toast.LENGTH_LONG).show();
        }
    }

    private void loadContacts(){
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

        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(content_uri, projection, where, selectionArgs, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(CONTACT_ID)));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME_PRIMARY));
                String birthday = cursor.getString(cursor.getColumnIndex(START_DATE));
                Bitmap previewPhoto = getContactPhoto(id);

                DateOfEvent dateOfEvent = new DateOfEvent(birthday);

                System.out.println("date BirthdayItem = " + birthday);

                recyclerViewItems.add(new BirthdayItem(previewPhoto, name, dateOfEvent, BirthdayItem.ITEM_TYPE_EVENT));
            }
            cursor.close();
            Collections.sort(recyclerViewItems, new Comparator<BirthdayItem>() {

                @Override
                public int compare(BirthdayItem b1, BirthdayItem b2) {

                    DateOfEvent o1 = b1.getDateOfEvent();
                    DateOfEvent o2 = b2.getDateOfEvent();

                    if(o1.getMonth() < o2.getMonth())
                        return -1;
                    if(o1.getMonth() > o2.getMonth())
                        return 1;

                    if (o1.getDayOfMounth() < o2.getDayOfMounth())
                        return -1;
                    if (o1.getDayOfMounth() > o2.getDayOfMounth())
                        return 1;

                    return 0;
                }

            });

            int lastMounth = -1;
            ListIterator<BirthdayItem> listIterator = recyclerViewItems.listIterator();
            while (listIterator.hasNext()){
                int currentMounth = listIterator.next().getDateOfEvent().getMonth();
                if(currentMounth != lastMounth){
                    listIterator.previous();
                    listIterator.add(new BirthdayItem(currentMounth, BirthdayItem.ITEM_TYPE_HEADER));
                    lastMounth = currentMounth;
                }
            }

            System.out.println("recyclerViewItems size = " + recyclerViewItems.size());
        }
    }

    public Bitmap getContactPhoto(int id){
        ContentResolver cr = getContext().getContentResolver();

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
