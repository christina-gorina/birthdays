package com.example.birthdays;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.birthdays.EventsContract.ImportantDates;

import java.util.ArrayList;

public class ImportantDatesFragment extends Fragment {
    private EventsDbHelper eventsDbHelper;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    Cursor cursor;
    private RecyclerView importantDateView;
    ArrayList<ImportantDatesItem> importantDateItems = new ArrayList<>();

    public ImportantDatesFragment(){

    }

    @Override
    public void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Fragment 2 onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.println("Fragment 2 onCreateView");
        View view = inflater.inflate(R.layout.important_dates_fragment, container, false);
        eventsDbHelper = new EventsDbHelper(getContext());

        importantDateView = view.findViewById(R.id.importantDaysItems);
        adapter = new ImportantDateAdapter(importantDateItems, getContext());

        layoutManager = new LinearLayoutManager(getContext());
        importantDateView.setAdapter(adapter);
        importantDateView.setLayoutManager(layoutManager);

        return view;
    }

    private void displayDatabaseInfo() {
        // Создадим и откроем для чтения базу данных
        SQLiteDatabase db = eventsDbHelper.getReadableDatabase();

        // Зададим условие для выборки - список столбцов
        String[] projection = {
                ImportantDates._ID,
                ImportantDates.COLUMN_NAME,
                ImportantDates.COLUMN_DATE,
                ImportantDates.COLUMN_NOYEAR,
                ImportantDates.COLUMN_NOTIFICATION };

        // Делаем запрос
        cursor = db.query(
                ImportantDates.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        System.out.println("////////////////////////////////////////////////////////////////////////");
        System.out.println("cursor.getCount() = " + cursor.getCount());

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(ImportantDates.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(ImportantDates.COLUMN_NAME));
                String date = cursor.getString(cursor.getColumnIndex(ImportantDates.COLUMN_DATE));
                int noyear = cursor.getInt(cursor.getColumnIndex(ImportantDates.COLUMN_NOYEAR));
                int notification = cursor.getInt(cursor.getColumnIndex(ImportantDates.COLUMN_NOTIFICATION));

                System.out.println("date ImportantDates = " + date);
                DateOfEvent dateOfEvent = new DateOfEvent(date);

                importantDateItems.add(new ImportantDatesItem(id, name, dateOfEvent, noyear, notification));
                /*System.out.println("id = " + id);
                System.out.println("name = " + name);
                System.out.println("date = " + date);
                System.out.println("noyear = " + noyear);
                System.out.println("notification = " + notification);*/

            }
        }
        eventsDbHelper.close();
    }
}
