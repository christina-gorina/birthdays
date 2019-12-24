package com.example.birthdays;

import java.util.ArrayList;
import java.util.ListIterator;

public class AddMonthSeparator<T extends EventItems>{

    public void add(String type, ArrayList list){
        int lastMounth = -1;
        ListIterator<T> listIterator = list.listIterator();
        while (listIterator.hasNext()){
            int currentMounth = listIterator.next().getDateOfEvent().getMonth();
            if(currentMounth != lastMounth){
                listIterator.previous();
                try {
                    Class clazz = Class.forName(type);
                    Class[] params = {int.class, int.class};
                    listIterator.add((T) clazz.getConstructor(params).newInstance(currentMounth, 0));
                } catch (Throwable e) {
                    System.err.println(e);
                }

                lastMounth = currentMounth;
            }
        }
    }
}
