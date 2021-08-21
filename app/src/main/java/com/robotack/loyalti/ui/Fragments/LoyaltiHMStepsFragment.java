package com.robotack.loyalti.ui.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.hihealth.DataController;
import com.huawei.hms.hihealth.HiHealthOptions;
import com.huawei.hms.hihealth.HuaweiHiHealth;
import com.huawei.hms.hihealth.SettingController;
import com.huawei.hms.hihealth.data.DataType;
import com.huawei.hms.hihealth.data.Field;
import com.huawei.hms.hihealth.data.SamplePoint;
import com.huawei.hms.hihealth.data.SampleSet;
import com.huawei.hms.hihealth.data.Scopes;
import com.huawei.hms.support.api.entity.auth.Scope;
import com.huawei.hms.support.hwid.HuaweiIdAuthAPIManager;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.result.HuaweiIdAuthResult;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;
import com.robotack.loyalti.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import xyz.hasnat.sweettoast.SweetToast;


public class LoyaltiHMStepsFragment extends Fragment {
    View rootView;
    LinearLayout getCoinsLinearLayout;
    private SettingController mSettingController;
    private static final String HEALTH_APP_SETTING_DATA_SHARE_HEALTHKIT_ACTIVITY_SCHEME =
            "huaweischeme://healthapp/achievement?module=kit";
    private static final int REQUEST_SIGN_IN_LOGIN = 1002;
    private static final int REQUEST_HEALTH_AUTH = 1003;
    private DataController dataController;
    String stepsCount;
    String currentDate = "";
    String yesterdaydate = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.loyal_fragment_steps, container, false);
        loginInHuaweiID();
        initValue();


        return rootView;
    }


    private void loginInHuaweiID() {
        signIn();
    }

    private void signIn() {
        List<Scope> scopeList = new ArrayList<>();
        scopeList.add(new Scope(Scopes.HEALTHKIT_STEP_BOTH));
        scopeList.add(new Scope(Scopes.HEALTHKIT_STEP_REALTIME));
        HuaweiIdAuthParamsHelper authParamsHelper =
                new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM);
        HuaweiIdAuthParams authParams =
                authParamsHelper.setIdToken().setAccessToken().setScopeList(scopeList).createParams();
        final HuaweiIdAuthService authService =
                HuaweiIdAuthManager.getService(getActivity(), authParams);
        Task<AuthHuaweiId> authHuaweiIdTask = authService.silentSignIn();
        authHuaweiIdTask.addOnSuccessListener(huaweiId -> {
            checkOrAuthorizeHealth();
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.i("TAG", "sign failed status:" + apiException.getStatusCode());
                    Log.i("TAG", "sign failed status:" + apiException.getStatusMessage());
                    Log.i("TAG", "sign failed status:" + apiException.getMessage());
                    Intent signInIntent = authService.getSignInIntent();
                    startActivityForResult(signInIntent, REQUEST_SIGN_IN_LOGIN);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleSignInResult(requestCode, data);
        handleHealthAuthResult(requestCode);
    }

    private void handleSignInResult(int requestCode, Intent data) {
        if (requestCode != REQUEST_SIGN_IN_LOGIN) {
            return;
        }
        HuaweiIdAuthResult result = HuaweiIdAuthAPIManager.HuaweiIdAuthAPIService.parseHuaweiIdFromIntent(data);
        if (result != null) {
            if (result.isSuccess()) {
                HuaweiIdAuthResult authResult =
                        HuaweiIdAuthAPIManager.HuaweiIdAuthAPIService.parseHuaweiIdFromIntent(data);
                checkOrAuthorizeHealth();
            }
        }
    }

    private void checkOrAuthorizeHealth() {

        Task<Boolean> authTask = mSettingController.getHealthAppAuthorisation();
        authTask.addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {


                Log.i("Success Permission", "checkOrAuthorizeHealth get result success");
                if (Boolean.TRUE.equals(result)) {
                    setupHMSFitness();
                } else {
                    Uri healthKitSchemaUri = Uri.parse(HEALTH_APP_SETTING_DATA_SHARE_HEALTHKIT_ACTIVITY_SCHEME);
                    Intent intent = new Intent(Intent.ACTION_VIEW, healthKitSchemaUri);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_HEALTH_AUTH);
                    } else {
                        Log.i("Failed Permission", "checkOrAuthorizeHealth get result success");
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                try {
                    if (exception != null) {
                        if (exception.getMessage().equals("50000")) {
                            SweetToast.error(getActivity(),getResources().getString(R.string.huawei_error), 3000);
                        } else if (exception.getMessage().contains("50005")) {
                            SweetToast.error(getActivity(),getResources().getString(R.string.huawei_error2), 3000);
                        } else if (exception.getMessage().contains("50007")) {
                            SweetToast.error(getActivity(),getResources().getString(R.string.huawei_error3), 3000);
                        } else if (exception.getMessage().contains("50008")) {
                            SweetToast.error(getActivity(),getResources().getString(R.string.huawei_error4), 3000);
                        } else if (exception.getMessage().contains("50009")) {
                            SweetToast.error(getActivity(),getResources().getString(R.string.huawei_error5), 3000);
                        } else if (exception.getMessage().contains("50026")) {
                            SweetToast.error(getActivity(),getResources().getString(R.string.huawei_error6), 3000);
                        } else if (exception.getMessage().contains("50037")) {
                            SweetToast.error(getActivity(),getResources().getString(R.string.huawei_error7), 3000);
                        } else if (exception.getMessage().contains("50038")) {
                            SweetToast.error(getActivity(),getResources().getString(R.string.huawei_error8), 3000);
                        } else if (exception.getMessage().contains("50010")) {
                            SweetToast.error(getActivity(),getResources().getString(R.string.huawei_error9), 3000);
                        } else if (exception.getMessage().contains("50032")) {
                            SweetToast.error(getActivity(),getResources().getString(R.string.huawei_error10), 3000);
                        } else if (exception.getMessage().contains("50032")) {
                            SweetToast.error(getActivity(),getResources().getString(R.string.huawei_error10), 3000);
                        } else if (exception.getMessage().contains("50033")) {
                            SweetToast.error(getActivity(),getResources().getString(R.string.huawei_error11), 3000);
                        } else if (exception.getMessage().contains("50034")) {
                            SweetToast.error(getActivity(),getResources().getString(R.string.huawei_error12), 3000);
                        }else {
                            SweetToast.error(getActivity(),getResources().getString(R.string.huawei_error), 3000);
                        }
                    }

                } catch (Exception e) {

                }



            }

        });
    }

    private void initValue() {
        HiHealthOptions fitnessOptions = HiHealthOptions.builder().build();
        AuthHuaweiId signInHuaweiId = HuaweiIdAuthManager.getExtendedAuthResult(fitnessOptions);
        mSettingController = HuaweiHiHealth.getSettingController(getActivity(), signInHuaweiId);

//        refresh = rootView.findViewById(R.id.swiperefresh);
//        tvToday = rootView.findViewById(R.id.tv_today_steps);
//        fit_umnicoin = (ImageView) rootView.findViewById(R.id.fit_umnicoin);
//        tvYesterday = rootView.findViewById(R.id.tv_yesterday_steps);
//        getCoinsLinearLayout = rootView.findViewById(R.id.getCoinsLinearLayout);
//        getCoinsLinearLayout.setAlpha(0.4f);
//        getCoinsLinearLayout.setEnabled(false);
//        refresh.setOnRefreshListener(this);
//        getCoinsLinearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getYourCoinsApiCall();
//            }
//        });

    }

    class MyAwesomeAsyncTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {

            // Execute query here
            try {
                currentDate = getDate(getTime());
                yesterdaydate = getDateyesterday(getTime());

            } catch (Exception e) {
                currentDate = "";
                yesterdaydate = "";
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                yesterdaySteps();
            } catch (ParseException e) {
            }

            try {
                todaysteps2();
            } catch (Exception e) {
            }

        }

    }

    private void setupHMSFitness() {
        HiHealthOptions hiHealthOptions = HiHealthOptions.builder()
                .addDataType(DataType.DT_CONTINUOUS_STEPS_DELTA, HiHealthOptions.ACCESS_READ)
                .addDataType(DataType.DT_CONTINUOUS_STEPS_DELTA, HiHealthOptions.ACCESS_WRITE)
                .addDataType(DataType.DT_INSTANTANEOUS_HEIGHT, HiHealthOptions.ACCESS_READ)
                .addDataType(DataType.DT_INSTANTANEOUS_HEIGHT, HiHealthOptions.ACCESS_WRITE)
                .build();
        AuthHuaweiId signInHuaweiId = HuaweiIdAuthManager.getExtendedAuthResult(hiHealthOptions);
        dataController = HuaweiHiHealth.getDataController(getActivity(), signInHuaweiId);
        try {
            new MyAwesomeAsyncTask().execute();
        } catch (Exception e) {

        }
    }


    private void handleHealthAuthResult(int requestCode) {
        if (requestCode != REQUEST_HEALTH_AUTH) {
            return;
        }
        queryHealthAuthorization();
    }
    private void queryHealthAuthorization() {
        Task<Boolean> queryTask = mSettingController.getHealthAppAuthorisation();
        queryTask.addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (Boolean.TRUE.equals(result)) {
                    Log.d("onSuccess","true");
                } else {
                    Log.d("onSuccess","false");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                if (exception != null) {
                    Log.d("addOnFailureListener",exception.getMessage());
                }
            }
        });
    }

    public void yesterdaySteps() throws ParseException {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            Date yesterday = calendar.getTime();
            if (currentDate == "") {
                currentDate = df.format(new Date());
            }
            if (yesterdaydate == "") {
                yesterdaydate = df.format(yesterday);
            }
            int endTime = Integer.parseInt(currentDate);
            int startTime = Integer.parseInt(yesterdaydate);
            Task<SampleSet> daliySummationTask =
                    dataController.readDailySummation(DataType.DT_CONTINUOUS_STEPS_DELTA, startTime, endTime);
            daliySummationTask.addOnSuccessListener(new OnSuccessListener<SampleSet>() {
                @Override
                public void onSuccess(SampleSet sampleSet) {
                    System.out.println("Success yesterday summation from HMS core");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    for (SamplePoint samplePoint : sampleSet.getSamplePoints()) {
                        for (Field field : samplePoint.getDataType().getFields()) {
//                        logger("Field: " + field.getName() + " Value: " + samplePoint.getFieldValue(field));
                            // o  mean yesterday
                            String yesterdayvalue = "";
                            if (sampleSet.getSamplePoints().size() == 1) {
                                yesterdayvalue = "0";
                            } else {
                                yesterdayvalue = sampleSet.getSamplePoints().get(0).getFieldValue(field) + "";

                            }
                            System.out.println(yesterdayvalue);

                            try {
                                if (Integer.parseInt(stepsCount) > 9999) {
                                    getCoinsLinearLayout.setAlpha(1);
                                    getCoinsLinearLayout.setEnabled(true);

                                } else {
                                    getCoinsLinearLayout.setAlpha(0.4f);
                                    getCoinsLinearLayout.setEnabled(false);
                                }
                            } catch (Exception e) {

                            }

                        }
                    }

                }
            });
            daliySummationTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    System.out.println(e.getMessage() + "" + "readYesterdaySummation");
                }
            });
        } catch (Exception e) {

        }

    }

    private long getTime() {
        String url = "https://time.is/Unix_time_now";
        Document doc = null;
        try {
            doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
        } catch (IOException e) {

        }
        String[] tags = new String[]{
                "div[id=time_section]",
                "div[id=clock0_bg]"
        };
        Elements elements = doc.select(tags[0]);
        for (int i = 0; i < tags.length; i++) {
            elements = elements.select(tags[i]);
        }
        return Long.parseLong(elements.text());
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

        return dateFormat.format(cal.getTime());
    }

    private String getDateyesterday(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        cal.add(Calendar.DATE, -1);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);


        return dateFormat.format(cal.getTime());
    }

    public void todaysteps2() {
        try {
            Task<SampleSet> todaySummationTask = dataController.readTodaySummation(DataType.DT_CONTINUOUS_STEPS_DELTA);
            todaySummationTask.addOnSuccessListener(new OnSuccessListener<SampleSet>() {
                @Override
                public void onSuccess(SampleSet sampleSet) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    for (SamplePoint samplePoint : sampleSet.getSamplePoints()) {
                        for (Field field : samplePoint.getDataType().getFields()) {
                            String todayvalue = "";
                            todayvalue = sampleSet.getSamplePoints().get(0).getFieldValue(field) + "";


                            System.out.println(todayvalue + "");

                            try {
//                                tvToday.setText(todayvalue);
                            } catch (Exception e) {


                            }
                        }
                    }
                }
            });
            todaySummationTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                }
            });
        } catch (Exception e) {

        }

    }

    public void todayStepsByDaliy() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String currentDate = df.format(new Date());

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DATE, -1);

        Date yesterday = calendar.getTime();

        String yesterdaydate = df.format(yesterday);


        int endTime = Integer.parseInt(currentDate);
        int startTime = Integer.parseInt(yesterdaydate);


        Task<SampleSet> daliySummationTask =
                dataController.readDailySummation(DataType.DT_CONTINUOUS_STEPS_DELTA, startTime, endTime);


        daliySummationTask.addOnSuccessListener(new OnSuccessListener<SampleSet>() {
            @Override
            public void onSuccess(SampleSet sampleSet) {
                System.out.println("Success today summation from HMS core");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                for (SamplePoint samplePoint : sampleSet.getSamplePoints()) {
                    for (Field field : samplePoint.getDataType().getFields()) {
//                        logger("Field: " + field.getName() + " Value: " + samplePoint.getFieldValue(field));
                        // o  mean yesterday
                        String todayvalue = "";
                        if (sampleSet.getSamplePoints().size() == 1) {
                            todayvalue = sampleSet.getSamplePoints().get(0).getFieldValue(field) + "";

                        } else {
                            todayvalue = sampleSet.getSamplePoints().get(1).getFieldValue(field) + "";
                        }
                        System.out.println(todayvalue + "");

                        try {
//                            tvToday.setText(todayvalue);
                        } catch (Exception e) {


                        }
                    }
                }
            }
        });
        daliySummationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                System.out.println(e.getMessage() + "" + "readTodaySummation");
            }
        });
    }


}
