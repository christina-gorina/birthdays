package com.example.birthdays;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.Observer;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class CheckContactsCreator {
    private static final String TAG = "CheckContactsCreator";
    private Context ctx;
    private static final int COUNT_OF_MINUTES_IN_HOUR = 60;
    private static final int TIME_OF_FIRST_NOTIFICATION = 8; // В 24 формате
    private static final int TIME_OF_SECOND_NOTIFICATION = 20; // В 24 формате
    private static final int REPEAT_TIME = 12;

    public CheckContactsCreator(Context context){
        this.ctx = context;
    }

    public void repetToCheckContactsList() {
        int delay = REPEAT_TIME * COUNT_OF_MINUTES_IN_HOUR;
        checkStarter(delay);
    }

    public void startToCheckContactsList() {
        // Количество минут до часа уведомления, начиная с начала дня уведомления
        int minutesInDayBeforeHourOfFirstNotification = TIME_OF_FIRST_NOTIFICATION * COUNT_OF_MINUTES_IN_HOUR;
        int minutesInDayBeforeHourOfSecondNotification = TIME_OF_SECOND_NOTIFICATION * COUNT_OF_MINUTES_IN_HOUR;


        // количество минут до конца дня
        int minutesBeforeDayEnd = new CalculateOfDate().MinutesBeforeDayEnd();
        //Log.d(TAG, "minutesBeforeDayEnd: " + minutesBeforeDayEnd);

        // количество минут с начала дня
        int minutesFromDayStart = new CalculateOfDate().MinutesFromDayStart();
        //Log.d(TAG, "minutesFromDayStart: " + minutesFromDayStart);

        int delay = 0;

        if(minutesFromDayStart <= minutesInDayBeforeHourOfFirstNotification){
            delay = minutesInDayBeforeHourOfFirstNotification - minutesFromDayStart;
        }else if((minutesFromDayStart > minutesInDayBeforeHourOfFirstNotification) && (minutesFromDayStart <= minutesInDayBeforeHourOfSecondNotification)){
            delay = minutesInDayBeforeHourOfSecondNotification - minutesFromDayStart;
        }else if(minutesFromDayStart > minutesInDayBeforeHourOfSecondNotification){
            delay = minutesBeforeDayEnd + minutesInDayBeforeHourOfFirstNotification;
        }

        checkStarter(delay);
    }
    public void checkStarter(int delay) {

        Log.d(TAG,"delay = " + (delay/60));
        OneTimeWorkRequest mWorkRequest = new OneTimeWorkRequest
                .Builder(CheckContactsWorker.class)
                 .setInitialDelay(delay, TimeUnit.MINUTES)
                //.setInitialDelay(5, TimeUnit.SECONDS) // для теста
                .build();

        WorkManager.getInstance(ctx).enqueue(mWorkRequest);
        WorkManager.getInstance(ctx).getWorkInfoByIdLiveData(mWorkRequest.getId()).observeForever(new Observer<WorkInfo>() {

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
        });
    }
}
