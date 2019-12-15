package com.example.birthdays;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.birthdays.EventsContract.ImportantDates;

public class EventsDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "events";
    private static final int DB_VERSION = 1;

    public EventsDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_EVENTS_TABLE =
                "CREATE TABLE " + ImportantDates.TABLE_NAME + "(" +
                        ImportantDates.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ImportantDates.COLUMN_NAME + " TEXT NOT NULL," +
                        ImportantDates.COLUMN_DATE + " TEXT NOT NULL, " +
                        ImportantDates.COLUMN_NOYEAR + " BOOL_TYPE, " +
                        ImportantDates.COLUMN_NOTIFICATION + " BOOL_TYPE " +
                        ");";

        // Запускаем создание таблицы
        db.execSQL(SQL_CREATE_EVENTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Запишем в журнал
        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);

        // Удаляем старую таблицу и создаём новую
        db.execSQL("DROP TABLE IF EXISTS " + ImportantDates.TABLE_NAME);
        // Создаём новую таблицу
        onCreate(db);
    }

    public boolean onDelete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ImportantDates.TABLE_NAME, ImportantDates.COLUMN_ID + "=" + id, null);
        return true;
    }
}
