package com.example.birthdays;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class NotificationCreator {
    private static final String TAG = "NotificationCreator";
    private static final int COUNT_OF_MINUTES_IN_HOUR = 60;
    private static final int COUNT_OF_MINUTES_IN_DAY = 1440;
    private static final String NOTIFICATION_IN_DAY = "in_day";
    private static final String NOTIFICATION_EARLY = "early";
    private Context cnt;
    private String name;
    private String type;
    private long id;
    private long idEventInfo;
    private int daysBefore;
    private int timeAfterYearInMinutes;
    private int minutesBeforeDayEnd;
    private int minutesFromDayStart;
    private int minutesInDayBeforeHourOfNotification;

    public NotificationCreator(){}

    public NotificationCreator(long id, long idEventInfo, String type, Context context){
        this.id = id;
        this.idEventInfo = idEventInfo;
        this.cnt = context;
        this.type = type;

        Log.d(TAG, "id = " + id);
        Log.d(TAG, "idEventInfo = " + idEventInfo);
        Log.d(TAG, "type = " + type);
    }

    public NotificationCreator(long id, long idEventInfo, String type, String name, String date, int time, Context context){
        this.cnt = context;
        this.type = type;
        this.id = id;
        this.idEventInfo = idEventInfo;
        this.name = name;

        // Количество минут до часа уведомления, начиная с начала дня уведомления
        minutesInDayBeforeHourOfNotification = time * COUNT_OF_MINUTES_IN_HOUR;
        //Log.d(TAG, "minutesInDayBeforeHourOfNotification: " + minutesInDayBeforeHourOfNotification);

        Log.d(TAG, "id = " + id);
        Log.d(TAG, "idEventInfo = " + idEventInfo);
        Log.d(TAG, "name = " + name);
        Log.d(TAG, "date = " + date);
        Log.d(TAG, "type = " + type);

        DateOfEvent dateOfEvent = new DateOfEvent(date);
        CalculateOfDate calculater = new CalculateOfDate();
        daysBefore = calculater.DaysBefore(dateOfEvent);
        Log.d(TAG, "daysBefore: " + daysBefore);

        // через сколько дней повторять
        timeAfterYearInMinutes = calculater.TimeAfterYearInMinutes();
        //Log.d(TAG, "timeAfterYearInMinutes: " + timeAfterYearInMinutes);

        // количество минут до конца дня
        minutesBeforeDayEnd = calculater.MinutesBeforeDayEnd();
        //Log.d(TAG, "minutesBeforeDayEnd: " + minutesBeforeDayEnd);

        // количество минут с начала дня
        minutesFromDayStart = calculater.MinutesFromDayStart();
        //Log.d(TAG, "minutesFromDayStart: " + minutesFromDayStart);
    }

    public void createNotificationInDay() {
        Log.d(TAG, "--createNotificationInDay--");
        int delayNotificationInIDDay;
        // Высчитываем задержку, через сколько показать уведомление, в день события
        if(daysBefore == 0){ // событие сегодня
            Log.d(TAG, "InDay событие сегодня");
            delayNotificationInIDDay = notificationTodayInTime(minutesFromDayStart, minutesInDayBeforeHourOfNotification);
        }else{
            Log.d(TAG, "InDay событие НЕ сегодня");
            delayNotificationInIDDay = minutesBeforeDayEnd + ((daysBefore - 1) * COUNT_OF_MINUTES_IN_DAY) + minutesInDayBeforeHourOfNotification; // количество минут до конца дня - (осталось дней, за исключением дня наступления события) + количество минут до часа уведомления в день уведомления
        }
        Log.d(TAG, "delayNotificationInIDDay = " + delayNotificationInIDDay);

        // id в базе данных будет совпадать с id уведомления NotificationManagerCompat, не путать с id WorkManager
        Data notificationDataIDToday = new Data.Builder()
                .putLong("id", id)
                .putLong("idEventInfo", idEventInfo)
                .putString("type", type)
                .putString("description", name)
                .putString("notificationStart", NOTIFICATION_IN_DAY)
                .putString("time", cnt.getResources().getString(R.string.event_today))
                .putString("msg", "onChanged InDay: ")
                .build();

        // Создаем уведомление о событии в день события
        // ID в названиях переменных сокращение от Important Day
        Log.d(TAG, "Создаем уведомление о событии в день события");
        createNotification(notificationDataIDToday, delayNotificationInIDDay, NOTIFICATION_IN_DAY);

    }

    public void addNotificationInDayIdInDB(UUID workRequestId) {
        Log.d(TAG, "-addNotificationInDayIdInDB-");
        Log.d(TAG, "id = " + id);
        DB db = new DB(cnt);
        db.open();
        db.updateNotificationInDayId(id, type, workRequestId);
        db.close();
    }

    public void createNotificationEarly(int count_of_days_before_notification) {
        Log.d(TAG, "--createNotificationEarly--");
        int delayNotificationInEarlyDay;
        // Высчитываем задержку, через сколько показать уведомление, заранее до события
        int daysBeforeEarlyNotification = daysBefore - count_of_days_before_notification;

        if(daysBeforeEarlyNotification == 0){ // уведомить сегодня
            Log.d(TAG, "InEarlyDay уведомить сегодня");
            delayNotificationInEarlyDay = notificationTodayInTime(minutesFromDayStart, minutesInDayBeforeHourOfNotification);
        }else if(daysBeforeEarlyNotification > 0){// уведомить не сегодня
            delayNotificationInEarlyDay = minutesBeforeDayEnd + ((daysBeforeEarlyNotification - 1) * COUNT_OF_MINUTES_IN_DAY) + minutesInDayBeforeHourOfNotification; // количество минут до конца дня - (осталось дней, за исключением дня наступления раннего оповещения) + количество минут до часа уведомления в день уведомления
            Log.d(TAG, "InEarlyDay уведомить НЕ сегодня");
        }else{
            // до события меньше дней, чем COUNT_OF_DAYS_BEFORE_NOTIFICATION , поэтому ставим задержку в год
            delayNotificationInEarlyDay = timeAfterYearInMinutes;
            Log.d(TAG, "InEarlyDay уведомить через год за count_of_days_before_notification до события");
        }
        Log.d(TAG, "delayNotificationInEarlyDay = " + delayNotificationInEarlyDay);

        // id в базе данных будет совпадать с id уведомления NotificationManagerCompat, не путать с id WorkManager
        Data notificationDataEarleDay = new Data.Builder()
                .putLong("id", id)
                .putLong("idEventInfo", idEventInfo)
                .putString("type", type)
                .putString("description", name)
                .putString("notificationStart", NOTIFICATION_EARLY)
                .putString("time", cnt.getResources().getString(R.string.days_before_event) + ": " + count_of_days_before_notification)
                .putString("msg", "onChanged EarlyDay: ")
                .build();

        // Создаем уведомление о событии заранее
        Log.d(TAG, "Создаем уведомление о событии заранее");
        createNotification(notificationDataEarleDay, delayNotificationInEarlyDay, NOTIFICATION_EARLY);
    }

    public void addNotificationEarlyIdInDB(UUID workRequestId) {
        Log.d(TAG, "-addNotificationEarlyIdInDB-");
        Log.d(TAG, "id = " + id);
        DB db = new DB(cnt);
        db.open();
        db.updateNotificationEarlyId(id, type, workRequestId);
        db.close();
    }

    private int notificationTodayInTime(int minutesFromDayStart, int minutesInDayBeforeHourOfNotification) {
        int delay;
        if(minutesFromDayStart > minutesInDayBeforeHourOfNotification){ // событие сегодня, а время уведомления уже прошло
            Log.d(TAG, "notificationTodayInTime событие сегодня, а время уведомления уже прошло");
            delay = 1;
        }else{
            Log.d(TAG, "событие сегодня, а время уведомления еще не наступило");
            delay = minutesInDayBeforeHourOfNotification - minutesFromDayStart;
        }
        return delay;
    }

    public void deleteNotification(UUID notificationInDay, UUID notificationEarly) {
        WorkManager.getInstance(cnt).cancelWorkById(notificationInDay);
        WorkManager.getInstance(cnt).cancelWorkById(notificationEarly);
        getStateNotification(notificationInDay, "InDay");
        getStateNotification(notificationEarly, "Early");
    }

    public UUID createNotification(Data data, int delay, String notificationStart) {
        final String mMsg = data.getString("msg");
        Log.d(TAG, "createNotification delay = " + delay);

        OneTimeWorkRequest myWorkRequestOneTime = new OneTimeWorkRequest
                .Builder(EventsWorker.class)
                .setInputData(data)
                .setInitialDelay(delay, TimeUnit.MINUTES)
                //.setInitialDelay(5, TimeUnit.SECONDS) // для теста
                .build();

        WorkManager.getInstance(cnt).enqueue(myWorkRequestOneTime);
        Log.d(TAG, "myWorkRequestOneTime.getId() = " + myWorkRequestOneTime.getId());

        if(notificationStart.equals(NOTIFICATION_EARLY)){
            addNotificationEarlyIdInDB(myWorkRequestOneTime.getId());
        }else if(notificationStart.equals(NOTIFICATION_IN_DAY)){
            addNotificationInDayIdInDB(myWorkRequestOneTime.getId());
        }
        /*WorkManager.getInstance(cnt).getWorkInfoByIdLiveData(myWorkRequestOneTime.getId()).observeForever(new Observer<WorkInfo>() {

            @Override
            public void onChanged(WorkInfo workInfo) {
                // Log.d(TAG,message + workInfo.getState());

                if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                    Log.d(TAG,"WorkInfo.State.SUCCEEDED");
                }
                if (workInfo.getState() == WorkInfo.State.ENQUEUED) {
                    Log.d(TAG,"WorkInfo.State.ENQUEUED");
                }
                if (workInfo.getState() == WorkInfo.State.RUNNING) {
                    Log.d(TAG,"WorkInfo.State.RUNNING");
                }
                if (workInfo.getState() == WorkInfo.State.FAILED) {
                    Log.d(TAG,"WorkInfo.State.FAILED");
                }
                if (workInfo.getState() == WorkInfo.State.BLOCKED) {
                    Log.d(TAG,"WorkInfo.State.BLOCKED");
                }
                if (workInfo.getState() == WorkInfo.State.CANCELLED) {
                    Log.d(TAG,"WorkInfo.State.CANCELLED");
                }
            }
        });*/

        // getStateNotification требует исправить ошибку с вызовом в фоновом потоке, для этого надо тесты писать и в них правила задавать InstantTaskExecutorRule

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Многоразовое уведомление прошлый тестовый вариант. Проблема в том, что звонит и не звонит когда хочет. Плюс каждый год надо задержку пересчитывать из-за високосного года
        /*Log.d(TAG, "createNotification delay = " + delay);
        final String message = msg;
        PeriodicWorkRequest notificationInDayID = new PeriodicWorkRequest
                //.Builder(EventsWorker.class, (repeat + 5), TimeUnit.MINUTES, repeat, TimeUnit.MINUTES)
                .Builder(EventsWorker.class, 16, TimeUnit.MINUTES, 15, TimeUnit.MINUTES)
                //.setInputData(data)
                //.setInitialDelay(delay, TimeUnit.MINUTES)
                .setInitialDelay(1, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(cnt).enqueue(notificationInDayID);
        WorkManager.getInstance(cnt).getWorkInfoByIdLiveData(notificationInDayID.getId()).observeForever(new Observer<WorkInfo>() {

            @Override
            public void onChanged(WorkInfo workInfo) {
               // Log.d(TAG,message + workInfo.getState());

                if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                    Log.d(TAG,"WorkInfo.State.SUCCEEDED");
                }
                if (workInfo.getState() == WorkInfo.State.ENQUEUED) {
                    Log.d(TAG,"WorkInfo.State.ENQUEUED");
                }
                if (workInfo.getState() == WorkInfo.State.RUNNING) {
                    Log.d(TAG,"WorkInfo.State.RUNNING");
                }
                if (workInfo.getState() == WorkInfo.State.FAILED) {
                    Log.d(TAG,"WorkInfo.State.FAILED");
                }
                if (workInfo.getState() == WorkInfo.State.BLOCKED) {
                    Log.d(TAG,"WorkInfo.State.BLOCKED");
                }
                if (workInfo.getState() == WorkInfo.State.CANCELLED) {
                    Log.d(TAG,"WorkInfo.State.CANCELLED");
                }
            }
        });*/

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return myWorkRequestOneTime.getId();
    }

    public void getStateNotification(UUID notificationEarlyUUID, String type) {
        Log.d(TAG,"getStateNotification type = " + type);
        WorkManager.getInstance(cnt).getWorkInfoByIdLiveData(notificationEarlyUUID).observeForever(new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                //Log.d(TAG,mMsg + workInfo.getState());

                if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                    Log.d(TAG,"WorkInfo.State.SUCCEEDED");
                }
                if (workInfo.getState() == WorkInfo.State.ENQUEUED) {
                    Log.d(TAG,"WorkInfo.State.ENQUEUED");
                }
                if (workInfo.getState() == WorkInfo.State.RUNNING) {
                    Log.d(TAG,"WorkInfo.State.RUNNING");
                }
                if (workInfo.getState() == WorkInfo.State.FAILED) {
                    Log.d(TAG,"WorkInfo.State.FAILED");
                }
                if (workInfo.getState() == WorkInfo.State.BLOCKED) {
                    Log.d(TAG,"WorkInfo.State.BLOCKED");
                }
                if (workInfo.getState() == WorkInfo.State.CANCELLED) {
                    Log.d(TAG,"WorkInfo.State.CANCELLED");
                }
            }
        });
    }
}
