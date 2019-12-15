package com.example.birthdays;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalculateOfDate {
    private int nowDayOfTheYear;
    private Calendar now;
    private int nDay;
    private int nMonth;
    private int nYear;

    public CalculateOfDate() {
        super();

        now = new GregorianCalendar();
        now.setTime(new Date());
        nowDayOfTheYear = now.get(Calendar.DAY_OF_YEAR);
        nDay = now.get(Calendar.DAY_OF_MONTH);
        nMonth = now.get(Calendar.MONTH);
        nYear = now.get(Calendar.YEAR);

        //System.out.println("CalculateOfDate");
        //System.out.println("dateDay = " + dateDay);
        //System.out.println("dateMounth = " + dateMounth);
    }

    public int YearAfter(DateOfEvent dateOfEvent){
        int birthday = -1;
        if(dateOfEvent.getYear() != 0){
            birthday = nYear - dateOfEvent.getYear();
            if (nMonth < dateOfEvent.getMonth()) {
                --birthday;
            }else if (nMonth == dateOfEvent.getMonth()) {
                if(nDay < dateOfEvent.getDayOfMounth()){
                    --birthday;
                }
            }
        }
        if(birthday == -1){return -1;}else {return birthday;}

    }

    public int DaysBefore(DateOfEvent dateOfEvent){
        int left;
        if (nMonth < dateOfEvent.getMonth()) {
            //System.out.println("Birthday next");
            left = calculateLastDay(dateOfEvent, "next");

        }else if(nMonth == dateOfEvent.getMonth()){
            if(nDay < dateOfEvent.getDayOfMounth()){
                //System.out.println("Birthday next");
                left = calculateLastDay(dateOfEvent, "next");
            }else{
                left = calculateLastDay(dateOfEvent, "last");
            }
        }else if(dateOfEvent.getMonth() == nMonth && nDay == dateOfEvent.getDayOfMounth()){
            //System.out.println("Birthday today");
            left = 0;
        }else{
            //System.out.println("Birthday last");
            left = calculateLastDay(dateOfEvent,"last");
        }

        return left;
    }

    private int calculateLastDay(DateOfEvent dateOfEvent, String flag){
        int bDayOfTheYear;
        int dayLeft = 0;
        bDayOfTheYear = dateOfEvent.getDayOfYear();

        Calendar lastDayOfTheYearCalendar;
        lastDayOfTheYearCalendar = new GregorianCalendar();
        lastDayOfTheYearCalendar.set(Calendar.DAY_OF_MONTH, 31);
        lastDayOfTheYearCalendar.set(Calendar.MONTH, 11);
        lastDayOfTheYearCalendar.set(Calendar.YEAR, nYear);
        int lastDayOfTheYear = lastDayOfTheYearCalendar.get(Calendar.DAY_OF_YEAR);

        if(flag.equals("next")){
            dayLeft = bDayOfTheYear - nowDayOfTheYear;
        }else if(flag.equals("last")){
            dayLeft = (lastDayOfTheYear - nowDayOfTheYear) + bDayOfTheYear;
        }
        return dayLeft;
    }

}
