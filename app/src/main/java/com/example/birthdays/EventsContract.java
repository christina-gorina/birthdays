package com.example.birthdays;

import android.provider.BaseColumns;

public class EventsContract {
    private EventsContract(){

    }

    public static final class ImportantDates implements BaseColumns {
        public static final String TABLE_NAME = "important_dates";

        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_NOYEAR = "noyear";
        public static final String COLUMN_NOTIFICATION = "notification";
    }


}
