package com.robotack.loyalti.ui.Activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.robotack.loyalti.R;
import com.robotack.loyalti.helpers.LanguageHelper;
import com.robotack.loyalti.helpers.PrefConstant;
import com.robotack.loyalti.managers.ApiCallResponse;
import com.robotack.loyalti.managers.BusinessManager;
import com.robotack.loyalti.models.AdsBannerModel;
import com.robotack.loyalti.models.CustomerDataModel;
import com.robotack.loyalti.models.GetTokenListener;
import com.robotack.loyalti.ui.Adapters.ImageSlideAdapter;
import com.robotack.loyalti.utilities.AutoScrollViewPager;
import com.robotack.loyalti.utilities.CirclePageIndicator;
import com.robotack.loyalti.utilities.Utils;

import static com.robotack.loyalti.helpers.PrefConstant.sdkLanguage;
import static com.robotack.loyalti.helpers.SharedPrefConstants.Language;

public class LoyaltyActivity extends AppCompatActivity {

    TextView redeemBtn;
    CardView stepsCard;
    TextView accumulatedPoints;
    TextView accumulatedCashback;
    TextView expiryPoint;
    TextView currentPoints;
    TextView currentPointsValue;
    TextView historyBtn;
    ShimmerFrameLayout mShimmerViewContainer;
    CustomerDataModel customerDataModel = null;
    ImageView arrow;
    ImageView arrowSteps;
    String userID = "UAT-00281253";
//    String userID = null;
    String LanguageValue = "en";
    LinearLayout mainView;

    GetTokenListener getTokenListener ;
    AutoScrollViewPager imagesViewPager;
    CirclePageIndicator imagesPageIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            try {
                LanguageValue = getIntent().getStringExtra(sdkLanguage);
            } catch (Exception e) {
                LanguageValue = "en";
            }
            if (LanguageValue == null) {
                LanguageValue = "en";
            }
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString(Language, LanguageValue);
            edit.commit();
        } catch (Exception e) {
            LanguageValue = "en";
        }
        new Utils().updateLangauge(this);
        setContentView(R.layout.activity_loyatli);


        try {
            Intent intent = getIntent();
            getTokenListener = (GetTokenListener) intent.getSerializableExtra("getTokenListener");
            if (getTokenListener == null)
            {
                showSettingsAlert(LoyaltyActivity.this,getString(R.string.internal_error));
                return;
            }
        } catch (Exception e) {

        }
        imagesPageIndicator = (CirclePageIndicator) findViewById(R.id.imagesPageIndicator);
        mainView = (LinearLayout) findViewById(R.id.mainView);
        imagesViewPager = (AutoScrollViewPager) findViewById(R.id.imagesViewPager);

        try {
            userID = getIntent().getStringExtra(PrefConstant.custumerID);

            if (userID == null) {
                showSettingsAlert(LoyaltyActivity.this,getString(R.string.no_user));

            }else {
                new Utils().setUserID(userID, this);
                getAdsBanner();
                getCustomerInfo();
            }
        } catch (Exception e) {
            showSettingsAlert(LoyaltyActivity.this,getString(R.string.no_user));
        }
        setupViews();
        if (!new Utils().isGMSAvailable(this)) {
            stepsCard.setVisibility(View.GONE);
        }

        stepsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoyaltyActivity.this, LoyaltiStepsActivity.class).putExtra("getTokenListener",getTokenListener));
            }
        });
        redeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customerDataModel == null) {
                    return;
                }

                startActivity(new Intent(LoyaltyActivity.this, LoyaltiRedeemPointsActivity.class).putExtra("customerDataModel", customerDataModel).putExtra("getTokenListener",getTokenListener));
            }
        });

        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoyaltyActivity.this, LoyaltiHistoryRedeemPointsActivity.class).putExtra("getTokenListener",getTokenListener));
            }
        });



        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setupViews() {

        mShimmerViewContainer = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmer();
        stepsCard = (CardView) findViewById(R.id.stepsCard);
        historyBtn = (TextView) findViewById(R.id.historyBtn);
        redeemBtn = (TextView) findViewById(R.id.redeemBtn);
        arrow = (ImageView) findViewById(R.id.arrow);
        arrowSteps = (ImageView) findViewById(R.id.arrowSteps);
        if (LanguageHelper.getCurrentLanguage(this).equals("ar")) {
            arrow.setScaleX(-1);
            arrowSteps.setScaleX(-1);
        }
        accumulatedPoints = (TextView) findViewById(R.id.accumulatedPoints);
        accumulatedCashback = (TextView) findViewById(R.id.accumulatedCashback);
        expiryPoint = (TextView) findViewById(R.id.expiryPoint);
        currentPoints = (TextView) findViewById(R.id.currentPoints);
        currentPointsValue = (TextView) findViewById(R.id.currentPointsValue);


    }

    private void getAdsBanner()
    {
        new BusinessManager().getAdsBanner(this,getTokenListener.getToken(), new ApiCallResponse() {
            @Override
            public void onSuccess(Object responseObject, String responseMessage) {
                AdsBannerModel customerHistoryModel = null;
                try {
                    customerHistoryModel = (AdsBannerModel) responseObject;
                    ImageSlideAdapter imageSlideAdapter = new ImageSlideAdapter(LoyaltyActivity.this, customerHistoryModel.getData());
                    imagesViewPager.setAdapter(imageSlideAdapter);
                    imagesPageIndicator.setViewPager(imagesViewPager);
                    imagesViewPager.startAutoScroll();
                    imagesViewPager.setInterval(3000);
                    imagesViewPager.setCycle(true);
                    imagesViewPager.setStopScrollWhenTouch(true);
                } catch (Exception e) {
                }

            }

            @Override
            public void onFailure(String errorResponse) {

            }
        });

    }

    private void getCustomerInfo() {
        new BusinessManager().getUserInfoApiCall(this,getTokenListener.getToken(), new ApiCallResponse() {
            @Override
            public void onSuccess(Object responseObject, String responseMessage) {

                try {
                    customerDataModel = (CustomerDataModel) responseObject;
                    if (customerDataModel != null) {

                        if (customerDataModel.getErrorCode().toString().equals("0")) {
                            mShimmerViewContainer.setVisibility(View.GONE);
                            mShimmerViewContainer.stopShimmer();
                            mainView.setVisibility(View.VISIBLE);
                            expiryPoint.setText(getResources().getString(R.string.expiry_points).replace("xxx", customerDataModel.getExpiryPoint().toString()));
                            currentPoints.setText(customerDataModel.getCurrentPoints().toString());
                            currentPointsValue.setText(getResources().getString(R.string.jod).replace("xxx", customerDataModel.getCurrentPointsValue().toString()));
                            accumulatedCashback.setText(customerDataModel.getAccumulatedCashback().toString());
                            accumulatedPoints.setText(customerDataModel.getAccumulatedPoints().toString());
                            try {
                                new Utils().setIdentifierValue(customerDataModel.getIdentifierValue(), LoyaltyActivity.this);
                            } catch (Exception e) {

                            }
                        } else if (customerDataModel.getErrorCode().toString().equals("-99")) {
                            startActivity(new Intent(LoyaltyActivity.this, MaintancePageActivity.class));
                        }else {
                            showSettingsAlert(LoyaltyActivity.this,customerDataModel.getDescriptionCode().toString());
                        }

                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(String errorResponse) {
                showSettingsAlert(LoyaltyActivity.this,getString(R.string.something_wrong));
            }
        });
    }

    public void showSettingsAlert(Context context,String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("");
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(Html.fromHtml(context.getResources().getString(R.string.ok__)), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              finish();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }


}