package com.example.birthdays;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.TreeMap;

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

    ArrayList<BirthdayItem> eventOfMonth0 = new ArrayList<>();
    ArrayList<BirthdayItem> eventOfMonth1 = new ArrayList<>();
    ArrayList<BirthdayItem> eventOfMonth2 = new ArrayList<>();
    ArrayList<BirthdayItem> eventOfMonth3 = new ArrayList<>();
    ArrayList<BirthdayItem> eventOfMonth4 = new ArrayList<>();
    ArrayList<BirthdayItem> eventOfMonth5 = new ArrayList<>();
    ArrayList<BirthdayItem> eventOfMonth6 = new ArrayList<>();
    ArrayList<BirthdayItem> eventOfMonth7 = new ArrayList<>();
    ArrayList<BirthdayItem> eventOfMonth8 = new ArrayList<>();
    ArrayList<BirthdayItem> eventOfMonth9 = new ArrayList<>();
    ArrayList<BirthdayItem> eventOfMonth10 = new ArrayList<>();
    ArrayList<BirthdayItem> eventOfMonth11 = new ArrayList<>();

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

    @RequiresApi(api = Build.VERSION_CODES.N)
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
/*
            PriorityQueue<BirthdayItem> eventsMap = new PriorityQueue(new Comparator<BirthdayItem>() {

                @Override
                public int compare(BirthdayItem b1, BirthdayItem b2) {

                     DateOfEvent o1 = b1.getDateOfEvent();
                     DateOfEvent o2 = b2.getDateOfEvent();

                        if(o1.getMonth() < o2.getMonth()){
                        return -1;
                    }else if(o1.getMonth() > o2.getMonth()){
                        return 1;
                    }

                    if (o1.getDayOfMounth() < o2.getDayOfMounth())
                        return -1;

                    if (o1.getDayOfMounth() > o2.getDayOfMounth())
                        return 1;
                    return 0;

                }

            });*/

            while (cursor.moveToNext()) {
                int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(CONTACT_ID)));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME_PRIMARY));
                String birthday = cursor.getString(cursor.getColumnIndex(START_DATE));
                Bitmap previewPhoto = getContactPhoto(id);

                DateOfEvent dateOfEvent = new DateOfEvent(birthday);
                int bMonth = dateOfEvent.getMonth();

                recyclerViewItems.add(new BirthdayItem(previewPhoto, name, dateOfEvent, BirthdayItem.ITEM_TYPE_EVENT));

               /* switch (bMonth){
                    case 0:
                        eventOfMonth0.add(new BirthdayItem(previewPhoto, name, dateOfEvent, BirthdayItem.ITEM_TYPE_EVENT));
                        break;
                    case 1:
                        eventOfMonth1.add(new BirthdayItem(previewPhoto, name, dateOfEvent, BirthdayItem.ITEM_TYPE_EVENT));
                        break;
                    case 2:
                        eventOfMonth2.add(new BirthdayItem(previewPhoto, name, dateOfEvent, BirthdayItem.ITEM_TYPE_EVENT));
                        break;
                    case 3:
                        eventOfMonth3.add(new BirthdayItem(previewPhoto, name, dateOfEvent, BirthdayItem.ITEM_TYPE_EVENT));
                        break;
                    case 4:
                        eventOfMonth4.add(new BirthdayItem(previewPhoto, name, dateOfEvent, BirthdayItem.ITEM_TYPE_EVENT));
                        break;
                    case 5:
                        eventOfMonth5.add(new BirthdayItem(previewPhoto, name, dateOfEvent, BirthdayItem.ITEM_TYPE_EVENT));
                        break;
                    case 6:
                        eventOfMonth6.add(new BirthdayItem(previewPhoto, name, dateOfEvent, BirthdayItem.ITEM_TYPE_EVENT));
                        break;
                    case 7:
                        eventOfMonth7.add(new BirthdayItem(previewPhoto, name, dateOfEvent, BirthdayItem.ITEM_TYPE_EVENT));
                        break;
                    case 8:
                        eventOfMonth8.add(new BirthdayItem(previewPhoto, name, dateOfEvent, BirthdayItem.ITEM_TYPE_EVENT));
                        break;
                    case 9:
                        eventOfMonth9.add(new BirthdayItem(previewPhoto, name, dateOfEvent, BirthdayItem.ITEM_TYPE_EVENT));
                        break;
                    case 10:
                        eventOfMonth10.add(new BirthdayItem(previewPhoto, name, dateOfEvent, BirthdayItem.ITEM_TYPE_EVENT));
                        break;
                    case 11:
                        eventOfMonth11.add(new BirthdayItem(previewPhoto, name, dateOfEvent, BirthdayItem.ITEM_TYPE_EVENT));
                        break;
                }*/
            }
            cursor.close();
/*
            System.out.println("eventOfMonth0 = " + eventOfMonth0.size());
            if(eventOfMonth0.size() > 0 ){
                recyclerViewItems.add(new BirthdayItem(0, BirthdayItem.ITEM_TYPE_HEADER));
                for (BirthdayItem evt : eventOfMonth0) {recyclerViewItems.add(evt);}
            }

            if(eventOfMonth1.size() > 0 ){
                recyclerViewItems.add(new BirthdayItem(1, BirthdayItem.ITEM_TYPE_HEADER));
                for (BirthdayItem evt : eventOfMonth1) {recyclerViewItems.add(evt);}
            }

            if(eventOfMonth2.size() > 0 ){
                recyclerViewItems.add(new BirthdayItem(2, BirthdayItem.ITEM_TYPE_HEADER));
                for (BirthdayItem evt : eventOfMonth2) {recyclerViewItems.add(evt);}
            }

            if(eventOfMonth3.size() > 0 ){
                recyclerViewItems.add(new BirthdayItem(3, BirthdayItem.ITEM_TYPE_HEADER));
                for (BirthdayItem evt : eventOfMonth3) {recyclerViewItems.add(evt);}
            }

            if(eventOfMonth4.size() > 0 ){
                recyclerViewItems.add(new BirthdayItem(4, BirthdayItem.ITEM_TYPE_HEADER));
                for (BirthdayItem evt : eventOfMonth4) {recyclerViewItems.add(evt);}
            }

            if(eventOfMonth5.size() > 0 ){
                recyclerViewItems.add(new BirthdayItem(5, BirthdayItem.ITEM_TYPE_HEADER));
                for (BirthdayItem evt : eventOfMonth5) {recyclerViewItems.add(evt);}
            }

            if(eventOfMonth6.size() > 0 ){
                recyclerViewItems.add(new BirthdayItem(6, BirthdayItem.ITEM_TYPE_HEADER));
                for (BirthdayItem evt : eventOfMonth6) {recyclerViewItems.add(evt);}
            }

            if(eventOfMonth7.size() > 0 ){
                recyclerViewItems.add(new BirthdayItem(7, BirthdayItem.ITEM_TYPE_HEADER));
                for (BirthdayItem evt : eventOfMonth7) {recyclerViewItems.add(evt);}
            }

            if(eventOfMonth8.size() > 0 ){
                recyclerViewItems.add(new BirthdayItem(8, BirthdayItem.ITEM_TYPE_HEADER));
                for (BirthdayItem evt : eventOfMonth8) {recyclerViewItems.add(evt);}
            }

            if(eventOfMonth9.size() > 0 ){
                recyclerViewItems.add(new BirthdayItem(9, BirthdayItem.ITEM_TYPE_HEADER));
                for (BirthdayItem evt : eventOfMonth9) {recyclerViewItems.add(evt);}
            }

            if(eventOfMonth10.size() > 0 ){
                recyclerViewItems.add(new BirthdayItem(10, BirthdayItem.ITEM_TYPE_HEADER));
                for (BirthdayItem evt : eventOfMonth10) {recyclerViewItems.add(evt);}
            }

            if(eventOfMonth11.size() > 0 ){
                recyclerViewItems.add(new BirthdayItem(11, BirthdayItem.ITEM_TYPE_HEADER));
                for (BirthdayItem evt : eventOfMonth11) {recyclerViewItems.add(evt);}
            }
*/

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

          /*  for(BirthdayItem item :recyclerViewItems){
                mounthOfCurrentItem
            }*/

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


    ///////////////////////////
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Fragment 1 onCreate");
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("Fragment 1 onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("Fragment 1 onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("Fragment 1 onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("Fragment 1 onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("Fragment 1 onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("Fragment 1 onDestroyView");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Fragment 1 onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println("Fragment 1 onDetach");
    }

}
