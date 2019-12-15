package com.example.birthdays;

import android.graphics.Bitmap;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class BirthdayItem {
    private Bitmap photo;
    private String name;
    private String date;
    private int bDay;
    private int bMonth;
    private int bYear;

    private String fullYears;
    private String left;
    private int nowDayOfTheYear;
    private Calendar now;
    private int nDay;
    private int nMonth;
    private int nYear;
    public int type;

    private DateOfEvent dateOfEvent;

    private int lastDayOfTheYear;

    private Calendar bDate;

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

        now = new GregorianCalendar();
        now.setTime(new Date());
        nowDayOfTheYear = now.get(Calendar.DAY_OF_YEAR);
        nDay = now.get(Calendar.DAY_OF_MONTH);
        nMonth = now.get(Calendar.MONTH);
        nYear = now.get(Calendar.YEAR);

        bDate = new GregorianCalendar();

        Calendar lastDayOfTheYearCalendar;
        lastDayOfTheYearCalendar = new GregorianCalendar();
        lastDayOfTheYearCalendar.set(Calendar.DAY_OF_MONTH, 31);
        lastDayOfTheYearCalendar.set(Calendar.MONTH, 11);
        lastDayOfTheYearCalendar.set(Calendar.YEAR, nYear);
        lastDayOfTheYear = lastDayOfTheYearCalendar.get(Calendar.DAY_OF_YEAR);

        bDate.set(Calendar.YEAR, bYear);
        bDate.set(Calendar.MONTH, bMonth);
        bDate.set(Calendar.DAY_OF_MONTH, bDay);

        setFullYears();
        setLeft();
        setData();
    }

    private void setFullYears(){
        int birthday = -1;
        if(bYear != 0){
            birthday = nYear - bYear;
            if (nMonth < bMonth) {
                --birthday;
            }else if (nMonth == bMonth) {
                if(nDay < bDay){
                    --birthday;
                }
            }
        }
        if(birthday == -1){fullYears = "";}else {fullYears = String.valueOf(birthday);}
    }

    private void setLeft(){

        NumberOfDaysBeforeDate umberOfDaysBeforeDate = new NumberOfDaysBeforeDate(bDay, bMonth);
        int birthDay;

        if (nMonth < bMonth) {
            //System.out.println("Birthday next");
            birthDay = nYear;
            left = String.valueOf(calculateLastDay(birthDay, "next"));

        }else if(nMonth == bMonth){
            if(nDay < bDay){
                //System.out.println("Birthday next");
                birthDay = nYear;
                left = String.valueOf(calculateLastDay(birthDay, "next"));
            }else{
                birthDay = nYear + 1;
                left = String.valueOf(calculateLastDay(birthDay, "last"));

            }
        }else if(bMonth == nMonth && nDay == bDay){
            //System.out.println("Birthday today");
            left = String.valueOf(0);
        }else{
             //System.out.println("Birthday last");
            birthDay = nYear + 1;
            left = String.valueOf(calculateLastDay(birthDay, "last"));

        }
    }
    private int calculateLastDay(int bYear, String flag){
        int bDayOfTheYear;
        int dayLeft = 0;
        bDayOfTheYear = bDate.get(Calendar.DAY_OF_YEAR);

        if(flag.equals("next")){
            dayLeft = bDayOfTheYear - nowDayOfTheYear;
        }else if(flag.equals("last")){
            dayLeft = (lastDayOfTheYear - nowDayOfTheYear) + bDayOfTheYear;
        }

        return dayLeft;
    }


    private void setData(){
        DateFormat df;
        if(bYear != 0){
            df = new SimpleDateFormat("d MMMM yyy");
        }else{
            df = new SimpleDateFormat("dd MMMM");
        }
        date = df.format(bDate.getTime());
        //System.out.println("date: " + date);
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
        return date;
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
