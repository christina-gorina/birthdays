package com.example.birthdays;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.util.concurrent.TimeUnit;

public class EventsWorker extends Worker {
    static final String TAG = "NotificationCreator";
    private static final String IMPORTANT_DATE = EventItems.IMPORTANT_DATE_TYPE;
    private static final String CONTACT_TYPE = EventItems.CONTACT_TYPE;
    private static String CHANNEL_ID = "EventsChannel"; // Идентификатор канала
    private String typeName = "";
    private Context cnt;

    public EventsWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.cnt = context;
    }

    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: start");
        long id = getInputData().getLong("id", 0);
        long idEventInfo = getInputData().getLong("idEventInfo", 0);
        String type = getInputData().getString("type");
        String description = getInputData().getString("description");
        String time = getInputData().getString("time");
        String notificationStart = getInputData().getString("notificationStart");

        if(type.equals(IMPORTANT_DATE)){
            typeName = cnt.getResources().getString(R.string.important_date);
        }else if(type.equals(CONTACT_TYPE)){
            typeName = cnt.getResources().getString(R.string.birthday);
        }

        try {
            TimeUnit.SECONDS.sleep(10);
            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_assignment_black_24dp)
                            .setContentTitle(typeName)
                            .setStyle(new NotificationCompat.InboxStyle()
                                    .addLine(description)
                                    .addLine(time))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(contentIntent)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setColor(Color.BLUE)
                            .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            notificationManager.notify((int)idEventInfo, builder.build());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "doWork: end");

        Log.d(TAG, "Уведомление показалось, повторяем через год");
        NotificationCreator nc = new NotificationCreator(id, idEventInfo, type, cnt);
        nc.createNotification(getInputData(), new CalculateOfDate().TimeAfterYearInMinutes(), notificationStart);

        return Result.success();
    }
}

