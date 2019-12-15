package com.example.birthdays;

import android.graphics.Bitmap;

public class ImportantDatesItem {
    private int id;
    private String name;
    private String date;
    private int noyear;
    private int notification;

    public ImportantDatesItem(int id, String name, String date, int noyear, int notification){
        this.id = id;
        this.name = name;
        this.date = date;
        this.noyear = noyear;
        this.notification = notification;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return date;
    }

    public int getNoyear() {
        return noyear;
    }

    public int getNotification() {
        return notification;
    }

}
