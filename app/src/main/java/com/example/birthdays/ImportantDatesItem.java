package com.example.birthdays;

public class ImportantDatesItem  implements EventItems {
    private long id;
    private String name;
    private String date;
    private int dMonth;
    private int noyear;
    private int notification;
    private String left;
    private DateOfEvent dateOfEvent;
    public int templateType;
    public static final int ITEM_TYPE_HEADER = 0;
    public static final int ITEM_TYPE_EVENT = 1;
    private static final String TAG = "ImportantDatesItem";

    public ImportantDatesItem(){}

    public ImportantDatesItem(int dMonth, int templateType)
    {
        this.dMonth = dMonth;
        this.templateType = templateType;
    }

    public ImportantDatesItem(long id, String name, DateOfEvent dateOfEvent, int noyear, int notification, int templateType){
        this.id = id;
        this.name = name;
        this.noyear = noyear;
        this.notification = notification;
        this.dateOfEvent = dateOfEvent;
        this.templateType = templateType;

        setLeft();
    }

    public long getId() {
        return id;
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

    private void setLeft(){
        CalculateOfDate calculater = new CalculateOfDate();
        int daysBefore = calculater.DaysBefore(dateOfEvent);
        left = String.valueOf(daysBefore);
    }

    public DateOfEvent getDateOfEvent(){
        return dateOfEvent;
    }

    public int getdMonth() {
        return dMonth;
    }

    public int getTemplateType() {
        return templateType;
    }

    public int getNoyear() {
        return noyear;
    }

    public int getNotification() {
        return notification;
    }

    public void setbMonth(int month) {dMonth = month;}

    public void setTemplateType(int type) {templateType = type;}

}
