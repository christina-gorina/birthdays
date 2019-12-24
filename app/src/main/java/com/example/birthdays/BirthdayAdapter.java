package com.example.birthdays;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.util.ArrayList;
import java.util.UUID;

public class BirthdayAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder>{
    ArrayList<BirthdayItem> birthdayItems;
    private static final String TAG = "BirthdayAdapter";
    private long idEventInfo;
    private long idEvent;
    private static final int COUNT_OF_DAYS_BEFORE_NOTIFICATION = 3; // За сколько дней напомнить о дате
    private static final int TIME_OF_NOTIFICATION = 8; // В 24 формате

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
        ToggleButton notification;

        public EventHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo);
            fullYears = itemView.findViewById(R.id.fullYears);
            name = itemView.findViewById(R.id.name);
            data = itemView.findViewById(R.id.data);
            left = itemView.findViewById(R.id.left);
            notification = itemView.findViewById(R.id.notification);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BirthdayItem item = birthdayItems.get(getAdapterPosition());
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(item.getId()));
                    intent.setData(uri);
                    cnt.startActivity(intent);
                }
            });
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
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.birthday_item, viewGroup, false);
                return new EventHolder(view);
            case BirthdayItem.ITEM_TYPE_HEADER:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.month_of_event, viewGroup, false);
                return new MonthOfEventHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        switch (birthdayItems.get(position).templateType) {
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

        switch (item.templateType) {
            case BirthdayItem.ITEM_TYPE_EVENT:
                EventHolder eventHolder = (EventHolder) viewHolder;
                eventHolder.photo.setImageBitmap(item.getPhoto());
                eventHolder.fullYears.setText(item.getFullYears());
                eventHolder.name.setText(item.getName());
                eventHolder.data.setText(item.getData());
                eventHolder.left.setText("Осталось дней " + item.getLeft());
                eventHolder.notification.setChecked(item.getNotification() == 1);
                System.out.println("setChecked = " + item.getNotification());

                final String type = EventItems.CONTACT_TYPE;
                final long id = item.getId();
                idEvent = item.getId();
                final String name = item.getName();
                final String unFormatedStringData = item.getUnFormatedStringData();
                Log.d(TAG,"unFormatedStringData = " + unFormatedStringData);


                eventHolder.notification.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View button) {
                        DB db = new DB(cnt);
                        db.open();
                        boolean notification = ((ToggleButton) button).isChecked();
                        int result = db.updateNotificationState(id, notification, type);
                        if(result != 0){
                            String notificationInDayId = db.getNotificationInDayId(id, type);
                            Log.d(TAG, "notificationInDayId = " + notificationInDayId);

                            String notificationEarlyId = db.getNotificationEarlyId(id, type);
                            Log.d(TAG, "notificationEarlyId = " + notificationEarlyId);

                            idEventInfo = db.getIdEventInfo(id, type);
                            Log.d(TAG, "idEventInfo = " + idEventInfo);

                            if(notification){
                                Log.d(TAG,"Нотификация запрещена");
                                if ((notificationInDayId != null) && (notificationEarlyId != null)) {
                                    Log.d(TAG, "notificationInDayUUID = " + UUID.fromString(notificationInDayId));
                                    Log.d(TAG, "notificationEarlyUUID = " + UUID.fromString(notificationEarlyId));

                                    new NotificationCreator().deleteNotification(UUID.fromString(notificationInDayId), UUID.fromString(notificationEarlyId));
                                }
                            }else{
                                Log.d(TAG,"Нотификация разрешена");
                                Log.d(TAG,"id = " + id);
                                Log.d(TAG,"type = " + type);

                                NotificationCreator nc = new NotificationCreator(id, idEventInfo, type, name, unFormatedStringData, TIME_OF_NOTIFICATION, cnt);
                                nc.createNotificationInDay();
                                nc.createNotificationEarly(COUNT_OF_DAYS_BEFORE_NOTIFICATION);
                            }
                        }
                        db.close();
                    }
                });
                break;

            case BirthdayItem.ITEM_TYPE_HEADER:
                MonthOfEventHolder monthOfEventHolder = (MonthOfEventHolder) viewHolder;
                int bMonth = item.getbMonth();
                monthOfEventHolder.monthLabel.setText(new MounthChoice().mounth(bMonth, cnt));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return birthdayItems.size();
    }

}