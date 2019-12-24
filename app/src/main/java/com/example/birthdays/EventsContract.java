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
    }

    public static final class EventsInfo implements BaseColumns {
        public static final String TABLE_NAME = "events_info";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_EVENTS_ID = "events_id";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_NOYEAR = "noyear";
        public static final String COLUMN_NOTIFICATION = "notification";
        public static final String COLUMN_NOTIFICATION_IN_DAY_ID = "notification_in_day_id";
        public static final String COLUMN_NOTIFICATION_EARLY_ID = "notification_early_id";
        public static final String COLUMN_SYNCHRONISATION = "synchronization";
    }
}
