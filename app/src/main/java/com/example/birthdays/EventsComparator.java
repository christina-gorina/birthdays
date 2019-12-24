package com.example.birthdays;

import java.util.Comparator;

public class EventsComparator<T extends EventItems>  implements Comparator<T>{
    @Override
    public int compare(T b1, T b2) {
        DateOfEvent o1 = b1.getDateOfEvent();
        DateOfEvent o2 = b2.getDateOfEvent();
        //System.out.println("o1 = " + o1.getMonth());
        //System.out.println("o2 = " + o2.getMonth());

        if(o1.getMonth() < o2.getMonth())
            return -1;
        if(o1.getMonth() > o2.getMonth())
            return 1;

        if (o1.getDayOfMonth() < o2.getDayOfMonth())
            return -1;
        if (o1.getDayOfMonth() > o2.getDayOfMonth())
            return 1;

        return 0;
    }
}