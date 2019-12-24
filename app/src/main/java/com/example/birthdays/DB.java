package com.example.birthdays;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.birthdays.EventsContract.ImportantDates;
import com.example.birthdays.EventsContract.EventsInfo;
import java.util.UUID;

public class DB {
    private static final String TAG = "DBHelper";
    private static final String DB_NAME = "events";
    private static final int DB_VERSION = 1;

    private static final String SQL_CREATE_IMPORTANT_EVENTS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + ImportantDates.TABLE_NAME + "(" +
                    ImportantDates.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ImportantDates.COLUMN_NAME + " TEXT NOT NULL," +
                    ImportantDates.COLUMN_DATE + " TEXT NOT NULL " +
                    ");";

    private static final String SQL_CREATE_EVENTS_INFO_TABLE =
            "CREATE TABLE IF NOT EXISTS " + EventsInfo.TABLE_NAME + "(" +
                    EventsInfo.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    EventsInfo.COLUMN_EVENTS_ID + " INTEGER NOT NULL," +
                    EventsInfo.COLUMN_TYPE + " TEXT NOT NULL," +
                    EventsInfo.COLUMN_NOYEAR + " INTEGER, " +
                    EventsInfo.COLUMN_NOTIFICATION + " INTEGER, " +
                    EventsInfo.COLUMN_NOTIFICATION_IN_DAY_ID + " TEXT, " +
                    EventsInfo.COLUMN_NOTIFICATION_EARLY_ID + " TEXT, " +
                    EventsInfo.COLUMN_SYNCHRONISATION + " INTEGER " +
                    ");";

    private final Context mCtx;
    private DBHelper mDBHelper;
    private SQLiteDatabase db;

    public DB(Context ctx) {

        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        db = mDBHelper.getReadableDatabase();
        //db = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper != null) mDBHelper.close();
    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllImportantDates() {
        Cursor cursor;
        // Зададим условие для выборки - список столбцов
        String table = ImportantDates.TABLE_NAME + " as ID inner join " + EventsInfo.TABLE_NAME + " as EI on ID._id = EI." + EventsInfo.COLUMN_EVENTS_ID;
        String columns[] = {
                "ID." + ImportantDates.COLUMN_ID + " as _id",
                "ID." + ImportantDates.COLUMN_NAME + " as name",
                "ID." + ImportantDates.COLUMN_DATE + " as date",
                "EI." + EventsInfo.COLUMN_TYPE + " as type",
                "EI." + EventsInfo.COLUMN_NOYEAR + " as noyear",
                "EI." + EventsInfo.COLUMN_NOTIFICATION + " as notification"
        };
        String selection = "EI." + EventsInfo.COLUMN_TYPE + " = ?";  //events_info  important_dates
        String[] selectionArgs = {EventItems.IMPORTANT_DATE_TYPE};
        cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        Log.d(TAG, "cursor.getCount() = " + cursor.getCount());
        return cursor;
    }

    public Cursor getImportantDate(long id, String type){
        Cursor cursor;
        String table = ImportantDates.TABLE_NAME + " as ID inner join " + EventsInfo.TABLE_NAME + " as EI on ID._id = EI." + EventsInfo.COLUMN_EVENTS_ID;
        String columns[] = {
                "ID." + ImportantDates.COLUMN_ID + " as _id",
                "ID." + ImportantDates.COLUMN_NAME + " as name",
                "ID." + ImportantDates.COLUMN_DATE + " as date",
                "EI." + EventsInfo.COLUMN_TYPE + " as type",
                "EI." + EventsInfo.COLUMN_NOYEAR + " as noyear",
                "EI." + EventsInfo.COLUMN_NOTIFICATION + " as notification",
                "EI." + EventsInfo.COLUMN_NOTIFICATION_IN_DAY_ID + " as notification_in_day_id",
                "EI." + EventsInfo.COLUMN_NOTIFICATION_EARLY_ID + " as notification_early_id"
        };
        String selection = EventsInfo.COLUMN_EVENTS_ID + " = ?" + " AND " + EventsInfo.COLUMN_TYPE + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(id), type};
        cursor = db.query(table, columns, selection, selectionArgs, null, null, null);

        return cursor;
    }

    public Cursor getEventInfoOfType(String type){
        Cursor cursor;
        String columns[] = {};
        String selection = EventsInfo.COLUMN_TYPE + " = ?";
        String[] selectionArgs = new String[]{type};
        cursor = db.query(EventsInfo.TABLE_NAME, columns, selection, selectionArgs, null, null, null);

        return cursor;
    }

    public Cursor getEventInfo(long id, String type){
        Cursor cursor;
        String columns[] = {};
        String selection = EventsInfo.COLUMN_EVENTS_ID + " = ?" + " AND " + EventsInfo.COLUMN_TYPE + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(id), type};
        cursor = db.query(EventsInfo.TABLE_NAME, columns, selection, selectionArgs, null, null, null);

        //System.out.println("cursor.count = " + cursor.getCount());
        return cursor;
    }

    public int getNotification(long id, String type){
        Cursor cursor;
        int notification = 0;
        String columns[] = {EventsInfo.COLUMN_NOTIFICATION};
        String selection = EventsInfo.COLUMN_EVENTS_ID + " = ?" + " AND " + EventsInfo.COLUMN_TYPE + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(id), type};

        cursor = db.query(EventsInfo.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        System.out.println("DB.cursor = " + cursor.getCount());

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                notification = cursor.getInt(cursor.getColumnIndex(EventsInfo.COLUMN_NOTIFICATION));
            }
        }

        return notification;
    }

    public int updateNotificationState(long eventId, boolean notification, String type) {
        ContentValues values = new ContentValues();
        values.put(EventsInfo.COLUMN_NOTIFICATION, notification);
        int answer = db.update(EventsInfo.TABLE_NAME, values, EventsInfo.COLUMN_EVENTS_ID + " = ?" + " AND " + EventsInfo.COLUMN_TYPE + " = ?", new String[]{String.valueOf(eventId), type});
        return answer;
    }


    public int updateNotificationInDayId(long eventId, String type, UUID workManagerId) {
        String workManagerIdString = workManagerId.toString();
        ContentValues values = new ContentValues();
        values.put(EventsInfo.COLUMN_NOTIFICATION_IN_DAY_ID, workManagerIdString);
        int answer = db.update(EventsInfo.TABLE_NAME, values, EventsInfo.COLUMN_EVENTS_ID + " = ?" + " AND " + EventsInfo.COLUMN_TYPE + " = ?", new String[]{String.valueOf(eventId), type});
        return answer;
    }

    public int getIdEventInfo(long id, String type){
        Cursor cursor;
        int idEventInfo = 0;
        String columns[] = {EventsInfo.COLUMN_ID};
        String selection = EventsInfo.COLUMN_EVENTS_ID + " = ?" + " AND " + EventsInfo.COLUMN_TYPE + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(id), type};

        cursor = db.query(EventsInfo.TABLE_NAME, columns, selection, selectionArgs, null, null, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                idEventInfo = cursor.getInt(cursor.getColumnIndex(EventsInfo.COLUMN_ID));
            }
        }

        return idEventInfo;
    }

    public String getNotificationInDayId(long id, String type){
        Cursor cursor;
        String result = "";
        String columns[] = {EventsInfo.COLUMN_NOTIFICATION_IN_DAY_ID};
        String selection = EventsInfo.COLUMN_EVENTS_ID + " = ?" + " AND " + EventsInfo.COLUMN_TYPE + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(id), type};

        cursor = db.query(EventsInfo.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        System.out.println("DB.cursor = " + cursor.getCount());

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                result = cursor.getString(cursor.getColumnIndex(EventsInfo.COLUMN_NOTIFICATION_IN_DAY_ID));
            }
        }

        return result;
    }

    public int updateNotificationEarlyId(long eventId, String type, UUID workManagerId) {
        String workManagerIdString = workManagerId.toString();
        ContentValues values = new ContentValues();
        values.put(EventsInfo.COLUMN_NOTIFICATION_EARLY_ID, workManagerIdString);
        int answer = db.update(EventsInfo.TABLE_NAME, values, EventsInfo.COLUMN_EVENTS_ID + " = ?" + " AND " + EventsInfo.COLUMN_TYPE + " = ?", new String[]{String.valueOf(eventId), type});
        return answer;
    }

    public int updateStartSynchronization() {
        ContentValues values = new ContentValues();
        values.put(EventsInfo.COLUMN_SYNCHRONISATION, 0);
        int answer = db.update(EventsInfo.TABLE_NAME, values, null, null);
        return answer;
    }

    public int updateEventSynchronization(long eventId, String type) {
        ContentValues values = new ContentValues();
        values.put(EventsInfo.COLUMN_SYNCHRONISATION, 1);
        int answer = db.update(EventsInfo.TABLE_NAME, values, EventsInfo.COLUMN_EVENTS_ID + " = ?" + " AND " + EventsInfo.COLUMN_TYPE + " = ?", new String[]{String.valueOf(eventId), type});
        return answer;
    }

    public String getNotificationEarlyId(long id, String type){
        Cursor cursor;
        String result = "";
        String columns[] = {EventsInfo.COLUMN_NOTIFICATION_EARLY_ID};
        String selection = EventsInfo.COLUMN_EVENTS_ID + " = ?" + " AND " + EventsInfo.COLUMN_TYPE + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(id), type};

        cursor = db.query(EventsInfo.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        System.out.println("DB.cursor = " + cursor.getCount());

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                result = cursor.getString(cursor.getColumnIndex(EventsInfo.COLUMN_NOTIFICATION_EARLY_ID));
            }
        }

        return result;
    }

    // добавить запись в DB_TABLE
    public long addImportantDate(String name, String date) {
        ContentValues values = new ContentValues();
        values.put(ImportantDates.COLUMN_NAME, name);
        values.put(ImportantDates.COLUMN_DATE, date);
        long rowId = db.insert(ImportantDates.TABLE_NAME, null, values);
        return rowId;
    }

    public long addEventInfo(long eventId, String type, int noyear, int notification) {
        ContentValues values = new ContentValues();
        values.put(EventsInfo.COLUMN_EVENTS_ID, eventId);
        values.put(EventsInfo.COLUMN_TYPE, type);
        values.put(EventsInfo.COLUMN_NOYEAR, noyear);
        values.put(EventsInfo.COLUMN_NOTIFICATION, notification);
        long rowId = db.insert(EventsInfo.TABLE_NAME, null, values);
        return rowId;
    }

    // удалить запись из DB_TABLE
    public boolean deleteImportantDateWithEventInfo(long id, String type) {
        db.beginTransaction();
        try {
            db.delete(ImportantDates.TABLE_NAME, ImportantDates.COLUMN_ID + " = " + id, null);
            db.setTransactionSuccessful();
            db.delete(EventsInfo.TABLE_NAME, EventsInfo.COLUMN_EVENTS_ID + " = ?" + " AND " + EventsInfo.COLUMN_TYPE + " = ?", new String[]{String.valueOf(id), type});
        }
        catch (SQLException e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
            return true;
        }
    }

    public int deleteEventSynchronization() {
        int answer = db.delete(EventsInfo.TABLE_NAME, EventsInfo.COLUMN_SYNCHRONISATION + " = ?" + " AND " + EventsInfo.COLUMN_TYPE + " = ?", new String[]{String.valueOf(0), EventItems.CONTACT_TYPE});
        return answer;
    }

    public void beginTransaction() {
        db.beginTransaction();
    }

    public void setTransactionSuccessful() {
        db.setTransactionSuccessful();
    }

    public void endTransaction() {
        db.endTransaction();
    }

    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            System.out.println("onCreate SQLiteDatabase");
            db.execSQL(SQL_CREATE_IMPORTANT_EVENTS_TABLE);
            db.execSQL(SQL_CREATE_EVENTS_INFO_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Запишем в журнал
            Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);

            // Удаляем старую таблицу и создаём новую
            db.execSQL("DROP TABLE IF EXISTS " + ImportantDates.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + EventsInfo.TABLE_NAME);
            // Создаём новую таблицу
            onCreate(db);
        }
    }
}




