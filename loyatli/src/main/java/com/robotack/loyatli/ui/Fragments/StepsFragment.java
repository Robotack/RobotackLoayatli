package com.robotack.loyatli.ui.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.robotack.loyatli.R;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class StepsFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;

    private TextView submitCLick;
    private TextView tvToday;
    String todaySteps = "";
    private GoogleApiClient mGoogleApiClient;
    CircularProgressBar circularProgressBar;
    ArrayList<String> steps = new ArrayList<>();
    String stepsCount;
    LinearLayout getCoinsLinearLayout;
    private Handler handler;
    private int delay = 3000;

    Runnable runnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_steps, container, false);
        tvToday = (TextView) rootView.findViewById(R.id.tvToday);
        submitCLick = (TextView) rootView.findViewById(R.id.submitCLick);
        circularProgressBar = rootView.findViewById(R.id.circularProgressbar);
        circularProgressBar.setProgressBarColor(Color.parseColor("#306095"));
        circularProgressBar.setBackgroundProgressBarColor(Color.parseColor("#CC306095"));
        setupFitness();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    101);
        }
        return rootView;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        GetStepCountTask task = new GetStepCountTask();
        task.execute();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("HistoryAPI", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }

    }

    private void setupFitness() {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .build();
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(getActivity()), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(getActivity()), fitnessOptions);
        } else {
            subscribe();
        }
    }

    public void subscribe() {
        Fitness.getRecordingClient(getActivity(), GoogleSignIn.getLastSignedInAccount(getActivity()))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    try {
                                        Log.e(">>>>>>", "Successfully subscribed!");

                                        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                                                .addApi(Fitness.HISTORY_API)
                                                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                                                .addConnectionCallbacks(StepsFragment.this)
                                                .enableAutoManage(getActivity(), 0, StepsFragment.this)
                                                .build();
                                    } catch (Exception ex) {

                                    }


                                } else {
                                    Log.e(">>>>>>", "There was a problem subscribing.", task.getException());
                                }
                            }
                        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                subscribe();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupFitness();

                } else {

                }
                return;
        }

    }

    private class GetStepCountTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            displayStepDataForToday();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            if (todaySteps.isEmpty()) {
                try {
                    tvToday.setText("0/10000");
                } catch (Exception e) {

                }
            } else {
                try {
                    tvToday.setText(todaySteps+""+"/10000");
                    circularProgressBar.setProgressMax(10000f);
                    circularProgressBar.setProgress(Integer.parseInt(todaySteps));
                    circularProgressBar.setProgressWithAnimation(Integer.parseInt(todaySteps), (long) 10000);
                } catch (Exception e) {

                }
            }
            if (!steps.isEmpty()) {

                try {
                    if (Integer.parseInt(todaySteps) > 9999) {
                        submitCLick.setAlpha(1);
                        submitCLick.setEnabled(true);

                    } else {
                        submitCLick.setAlpha(0.4f);
                        submitCLick.setEnabled(false);
                    }
                } catch (Exception e) {

                }

            }

        }
    }


    private void displayStepDataForToday() {
        try {
            DailyTotalResult result = Fitness.HistoryApi.
                    readDailyTotalFromLocalDevice(mGoogleApiClient, DataType.TYPE_STEP_COUNT_DELTA)
                    .await(1, TimeUnit.MINUTES);
            result.getStatus();
            result.getTotal();
            for (DataPoint dp : result.getTotal().getDataPoints()) {
                for (Field field : dp.getDataType().getFields()) {
                    if (!"user_input".equals(dp.getOriginalDataSource().getStreamName())) {
                        Log.e("History", "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                        todaySteps = "" + dp.getValue(field);
                    }
                }
            }
        } catch (Exception e) {

        }


    }

    @Override
    public void onRefresh() {

    }


    private Date getStart() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();


    }

    private Date getEnd() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
        Calendar end = Calendar.getInstance();
        end.add(Calendar.DATE, -1);
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        return end.getTime();
    }


    private void getStepsForYesterday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        long end = getEnd().getTime();
        ;
        long start = getStart().getTime();


        Log.e(">>>>>", "Start: " + sdf.format(start) + " >> End: " + sdf.format(end));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(start, end, TimeUnit.MILLISECONDS)

                .build();
        DataReadResult dataReadResult =
                Fitness.HistoryApi
                        .readData(mGoogleApiClient, readRequest)
                        .await(1, TimeUnit.MINUTES);

        if (dataReadResult.getBuckets().size() > 0) {
            Log.e("History", "Number of buckets: " + dataReadResult.getBuckets().size());

            for (int i = 0; i < dataReadResult.getBuckets().size(); i++) {
                List<DataSet> dataSets = dataReadResult.getBuckets().get(i).getDataSets();
                for (int j = 0; j < dataSets.size(); j++) {
                    showDataSet(dataSets.get(j));
                }
            }

        }//Used for non-aggregated data
        else if (dataReadResult.getDataSets().size() > 0) {
            Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                showDataSet(dataSet);
            }
        }
    }

    private void showDataSet(DataSet dataSet) {
        for (DataPoint dp : dataSet.getDataPoints()) {
//            logDays(dp,dateFormat,timeFormat);
            for (Field field : dp.getDataType().getFields()) {
                if (!"user_input".equals(dp.getOriginalDataSource().getStreamName()))
                    try {
                        steps.add("" + dp.getValue(field));
                    } catch (Exception e) {

                    }
            }
        }
    }

    @Override
    public void onResume() {
        if (handler == null) {
            handler = new Handler();
        }
        handler.postDelayed(runnable, delay);
        super.onResume();
    }
}