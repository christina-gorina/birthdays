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
    private static final int COUNT_OF_MINUTS_IN_DAY = 1440;
    private static final int COUNT_OF_DAYS_IN_LEAP_YEAR = 366;
    private static final int COUNT_OF_DAYS_IN_NOLEAP_YEAR = 365;
    private static final int COUNT_OF_MINUTS_IN_HOUR = 60;

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
                if(nDay < dateOfEvent.getDayOfMonth()){
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
            if(nDay < dateOfEvent.getDayOfMonth()){
                //System.out.println("Birthday next");
                left = calculateLastDay(dateOfEvent, "next");
            }else{
                left = calculateLastDay(dateOfEvent, "last");
            }
        }else if(dateOfEvent.getMonth() == nMonth && nDay == dateOfEvent.getDayOfMonth()){
            //System.out.println("Birthday today");
            left = 0;
        }else{
            //System.out.println("Birthday last");
            left = calculateLastDay(dateOfEvent,"last");
        }

        if(left >= COUNT_OF_DAYS_IN_NOLEAP_YEAR){
            left = 0;
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


    public int TimeAfterYearInMinutes(){
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        int days = (calendar.isLeapYear(nYear)) ? COUNT_OF_DAYS_IN_LEAP_YEAR : COUNT_OF_DAYS_IN_NOLEAP_YEAR;
        return days * COUNT_OF_MINUTS_IN_DAY;
    }

    public int MinutesBeforeDayEnd(){
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minut = now.get(Calendar.MINUTE);
        return COUNT_OF_MINUTS_IN_DAY - (hour * COUNT_OF_MINUTS_IN_HOUR + minut);
    }

    public int MinutesFromDayStart(){
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minut = now.get(Calendar.MINUTE);
        return hour * COUNT_OF_MINUTS_IN_HOUR + minut;
    }
}
