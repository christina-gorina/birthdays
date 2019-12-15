package com.example.birthdays;

public class DateOfEvent {

    private int dayOfMounth;
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

        //System.out.println("actionDate = " + actionDate);
        parseDate = actionDate.split("-");

        year = hasNumber(parseDate[0]);
        month = hasNumber(parseDate[1]) - 1;
        dayOfMounth = hasNumber(parseDate[2]);

        /*System.out.println("DateOfEvent bYear = " + bYear);
        System.out.println("DateOfEvent bMonth = " + bMonth);
        System.out.println("DateOfEvent dayOfMounth = " + dayOfMounth);*/
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

    public int getDayOfMounth() {
        return dayOfMounth;
    }
    public int getMonth() {
        return month;
    }
    public int getYear() {
        return year;
    }


}
