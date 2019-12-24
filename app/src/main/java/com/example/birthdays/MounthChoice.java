package com.example.birthdays;

import android.content.Context;

public class MounthChoice {

    public String mounth(int month, Context cnt){
        String nameOfMonth = "";
        switch (month){
            case 0:
                nameOfMonth = cnt.getResources().getString(R.string.january);
                break;
            case 1:
                nameOfMonth = cnt.getResources().getString(R.string.february);
                break;
            case 2:
                nameOfMonth = cnt.getResources().getString(R.string.march);
                break;
            case 3:
                nameOfMonth = cnt.getResources().getString(R.string.april);
                break;
            case 4:
                nameOfMonth = cnt.getResources().getString(R.string.may);
                break;
            case 5:
                nameOfMonth = cnt.getResources().getString(R.string.june);
                break;
            case 6:
                nameOfMonth = cnt.getResources().getString(R.string.july);
                break;
            case 7:
                nameOfMonth = cnt.getResources().getString(R.string.august);
                break;
            case 8:
                nameOfMonth = cnt.getResources().getString(R.string.september);
                break;
            case 9:
                nameOfMonth = cnt.getResources().getString(R.string.october);
                break;
            case 10:
                nameOfMonth = cnt.getResources().getString(R.string.november);
                break;
            case 11:
                nameOfMonth = cnt.getResources().getString(R.string.december);
                break;
        }
        return nameOfMonth;
    }
}
