package com.prt.rezaworkmanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.RxWorker;
import androidx.work.WorkManager;
import androidx.work.WorkerParameters;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class CountWorker extends RxWorker {

    private final static int NOTIFICATION_ID = 333;
    private final static String CHANNEL_ID = "PRT_CHANNEL";
    private final static String TITLE = "My Notification";
    private final static String CANCEL = "Cancel";

    private NotificationManager notificationManager;
    private final long number;

    private CompositeDisposable compositeDisposable;

    public CountWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        number = workerParams.getInputData().getLong("NUMBER_KEY", -1);
        compositeDisposable = new CompositeDisposable();
    }

    @NonNull
    @Override
    public Single<Result> createWork() {
        setForegroundAsync(createForegroundInfo());

        return Single.create(emitter -> {
            compositeDisposable.add(Completable.fromAction(new Action() {
                @Override
                public void run() throws Exception {
                    for (int i = 0; i < number; i++) {
                        Log.d("TEST_TAG", "For : " + i);
                        Thread.sleep(200);
                    }
                }
            }).subscribe());
        });
    }

    @Override
    public void onStopped() {
        super.onStopped();
        Log.d("TEST_TAG", "onStopped: ");
        compositeDisposable.dispose();
    }

    @NonNull
    private ForegroundInfo createForegroundInfo() {
        Context context = getApplicationContext();
        PendingIntent intent = WorkManager.getInstance(context).createCancelPendingIntent(getId());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(TITLE)
                .setTicker(TITLE)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_delete, CANCEL, intent)
                .build();
        return new ForegroundInfo(NOTIFICATION_ID, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
//        Create channel
    }
}
