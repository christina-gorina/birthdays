package com.example.birthdays;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class BirthdayAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder>{
    ArrayList<BirthdayItem> birthdayItems;
    public static final int ITEM_TYPE_EVENT = 0;
    public static final int ITEM_TYPE_HEADER = 1;

    Context cnt;

    public BirthdayAdapter(ArrayList<BirthdayItem> birthdayItems, Context context){
        this.birthdayItems = birthdayItems;
        this.cnt = context;
    }

    public class EventHolder extends RecyclerView.ViewHolder{
        ImageView photo;
        TextView fullYears;
        TextView name;
        TextView data;
        TextView left;


        public EventHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo);
            fullYears = itemView.findViewById(R.id.fullYears);
            name = itemView.findViewById(R.id.name);
            data = itemView.findViewById(R.id.data);
            left = itemView.findViewById(R.id.left);
        }
    }

    public class MonthOfEventHolder extends RecyclerView.ViewHolder {
        TextView monthLabel;
        public MonthOfEventHolder(@NonNull View itemView) {
            super (itemView);
            monthLabel = itemView.findViewById(R.id.monthLabel);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
            case BirthdayItem.ITEM_TYPE_EVENT:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_item, viewGroup, false);
                return new EventHolder(view);
            case BirthdayItem.ITEM_TYPE_HEADER:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.month_of_event, viewGroup, false);
                return new MonthOfEventHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        switch (birthdayItems.get(position).type) {
            case 0:
                return BirthdayItem.ITEM_TYPE_HEADER;
            case 1:
                return BirthdayItem.ITEM_TYPE_EVENT;
            default:
                return -1;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        BirthdayItem item = birthdayItems.get(position);

        switch (item.type) {
            case BirthdayItem.ITEM_TYPE_EVENT:
                EventHolder eventHolder = (EventHolder) viewHolder;
                eventHolder.photo.setImageBitmap(item.getPhoto());
                eventHolder.fullYears.setText(item.getFullYears());
                eventHolder.name.setText(item.getName());
                eventHolder.data.setText(item.getData());
                eventHolder.left.setText("Осталось дней " + item.getLeft());
                break;

            case BirthdayItem.ITEM_TYPE_HEADER:
                MonthOfEventHolder monthOfEventHolder = (MonthOfEventHolder) viewHolder;

                int bMonth = item.getbMonth();
                String nameOfMonth = "";
                switch (bMonth){
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
                monthOfEventHolder.monthLabel.setText(nameOfMonth);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return birthdayItems.size();
    }


}