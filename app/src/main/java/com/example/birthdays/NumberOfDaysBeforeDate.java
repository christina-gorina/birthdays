package com.example.birthdays;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class NumberOfDaysBeforeDate {
    private int dateDay;
    private int dateMounth;
    private int nowDayOfTheYear;
    private Calendar now;
    private int nDay;
    private int nMonth;
    private int nYear;


    public NumberOfDaysBeforeDate(int dateDay, int dateMounth) {
        super();
        this.dateDay = dateDay;
        this.dateMounth = dateMounth;

        now = new GregorianCalendar();
        now.setTime(new Date());
        nowDayOfTheYear = now.get(Calendar.DAY_OF_YEAR);
        nDay = now.get(Calendar.DAY_OF_MONTH);
        nMonth = now.get(Calendar.MONTH);
        nYear = now.get(Calendar.YEAR);

        //System.out.println("NumberOfDaysBeforeDate");
        //System.out.println("dateDay = " + dateDay);
        //System.out.println("dateMounth = " + dateMounth);
    }

    public String DaysBefore(){
        int birthDay;
        String left = "123";
        /*if (nMonth < dateMounth) {
            //System.out.println("Birthday next");
            birthDay = nYear;
            left = String.valueOf(calculateLastDay(birthDay, "next"));

        }else if(nMonth == dateMounth){
            if(nDay < dateDay){
                //System.out.println("Birthday next");
                birthDay = nYear;
                left = String.valueOf(calculateLastDay(birthDay, "next"));
            }else{
                birthDay = nYear + 1;
                left = String.valueOf(calculateLastDay(birthDay, "last"));

            }
        }else if(dateMounth == nMonth && nDay == dateDay){
            //System.out.println("Birthday today");
            left = String.valueOf(0);
        }else{
            //System.out.println("Birthday last");
            birthDay = nYear + 1;
            left = String.valueOf(calculateLastDay(birthDay, "last"));

        }
        */


        return left;
    }

    /*private int calculateLastDay(int bYear, String flag){
        int bDayOfTheYear;
        int dayLeft = 0;
        bDayOfTheYear = bDate.get(Calendar.DAY_OF_YEAR);

        if(flag.equals("next")){
            dayLeft = bDayOfTheYear - nowDayOfTheYear;
        }else if(flag.equals("last")){
            dayLeft = (lastDayOfTheYear - nowDayOfTheYear) + bDayOfTheYear;
        }

        return dayLeft;
    }*/
}
