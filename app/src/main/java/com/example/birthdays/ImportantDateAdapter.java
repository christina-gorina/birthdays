package com.example.birthdays;

import android.content.Context;
import android.content.DialogInterface;
import android.database.SQLException;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.util.ArrayList;
import java.util.UUID;

public class ImportantDateAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder>{
    private static final String TAG = "ImportantDateAdapter";
    private static final String IMPORTANT_DATE = EventItems.IMPORTANT_DATE_TYPE;
    private static final int COUNT_OF_DAYS_BEFORE_NOTIFICATION = 3; // За сколько дней напомнить о дате
    private static final int TIME_OF_NOTIFICATION = 8; // В 24 формате
    private long idEventInfo;
    Context cnt;
    ArrayList<ImportantDatesItem> importantDateItems;

    public ImportantDateAdapter(ArrayList<ImportantDatesItem> importantDateItems , Context context) {
        this.cnt = context;
        this.importantDateItems = importantDateItems;
    }

    public static class ImportantDateHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView data;
        TextView left;
        Button delete;
        ToggleButton notification;

        public ImportantDateHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            data = itemView.findViewById(R.id.data);
            delete = itemView.findViewById(R.id.delete);
            notification = itemView.findViewById(R.id.notification);
            left = itemView.findViewById(R.id.left);
        }
    }

    public static class MonthOfEventHolder extends RecyclerView.ViewHolder {
        TextView monthLabel;
        public MonthOfEventHolder(@NonNull View itemView) {
            super (itemView);
            monthLabel = itemView.findViewById(R.id.monthLabel);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
            case ImportantDatesItem.ITEM_TYPE_EVENT:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.importantdate_item, viewGroup, false);
                return new ImportantDateHolder(view);
            case ImportantDatesItem.ITEM_TYPE_HEADER:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.month_of_event, viewGroup, false);
                return new MonthOfEventHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        switch (importantDateItems.get(position).templateType) {
            case 0:
                return ImportantDatesItem.ITEM_TYPE_HEADER;
            case 1:
                return ImportantDatesItem.ITEM_TYPE_EVENT;
            default:
                return -1;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ImportantDatesItem item = importantDateItems.get(position);
        switch (item.templateType) {

            case ImportantDatesItem.ITEM_TYPE_EVENT:
                ImportantDateHolder eventHolder = (ImportantDateHolder) viewHolder;
                eventHolder.name.setText(item.getName());
                eventHolder.data.setText(item.getData());
                eventHolder.left.setText("Осталось дней " + item.getLeft());
                eventHolder.notification.setChecked(item.getNotification() == 1);

                final int index = position;
                final long id = item.getId();
                final String name = item.getName();
                final String unFormatedStringData = item.getUnFormatedStringData();
                Log.d(TAG,"unFormatedStringData = " + unFormatedStringData);
                final String type = IMPORTANT_DATE;

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

                eventHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "-delete Importsnt Date-");
                        Log.d(TAG, "item.getId() = " + id);

                        AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                        alertDialog.setTitle("Удаление");
                        alertDialog.setMessage("Вы хотите удалить запись?");

                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Нет",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Да",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        DB db = new DB(cnt);
                                        db.open();

                                        String notificationInDayId = db.getNotificationInDayId(id, type);
                                        Log.d(TAG, "notificationInDayId = " + notificationInDayId);

                                        String notificationEarlyId = db.getNotificationEarlyId(id, type);
                                        Log.d(TAG, "notificationEarlyId = " + notificationEarlyId);

                                        boolean isDeleted = db.deleteImportantDateWithEventInfo(id, type);
                                        Log.d(TAG, "isDeleted = " + isDeleted);

                                        if(isDeleted) {
                                            try {
                                                importantDateItems.remove(index);

                                                if ((notificationInDayId != null) && (notificationEarlyId != null)) {
                                                    Log.d(TAG, "notificationInDayUUID = " + UUID.fromString(notificationInDayId));
                                                    Log.d(TAG, "notificationEarlyUUID = " + UUID.fromString(notificationEarlyId));

                                                    new NotificationCreator().deleteNotification(UUID.fromString(notificationInDayId), UUID.fromString(notificationEarlyId));
                                                }

                                                notifyItemRemoved(index);
                                                notifyItemRangeChanged(index, getItemCount());
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        db.close();
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                });
                break;
            case ImportantDatesItem.ITEM_TYPE_HEADER:
                MonthOfEventHolder monthOfEventHolder = (MonthOfEventHolder) viewHolder;

                int dMonth = item.getdMonth();
                monthOfEventHolder.monthLabel.setText(new MounthChoice().mounth(dMonth, cnt));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return importantDateItems.size();
    }
}
