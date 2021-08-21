package com.robotack.loyalti.ui.Activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.robotack.loyalti.R;
import com.robotack.loyalti.helpers.LanguageHelper;
import com.robotack.loyalti.managers.ApiCallResponse;
import com.robotack.loyalti.managers.BusinessManager;
import com.robotack.loyalti.models.CustomerDataModel;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            String LanguageValue = null;
            try {
                LanguageValue = getIntent().getStringExtra(sdkLanguage);
            } catch (Exception e) {
                LanguageValue = "en";
            }
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString(Language, LanguageValue);
            edit.commit();
        } catch (Exception e) {
        }
        new Utils().updateLangauge(this);
        setContentView(R.layout.activity_loyatli);
        setupViews();
        stepsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoyaltyActivity.this, LoyaltiStepsActivity.class));
            }
        });
        redeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customerDataModel == null) {
                    return;
                }
                startActivity(new Intent(LoyaltyActivity.this, LoyaltiRedeemPointsActivity.class).putExtra("customerDataModel", customerDataModel));
            }
        });

        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoyaltyActivity.this, LoyaltiHistoryRedeemPointsActivity.class));
            }
        });
        new Utils().setUserID("UAT-00281252", this);
        getCustomerInfo();
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setupViews() {

        mShimmerViewContainer = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmerAnimation();
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

    private void getCustomerInfo() {
        new BusinessManager().getUserInfoApiCall(this, new ApiCallResponse() {
            @Override
            public void onSuccess(Object responseObject, String responseMessage) {
                mShimmerViewContainer.setVisibility(View.GONE);
                try {
                    customerDataModel = (CustomerDataModel) responseObject;
                    if (customerDataModel != null) {
                        expiryPoint.setText(getResources().getString(R.string.expiry_points).replace("xxx", customerDataModel.getExpiryPoint().toString()));
                        currentPoints.setText(customerDataModel.getCurrentPoints().toString());
                        currentPointsValue.setText(getResources().getString(R.string.jod).replace("xxx", customerDataModel.getCurrentPointsValue().toString()));
                        accumulatedCashback.setText(customerDataModel.getAccumulatedCashback().toString());
                        accumulatedPoints.setText(customerDataModel.getAccumulatedPoints().toString());
                        try {
                            new Utils().setIdentifierValue(customerDataModel.getIdentifierValue(), LoyaltyActivity.this);
                        } catch (Exception e) {

                        }
                    }
                } catch (Exception e) {
                    mShimmerViewContainer.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(String errorResponse) {
                mShimmerViewContainer.setVisibility(View.GONE);
            }
        });
    }
}