package com.example.birthdays;

import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.birthdays.EventsContract.ImportantDates;
import java.util.ArrayList;
import java.util.Collections;

public class ImportantDatesFragment extends Fragment {
    private static final String TAG = "ImportantDatesFragment";
    ArrayList<ImportantDatesItem> importantDateItems = new ArrayList<>();

    public ImportantDatesFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Fragment 2 onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Fragment 2 onCreateView");
        displayDatabaseInfo();


        View view = inflater.inflate(R.layout.important_dates_fragment, container, false);
        RecyclerView importantDateView = view.findViewById(R.id.importantDaysItems);
        Adapter adapter = new ImportantDateAdapter(importantDateItems, getContext());

        LayoutManager layoutManager = new LinearLayoutManager(getContext());
        importantDateView.setAdapter(adapter);
        importantDateView.setLayoutManager(layoutManager);

        return view;
    }

    private void displayDatabaseInfo() {
        // Создадим и откроем для чтения базу данных
        DB db = new DB(getContext());
        db.open();

        Cursor cursor = db.getAllImportantDates();

        Log.d(TAG, "cursor.getCount() = " + cursor.getCount());
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(ImportantDates.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(ImportantDates.COLUMN_NAME));
                String date = cursor.getString(cursor.getColumnIndex(ImportantDates.COLUMN_DATE));
                int noyear = cursor.getInt(cursor.getColumnIndex(EventsContract.EventsInfo.COLUMN_NOYEAR));
                int notification = cursor.getInt(cursor.getColumnIndex(EventsContract.EventsInfo.COLUMN_NOTIFICATION));

                DateOfEvent dateOfEvent = new DateOfEvent(date);
                importantDateItems.add(new ImportantDatesItem(id, name, dateOfEvent, noyear, notification, ImportantDatesItem.ITEM_TYPE_EVENT));
            }
        }
        Log.d(TAG, "importantDateItems.size = " + importantDateItems.size());

        cursor.close();
        db.close();

        Collections.sort(importantDateItems, new EventsComparator());
        new AddMonthSeparator().add(ImportantDatesItem.class.getName(), importantDateItems);
    }
}

































