package com.example.birthdays;

import android.graphics.Bitmap;

public class BirthdayItem {
    private Bitmap photo;
    private String name;
    private String date;
    private int bDay;
    private int bMonth;
    private int bYear;
    private String fullYears;
    private String left;
    public int type;

    private DateOfEvent dateOfEvent;

    public static final int ITEM_TYPE_HEADER = 0;
    public static final int ITEM_TYPE_EVENT = 1;

    public BirthdayItem(int bMonth, int type)
    {
        this.bMonth = bMonth;
        this.type = type;
    }

    public BirthdayItem(Bitmap photo, String name, DateOfEvent dateOfEvent, int type){
        this.photo = photo;
        this.name = name;
        this.bDay = dateOfEvent.getDayOfMounth();
        this.bMonth = dateOfEvent.getMonth();
        this.bYear = dateOfEvent.getYear();
        this.type = type;
        this.dateOfEvent = dateOfEvent;

       // Log.i("MyLog", "date = " + date);
        //System.out.println("================");

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

    public int getbMonth() {
        return bMonth;
    }


    public String getLeft() {
        return left;
    }

    public DateOfEvent getDateOfEvent(){
        return dateOfEvent;
    }
}
