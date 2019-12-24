package com.example.birthdays;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class CheckContactsWorker extends Worker {
    static final String TAG = "CheckContactsCreator";
    private Context cnt;
    public CheckContactsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.cnt = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: start");

        try {
            //TimeUnit.SECONDS.sleep(10);
            new BirthdaysFragment().loadContacts(cnt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "doWork: end");
        Log.d(TAG, "Проверка сработала, повторяем через 12 часов");
        new CheckContactsCreator(cnt).repetToCheckContactsList();
        return Result.success();
    }

}
