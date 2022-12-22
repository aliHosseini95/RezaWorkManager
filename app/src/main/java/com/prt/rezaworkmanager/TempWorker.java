package com.prt.rezaworkmanager;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.Callable;

import javax.xml.transform.Result;

import androidx.annotation.NonNull;
import androidx.work.RxWorker;
import androidx.work.WorkerParameters;
import io.reactivex.Single;

public class TempWorker extends RxWorker {

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
                }
                return Result.success();
            }
        });
    }
}
