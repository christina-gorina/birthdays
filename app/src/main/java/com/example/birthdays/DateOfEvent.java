package com.example.birthdays;

import android.util.Log;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateOfEvent {
    private static final String TAG = "DateOfEvent";
    private int dayOfMonth;
    private int month;
    private int year;

    public DateOfEvent(String dateOfEvent){

        String parseDate[];
        String actionDate;

        String settingDate = String.valueOf(dateOfEvent);
        char zeroChar = settingDate.charAt(0);
        if(zeroChar == '-'){
            actionDate = '0' + settingDate.substring(1, settingDate.length());
        }else{
            actionDate = settingDate;
        }

        parseDate = actionDate.split("-");

        year = hasNumber(parseDate[0]);
        month = hasNumber(parseDate[1]) - 1;
        dayOfMonth = hasNumber(parseDate[2]);

        /*System.out.println("DateOfEvent bYear = " + bYear);
        System.out.println("DateOfEvent bMonth = " + bMonth);
        System.out.println("DateOfEvent dayOfMonth = " + dayOfMonth);*/
    }

    public Calendar getCalendarDate(){
        Calendar bDate;
        bDate = new GregorianCalendar();
        bDate.set(Calendar.YEAR, getYear());
        bDate.set(Calendar.MONTH, getMonth());
        bDate.set(Calendar.DAY_OF_MONTH, getDayOfMonth());
        return bDate;
    }

    public String toUnFormatedString(){
        Log.d(TAG,"toUnFormatedString");
        String date = "";
        String sMonth = "";
        int month = getMonth() + 1;
        if(month <= 9){
            sMonth = "0" + month;
        }else{
            sMonth = "" + month;
        }

        Log.d(TAG,"getYear() = " + getYear());
        Log.d(TAG,"getMonth() = " + sMonth);
        Log.d(TAG,"getDayOfMonth() = " + getDayOfMonth());
        if(getYear() != 0){
            date = getYear() + "-" + sMonth + "-" + getDayOfMonth();
        }else{
            date = "--" + sMonth + "-" + getDayOfMonth();
        }
        Log.d(TAG,"date = " + date);
        return date;
    }

    public String toFormatedString(){
        DateFormat df;
        if(getYear() != 0){
            df = new SimpleDateFormat("d MMMM yyy");
        }else{
            df = new SimpleDateFormat("dd MMMM");
        }
        String date = df.format(getCalendarDate().getTime());
        return date;
    }

    public int getDayOfYear(){
        Calendar bDate = new GregorianCalendar();
        bDate.set(Calendar.YEAR, year);
        bDate.set(Calendar.MONTH, month);
        bDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return bDate.get(Calendar.DAY_OF_YEAR);
    }

    private int hasNumber(String number){
        int hasNumber;
        if(number.equals("")){
            hasNumber = 0;
        }else{
            hasNumber = Integer.parseInt(number);
        }
        return hasNumber;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }
    public int getMonth() {
        return month;
    }
    public int getYear() {
        return year;
    }
}
