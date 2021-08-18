package com.robotack.loyatli.ui.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.facebook.shimmer.ShimmerFrameLayout;

import com.robotack.loyatli.R;
import com.robotack.loyatli.helpers.LanguageHelper;
import com.robotack.loyatli.managers.ApiCallResponse;
import com.robotack.loyatli.managers.BusinessManager;
import com.robotack.loyatli.models.CustomerDataModel;
import com.robotack.loyatli.utilities.Utils;

import static com.robotack.loyatli.applications.MyApplication.updateLanguage;


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
        updateLanguage(this);
        setContentView(R.layout.activity_main);
        setupViews();
        stepsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoyaltyActivity.this, StepsActivity.class));
            }
        });
        redeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customerDataModel == null)
                {
                    return;
                }
                startActivity(new Intent(LoyaltyActivity.this, RedeemPointsActivity.class).putExtra("customerDataModel",customerDataModel));
            }
        });

        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoyaltyActivity.this, HistoryRedeemPointsActivity.class));
            }
        });
        new Utils().setUserID("UAT-00281252", this);
        getCustomerInfo();
    }

    private void setupViews() {

        mShimmerViewContainer = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmerAnimation();
        stepsCard = (CardView) findViewById(R.id.stepsCard);
        historyBtn = (TextView) findViewById(R.id.historyBtn);
        redeemBtn = (TextView) findViewById(R.id.redeemBtn);
        arrow = (ImageView) findViewById(R.id.arrow);
        arrowSteps = (ImageView) findViewById(R.id.arrowSteps);
        if (LanguageHelper.getCurrentLanguage(this).equals("ar"))
        {
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
                            new Utils().setIdentifierValue(customerDataModel.getIdentifierValue(),LoyaltyActivity.this);
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