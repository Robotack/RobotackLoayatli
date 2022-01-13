package com.robotack.loyalti.ui.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.robotack.loyalti.R;
import com.robotack.loyalti.managers.ApiCallResponse;
import com.robotack.loyalti.managers.BusinessManager;
import com.robotack.loyalti.models.GainPointsModel;
import com.robotack.loyalti.models.GenralModel;
import com.robotack.loyalti.models.GetTokenListener;
import com.robotack.loyalti.models.StepsInfoModel;
import com.robotack.loyalti.ui.Activites.MaintancePageActivity;
import com.robotack.loyalti.utilities.Utils;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import xyz.hasnat.sweettoast.SweetToast;


public class LoyaltiStepsFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;
    private TextView submitCLick;
    private TextView tvToday;
    private TextView infoSteps;
    private CardView btn;
    String todaySteps = "";
    TextView stepsStatis;
    private GoogleApiClient mGoogleApiClient;
    CircularProgressBar circularProgressBar;
    ArrayList<String> steps = new ArrayList<>();
    private Handler handler;
    private int delay = 3000;
    int StepCountValue = 10000;
    ProgressBar progressBar;
    Runnable runnable;

    String stepCountValidation = "10000";

    GetTokenListener interToken ;
    public LoyaltiStepsFragment(GetTokenListener inter) {
        interToken = inter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.loyal_fragment_steps, container, false);
        btn = (CardView) rootView.findViewById(R.id.clickBtn);
        tvToday = (TextView) rootView.findViewById(R.id.tvToday);
        infoSteps = (TextView) rootView.findViewById(R.id.infoSteps);
        stepsStatis = (TextView) rootView.findViewById(R.id.stepsStatis);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        submitCLick = (TextView) rootView.findViewById(R.id.submitCLick);
        circularProgressBar = rootView.findViewById(R.id.circularProgressbar);
        circularProgressBar.setProgressBarColor(Color.parseColor("#306095"));
        circularProgressBar.setBackgroundProgressBarColor(Color.parseColor("#40306095"));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupFitness();
            }
        });

        getStepsInfo();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    101);
        }

        submitCLick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gainPoints();
            }
        });
        return rootView;
    }


    private void getStepsInfo() {
        new BusinessManager().getStepsInfo(getActivity(),interToken.getToken(), new ApiCallResponse() {
            @Override
            public void onSuccess(Object responseObject, String responseMessage) {
                StepsInfoModel genralModel = null;
                try {
                    genralModel = (StepsInfoModel) responseObject;
                    if (genralModel.getErrorCode() == 0) {
                        infoSteps.setText(getString(R.string.steps_count).replace("xxxx", genralModel.getPoints()).replace("yyyy", genralModel.getStepsCount()));
                        StepCountValue = Integer.parseInt(genralModel.getStepsCount());
                        stepsStatis.setText(getString(R.string.you_need_to_reach).toString().replace("xxxx",genralModel.getStepsCount()));
                        stepsStatis.setVisibility(View.VISIBLE);
                        stepCountValidation = genralModel.getStepsCount().toString();
                    } else if (genralModel.getErrorCode() == -99) {
                        startActivity(new Intent(getActivity(), MaintancePageActivity.class));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(String errorResponse) {

            }
        });
    }

    private void gainPoints() {

        progressBar.setVisibility(View.VISIBLE);
        GainPointsModel senderClass = new GainPointsModel();
        senderClass.identifierValue = new Utils().getIdentifierValue(getActivity());
        senderClass.points = todaySteps;
        senderClass.eventKey = "steps";
        Gson gson = new Gson();
        String json = gson.toJson(senderClass);
        JsonObject gsonObject = new JsonObject();
        JsonParser jsonParser = new JsonParser();
        gsonObject = (JsonObject) jsonParser.parse(json.toString());
        new BusinessManager().gainPoints(getActivity(), gsonObject,interToken.getToken(), new ApiCallResponse() {
            @Override
            public void onSuccess(Object responseObject, String responseMessage) {
                progressBar.setVisibility(View.GONE);

                GenralModel genralModel = null;
                try {
                    genralModel = (GenralModel) responseObject;
                    if (genralModel.getErrorCode() == 0) {

                        SweetToast.success(getActivity(), genralModel.getDescriptionCode(), 3000);

                    } else if (genralModel.getErrorCode() == -99) {
                        startActivity(new Intent(getActivity(), MaintancePageActivity.class));

                    } else {
                        try {
                            SweetToast.error(getActivity(), genralModel.getDescriptionCode(), 3000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(String errorResponse) {
                progressBar.setVisibility(View.GONE);

                try {
                    SweetToast.error(getActivity(), errorResponse, 3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                GetStepCountTask task = new GetStepCountTask();
                task.execute();            }
        }, 3000);


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
                                                .addConnectionCallbacks(LoyaltiStepsFragment.this)
                                                .enableAutoManage(getActivity(), 0, LoyaltiStepsFragment.this)
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
                    tvToday.setText("0/"+stepCountValidation);
                } catch (Exception e) {

                }
            } else {
                try {
                    tvToday.setText(todaySteps + "" + "/ "+stepCountValidation);
                    circularProgressBar.setProgressMax(Float.parseFloat(stepCountValidation));
                    circularProgressBar.setProgress(Integer.parseInt(todaySteps));
                    circularProgressBar.setProgressWithAnimation(Integer.parseInt(todaySteps), (long) Float.parseFloat(stepCountValidation));
                } catch (Exception e) {

                }
            }
            if (!todaySteps.isEmpty()) {

                try {
                    if (Integer.parseInt(todaySteps) >= StepCountValue) {
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
        long start = getStart().getTime();
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
            for (int i = 0; i < dataReadResult.getBuckets().size(); i++) {
                List<DataSet> dataSets = dataReadResult.getBuckets().get(i).getDataSets();
                for (int j = 0; j < dataSets.size(); j++) {
                    showDataSet(dataSets.get(j));
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                showDataSet(dataSet);
            }
        }
    }

    private void showDataSet(DataSet dataSet) {
        for (DataPoint dp : dataSet.getDataPoints()) {
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