//package com.robotack.loyalti.ui.Fragments;
//
//import android.Manifest;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Color;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import com.huawei.hmf.tasks.OnFailureListener;
//import com.huawei.hmf.tasks.OnSuccessListener;
//import com.huawei.hmf.tasks.Task;
//import com.huawei.hms.common.ApiException;
//import com.huawei.hms.hihealth.DataController;
//import com.huawei.hms.hihealth.HiHealthOptions;
//import com.huawei.hms.hihealth.HuaweiHiHealth;
//import com.huawei.hms.hihealth.SettingController;
//import com.huawei.hms.hihealth.data.DataType;
//import com.huawei.hms.hihealth.data.Field;
//import com.huawei.hms.hihealth.data.SamplePoint;
//import com.huawei.hms.hihealth.data.SampleSet;
//import com.huawei.hms.hihealth.data.Scopes;
//import com.huawei.hms.support.api.entity.auth.Scope;
//import com.huawei.hms.support.hwid.HuaweiIdAuthAPIManager;
//import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
//import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
//import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
//import com.huawei.hms.support.hwid.result.AuthHuaweiId;
//import com.huawei.hms.support.hwid.result.HuaweiIdAuthResult;
//import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;
//import com.mikhaellopez.circularprogressbar.CircularProgressBar;
//import com.robotack.loyalti.R;
//import com.robotack.loyalti.managers.ApiCallResponse;
//import com.robotack.loyalti.managers.BusinessManager;
//import com.robotack.loyalti.models.GainPointsModel;
//import com.robotack.loyalti.models.GenralModel;
//import com.robotack.loyalti.models.StepsInfoModel;
//import com.robotack.loyalti.ui.Activites.MaintancePageActivity;
//import com.robotack.loyalti.utilities.Utils;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//import xyz.hasnat.sweettoast.SweetToast;
//
//
//public class LoyaltiHMStepsFragment extends Fragment {
//    View rootView;
//    int StepCountValue = 10000;
//    private TextView submitCLick;
//    private TextView tvToday;
//    String todaySteps = "";
//    CircularProgressBar circularProgressBar;
//    private SettingController mSettingController;
//    private static final String HEALTH_APP_SETTING_DATA_SHARE_HEALTHKIT_ACTIVITY_SCHEME =
//            "huaweischeme://healthapp/achievement?module=kit";
//    private static final int REQUEST_SIGN_IN_LOGIN = 1002;
//    private static final int REQUEST_HEALTH_AUTH = 1003;
//    private DataController dataController;
//    ProgressBar progressBar;
//    private TextView infoSteps;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        rootView = inflater.inflate(R.layout.loyal_fragment_steps, container, false);
//        loginInHuaweiID();
//        initValue();
//        getStepsInfo();
//        return rootView;
//    }
//
//    private void loginInHuaweiID() {
//        signIn();
//    }
//
//    private void signIn() {
//        List<Scope> scopeList = new ArrayList<>();
//        scopeList.add(new Scope(Scopes.HEALTHKIT_STEP_BOTH));
//        scopeList.add(new Scope(Scopes.HEALTHKIT_HEIGHTWEIGHT_BOTH));
//        scopeList.add(new Scope(Scopes.HEALTHKIT_HEARTRATE_BOTH));
//        scopeList.add(new Scope(Scopes.HEALTHKIT_STEP_REALTIME));
//        scopeList.add(new Scope(Scopes.HEALTHKIT_HEARTRATE_REALTIME));
//        scopeList.add(new Scope(Scopes.HEALTHKIT_ACTIVITY_RECORD_BOTH));
//        HuaweiIdAuthParamsHelper authParamsHelper =
//                new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM);
//        HuaweiIdAuthParams authParams =
//                authParamsHelper.setIdToken().setAccessToken().setScopeList(scopeList).createParams();
//        final HuaweiIdAuthService authService =
//                HuaweiIdAuthManager.getService(getActivity(), authParams);
//        Task<AuthHuaweiId> authHuaweiIdTask = authService.silentSignIn();
//        authHuaweiIdTask.addOnSuccessListener(huaweiId -> {
//            checkOrAuthorizeHealth();
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(Exception exception) {
//                if (exception instanceof ApiException) {
//                    ApiException apiException = (ApiException) exception;
//                    Log.i("TAG", "sign failed status:" + apiException.getStatusCode());
//                    Intent signInIntent = authService.getSignInIntent();
//                    startActivityForResult(signInIntent, REQUEST_SIGN_IN_LOGIN);
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        handleSignInResult(requestCode, data);
//        handleHealthAuthResult(requestCode);
//    }
//
//    private void handleSignInResult(int requestCode, Intent data) {
//        if (requestCode != REQUEST_SIGN_IN_LOGIN) {
//            return;
//        }
//        HuaweiIdAuthResult result = HuaweiIdAuthAPIManager.HuaweiIdAuthAPIService.parseHuaweiIdFromIntent(data);
//        if (result != null) {
//            if (result.isSuccess()) {
//                HuaweiIdAuthResult authResult =
//                        HuaweiIdAuthAPIManager.HuaweiIdAuthAPIService.parseHuaweiIdFromIntent(data);
//                checkOrAuthorizeHealth();
//            }
//        }
//    }
//
//    private void checkOrAuthorizeHealth() {
//        Task<Boolean> authTask = mSettingController.getHealthAppAuthorisation();
//        authTask.addOnSuccessListener(new OnSuccessListener<Boolean>() {
//            @Override
//            public void onSuccess(Boolean result) {
//                Log.i("Success Permission", "checkOrAuthorizeHealth get result success");
//                if (Boolean.TRUE.equals(result)) {
//
//                    setupHMSFitness();
//
//                } else {
//                    Uri healthKitSchemaUri = Uri.parse(HEALTH_APP_SETTING_DATA_SHARE_HEALTHKIT_ACTIVITY_SCHEME);
//                    Intent intent = new Intent(Intent.ACTION_VIEW, healthKitSchemaUri);
//                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
//                        startActivityForResult(intent, REQUEST_HEALTH_AUTH);
//                    } else {
//
//                    }
//                }
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(Exception exception) {
//                try {
//
//                    if (exception != null) {
//                        if (exception.getMessage().equals("50000")) {
//                            SweetToast.error(getActivity(), getResources().getString(R.string.huawei_error), 3000);
//                        } else if (exception.getMessage().contains("50005")) {
//                            SweetToast.error(getActivity(), getResources().getString(R.string.huawei_error2), 3000);
//                        } else if (exception.getMessage().contains("50007")) {
//                            SweetToast.error(getActivity(), getResources().getString(R.string.huawei_error3), 3000);
//                        } else if (exception.getMessage().contains("50008")) {
//                            SweetToast.error(getActivity(), getResources().getString(R.string.huawei_error4), 3000);
//                        } else if (exception.getMessage().contains("50009")) {
//                            SweetToast.error(getActivity(), getResources().getString(R.string.huawei_error5), 3000);
//                        } else if (exception.getMessage().contains("50026")) {
//                            SweetToast.error(getActivity(), getResources().getString(R.string.huawei_error6), 3000);
//                        } else if (exception.getMessage().contains("50037")) {
//                            SweetToast.error(getActivity(), getResources().getString(R.string.huawei_error7), 3000);
//                        } else if (exception.getMessage().contains("50038")) {
//                            SweetToast.error(getActivity(), getResources().getString(R.string.huawei_error8), 3000);
//                        } else if (exception.getMessage().contains("50010")) {
//                            SweetToast.error(getActivity(), getResources().getString(R.string.huawei_error9), 3000);
//                        } else if (exception.getMessage().contains("50032")) {
//                            SweetToast.error(getActivity(), getResources().getString(R.string.huawei_error10), 3000);
//                        } else if (exception.getMessage().contains("50032")) {
//                            SweetToast.error(getActivity(), getResources().getString(R.string.huawei_error10), 3000);
//                        } else if (exception.getMessage().contains("50033")) {
//                            SweetToast.error(getActivity(), getResources().getString(R.string.huawei_error11), 3000);
//                        } else if (exception.getMessage().contains("50034")) {
//                            SweetToast.error(getActivity(), getResources().getString(R.string.huawei_error12), 3000);
//                        } else {
//                            SweetToast.error(getActivity(), getResources().getString(R.string.huawei_error), 3000);
//                        }
//                    }
//
//                } catch (Exception e) {
//
//                }
//
//
//            }
//
//        });
//    }
//
//    private void initValue() {
//        infoSteps = (TextView) rootView.findViewById(R.id.infoSteps);
//        tvToday = (TextView) rootView.findViewById(R.id.tvToday);
//        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
//        submitCLick = (TextView) rootView.findViewById(R.id.submitCLick);
//        circularProgressBar = rootView.findViewById(R.id.circularProgressbar);
//        circularProgressBar.setProgressBarColor(Color.parseColor("#306095"));
//        circularProgressBar.setBackgroundProgressBarColor(Color.parseColor("#40306095"));
//
//        HiHealthOptions fitnessOptions = HiHealthOptions.builder().build();
//        AuthHuaweiId signInHuaweiId = HuaweiIdAuthManager.getExtendedAuthResult(fitnessOptions);
//        mSettingController = HuaweiHiHealth.getSettingController(getActivity(), signInHuaweiId);
//
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACTIVITY_RECOGNITION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(),
//                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
//                    101);
//        }
//        submitCLick.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                gainPoints();
//            }
//        });
//    }
//
//    private void gainPoints() {
//        submitCLick.setEnabled(false);
//        progressBar.setVisibility(View.VISIBLE);
//        GainPointsModel senderClass = new GainPointsModel();
//        senderClass.identifierValue = new Utils().getIdentifierValue(getActivity());
//        senderClass.points = todaySteps;
//        senderClass.eventKey = "steps";
//        Gson gson = new Gson();
//        String json = gson.toJson(senderClass);
//        JsonObject gsonObject = new JsonObject();
//        JsonParser jsonParser = new JsonParser();
//        gsonObject = (JsonObject) jsonParser.parse(json.toString());
//        new BusinessManager().gainPoints(getActivity(), gsonObject, new ApiCallResponse() {
//            @Override
//            public void onSuccess(Object responseObject, String responseMessage) {
//                progressBar.setVisibility(View.GONE);
//                submitCLick.setEnabled(true);
//                GenralModel genralModel = null;
//                try {
//                    genralModel = (GenralModel) responseObject;
//                    if (genralModel.getErrorCode() == 0) {
//
//                        SweetToast.success(getActivity(), genralModel.getDescriptionCode(), 3000);
//
//                    }else if (genralModel.getErrorCode() == -99) {
//                        startActivity(new Intent(getActivity(), MaintancePageActivity.class));
//
//                    } else {
//                        try {
//                            SweetToast.error(getActivity(), genralModel.getDescriptionCode(), 3000);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(String errorResponse) {
//                progressBar.setVisibility(View.GONE);
//                submitCLick.setEnabled(true);
//                try {
//                    SweetToast.error(getActivity(), errorResponse, 3000);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//    }
//
//    private void getStepsInfo() {
//        new BusinessManager().getStepsInfo(getActivity(), new ApiCallResponse() {
//            @Override
//            public void onSuccess(Object responseObject, String responseMessage) {
//                StepsInfoModel genralModel = null;
//                try {
//                    genralModel = (StepsInfoModel) responseObject;
//                    if (genralModel.getErrorCode() == 0) {
//                        infoSteps.setText(getString(R.string.steps_count).replace("xxxx", genralModel.getPoints()).replace("yyyy", genralModel.getStepsCount()));
//                        StepCountValue = Integer.parseInt(genralModel.getStepsCount());
//                    } else if (genralModel.getErrorCode() == -99) {
//                        startActivity(new Intent(getActivity(), MaintancePageActivity.class));
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(String errorResponse) {
//
//            }
//        });
//    }
//
//    class MyAwesomeAsyncTask extends AsyncTask<Void, Void, Void> {
//
//
//        @Override
//        protected void onPreExecute() {
//            //Create progress dialog here and show it
//            try {
//                progressBar.setVisibility(View.VISIBLE);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                progressBar.setVisibility(View.GONE);
//            } catch (Exception e) {
//
//            }
//            return null;
//
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            try {
//                progressBar.setVisibility(View.GONE);
//            } catch (Exception e) {
//
//            }
//            try {
//
//                todaysteps2();
//            } catch (Exception e) {
//            }
//
//        }
//
//    }
//
//    private void setupHMSFitness() {
//        HiHealthOptions hiHealthOptions = HiHealthOptions.builder()
//                .addDataType(DataType.DT_CONTINUOUS_STEPS_DELTA, HiHealthOptions.ACCESS_READ)
//                .addDataType(DataType.DT_CONTINUOUS_STEPS_DELTA, HiHealthOptions.ACCESS_WRITE)
//                .addDataType(DataType.DT_INSTANTANEOUS_HEIGHT, HiHealthOptions.ACCESS_READ)
//                .addDataType(DataType.DT_INSTANTANEOUS_HEIGHT, HiHealthOptions.ACCESS_WRITE)
//                .build();
//        AuthHuaweiId signInHuaweiId = HuaweiIdAuthManager.getExtendedAuthResult(hiHealthOptions);
//        dataController = HuaweiHiHealth.getDataController(getActivity(), signInHuaweiId);
//        try {
//            new MyAwesomeAsyncTask().execute();
//        } catch (Exception e) {
//
//        }
//
//
//    }
//
//
//    private void handleHealthAuthResult(int requestCode) {
//        if (requestCode != REQUEST_HEALTH_AUTH) {
//            return;
//        }
//
//        queryHealthAuthorization();
//    }
//
//    private void queryHealthAuthorization() {
//
//        Task<Boolean> queryTask = mSettingController.getHealthAppAuthorisation();
//        queryTask.addOnSuccessListener(new OnSuccessListener<Boolean>() {
//            @Override
//            public void onSuccess(Boolean result) {
//
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(Exception exception) {
//
//
//            }
//        });
//    }
//
//
//    public void todaysteps2() {
//        try {
//            Task<SampleSet> todaySummationTask = dataController.readTodaySummation(DataType.DT_CONTINUOUS_STEPS_DELTA);
//            todaySummationTask.addOnSuccessListener(new OnSuccessListener<SampleSet>() {
//                @Override
//                public void onSuccess(SampleSet sampleSet) {
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//                    for (SamplePoint samplePoint : sampleSet.getSamplePoints()) {
//                        for (Field field : samplePoint.getDataType().getFields()) {
//
//                            try {
//                                tvToday.setText("0" + "" + "/10000");
//                            } catch (Exception e) {
//
//                            }
//                            String todayvalue = "";
//                            todayvalue = sampleSet.getSamplePoints().get(0).getFieldValue(field) + "";
//                            try {
//                                tvToday.setText(todayvalue + "" + "/10000");
//                            } catch (Exception e) {
//
//                            }
//
//                            try {
//                                if (Integer.parseInt(tvToday.getText().toString()) >= StepCountValue) {
//                                    submitCLick.setAlpha(1);
//                                    submitCLick.setEnabled(true);
//
//                                } else {
//                                    submitCLick.setAlpha(0.4f);
//                                    submitCLick.setEnabled(false);
//                                }
//                            } catch (Exception e) {
//
//                            }
//                        }
//                    }
//                }
//            });
//            todaySummationTask.addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(Exception e) {
//                }
//            });
//        } catch (Exception e) {
//
//        }
//
//    }
//
//
//}
