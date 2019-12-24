package com.example.birthdays;

import android.graphics.Bitmap;

public class BirthdayItem implements EventItems, Cloneable {
    private long id;
    private Bitmap photo;
    private String name;
    private String date;
    private int bDay;
    private int bMonth;
    private int bYear;
    private String fullYears;
    private String left;
    public int templateType;
    private int notification;
    private int noyear;
    private DateOfEvent dateOfEvent;

    public static final int ITEM_TYPE_HEADER = 0;
    public static final int ITEM_TYPE_EVENT = 1;

    public BirthdayItem() {}

    public BirthdayItem(int bMonth, int templateType)
    {
        this.bMonth = bMonth;
        this.templateType = templateType;
    }

    public BirthdayItem(long id, Bitmap photo, String name, DateOfEvent dateOfEvent, int noyear, int notification, int templateType){
       // super(dateOfEvent);
        this.id = id;
        this.photo = photo;
        this.name = name;
        this.bDay = dateOfEvent.getDayOfMonth();
        this.bMonth = dateOfEvent.getMonth();
        this.bYear = dateOfEvent.getYear();
        this.templateType = templateType;
        this.dateOfEvent = dateOfEvent;
        this.notification = notification;
        this.noyear = notification;

        setFullYears();
        setLeft();
    }

    private void setFullYears(){
        CalculateOfDate calculate = new CalculateOfDate();
        int yearAfter = calculate.YearAfter(dateOfEvent);
        if(yearAfter == -1){fullYears = "";}else{fullYears = String.valueOf(yearAfter);}
    }

    private void setLeft(){
        CalculateOfDate calculater = new CalculateOfDate();
        int daysBefore = calculater.DaysBefore(dateOfEvent);
        left = String.valueOf(daysBefore);
    }

    public long getId() {
        return id;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public String getFullYears() {
        return fullYears;
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return dateOfEvent.toFormatedString();
    }

    public String getUnFormatedStringData() {
        return dateOfEvent.toUnFormatedString();
    }

    public String getLeft() {
        return left;
    }

    public DateOfEvent getDateOfEvent(){
        return dateOfEvent;
    }

    public int getNotification() {
        return notification;
    }

    public int getNoyear() {
        return noyear;
    }

    public int getbMonth() {
        return bMonth;
    }

    public void setbMonth(int month) {this.bMonth = month;}

    public void setTemplateType(int type) {this.templateType = type;}
}
