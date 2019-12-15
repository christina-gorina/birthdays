package com.example.birthdays;

public class ImportantDatesItem {
    private int id;
    private String name;
    private String date;
    private int noyear;
    private int notification;
    private String left;
    private DateOfEvent dateOfEvent;

    public ImportantDatesItem(int id, String name, DateOfEvent dateOfEvent, int noyear, int notification){
        this.id = id;
        this.name = name;
        this.noyear = noyear;
        this.notification = notification;
        this.dateOfEvent = dateOfEvent;

        setLeft();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return dateOfEvent.toFormatedString();
    }

    public String getLeft() {
        return left;
    }

    private void setLeft(){
        CalculateOfDate calculater = new CalculateOfDate();
        int daysBefore = calculater.DaysBefore(dateOfEvent);
        left = String.valueOf(daysBefore);
    }

    public int getNoyear() {
        return noyear;
    }

    public int getNotification() {
        return notification;
    }

}
