package com.prt.rezaworkmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    public static final String DEBUG_TAG = "TEST_TAG";
    public static final String NUMBER_KEY = "NUMBER";

    Button countButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TEST_TAG", "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countButton = findViewById(R.id.count_button);
        countButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count(30);
            }
        });
    }

    private void count(int number) {
        WorkManager workManager = WorkManager.getInstance(getBaseContext());
        WorkRequest workRequest = new OneTimeWorkRequest.Builder(TempWorker.class).build();
        workManager.enqueue(workRequest);
        workManager.getWorkInfoByIdLiveData(workRequest.getId()).observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (workInfo.getState().equals(WorkInfo.State.FAILED)) {
                    Log.d(DEBUG_TAG, "onChanged: Work Failed");
                }
            }
        });
    }
}