package com.prt.rezaworkmanager;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.RxWorker;
import androidx.work.WorkManager;
import androidx.work.WorkerParameters;
import androidx.work.impl.utils.futures.SettableFuture;
import io.reactivex.Single;

public class TempWorker extends RxWorker {

    private static final String CHANNEL_ID = "R_WORK_MANAGER";
    private final int number;

    public TempWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        number = workerParams.getInputData().getInt(MainActivity.NUMBER_KEY, -1);
    }

    @NonNull
    @Override
    public Single<Result> createWork() {
        return Single.fromCallable(new Callable<Result>() {
            @Override
            public Result call() throws Exception {
                if (number <= 0) {
                    return Result.failure();
                }
                for (int i = 0; i < number; i++) {
                    Log.d(MainActivity.DEBUG_TAG, "for : " + i);
                    Thread.sleep(200);
                }
                return Result.success();
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @NonNull
    @Override
    public ListenableFuture<ForegroundInfo> getForegroundInfoAsync() {
        SettableFuture<ForegroundInfo> future = SettableFuture.create();
        future.set(createForegroundInfo());
        return future;
    }

    private ForegroundInfo createForegroundInfo() {
        PendingIntent pendingIntent = WorkManager.getInstance(getApplicationContext()).createCancelPendingIntent(getId());
        createChannel();
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext()
                        , CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Reza Work Manager")
                        .setContentText("Content Text")
                        .setOngoing(true)
                        .addAction(android.R.drawable.ic_menu_close_clear_cancel
                                , "Cancel", pendingIntent)
                        .setChannelId(CHANNEL_ID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationBuilder.setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_IMMUTABLE));
        }
        return new ForegroundInfo(333, notificationBuilder.build());
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "RezaWorkManagerChannel";
            String description = "Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
