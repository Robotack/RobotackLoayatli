package com.robotack.loyalti.ui.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.robotack.loyalti.R;
import com.robotack.loyalti.helpers.LanguageHelper;
import com.robotack.loyalti.managers.ApiCallResponse;
import com.robotack.loyalti.managers.BusinessManager;
import com.robotack.loyalti.models.CalculateAmountClass;
import com.robotack.loyalti.models.GenralModel;
import com.robotack.loyalti.models.RedeemModel;
import com.robotack.loyalti.models.SenderCalculateClass;
import com.robotack.loyalti.models.SenderRedeemClass;
import com.robotack.loyalti.utilities.Utils;

import xyz.hasnat.sweettoast.SweetToast;

public class ConfirmationRedeemPointsActivity extends AppCompatActivity {
    ImageView backIcon;
    TextView toolbarTitle;
    TextView points;
    TextView calculatePoints;
    TextView transfer;
    TextView submitClick;
    ProgressBar progressBar;
    RedeemModel redeemModel = null;
    ShimmerFrameLayout mShimmerViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Utils().updateLangauge(this);
        setContentView(R.layout.activity_confirmation_redeem);
        setToolbarView();
        setupView();
    }

    private void setToolbarView() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        submitClick = (TextView) findViewById(R.id.submitClick);
        toolbarTitle.setText(R.string.redeem);
        backIcon = (ImageView) findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (LanguageHelper.getCurrentLanguage(this).equals("ar")) {
            backIcon.setScaleX(-1);
        }
        submitClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRedeemPoints(redeemModel.getPoints(), redeemModel.getAccount());
            }
        });
    }

    private void setupView() {
        try {
            mShimmerViewContainer = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
            mShimmerViewContainer.startShimmerAnimation();
            redeemModel = (RedeemModel) getIntent().getSerializableExtra("redeemModel");
            points = (TextView) findViewById(R.id.points);
            calculatePoints = (TextView) findViewById(R.id.calculatePoints);
            transfer = (TextView) findViewById(R.id.transfer);
            points.setText(redeemModel.getPoints().toString());
            transfer.setText(redeemModel.getAccount().toString());
            setCalculatePoints(redeemModel.getPoints());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCalculatePoints(String points) {

        SenderCalculateClass senderClass = new SenderCalculateClass();
        senderClass.identifierValue = new Utils().getIdentifierValue(this);
        senderClass.points = points;
        Gson gson = new Gson();
        String json = gson.toJson(senderClass);
        JsonObject gsonObject = new JsonObject();
        JsonParser jsonParser = new JsonParser();
        gsonObject = (JsonObject) jsonParser.parse(json.toString());
        new BusinessManager().postCalcautePoints(this, gsonObject, new ApiCallResponse() {
            @Override
            public void onSuccess(Object responseObject, String responseMessage) {
                CalculateAmountClass calculateAmountClass = (CalculateAmountClass) responseObject;
                try {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    calculatePoints.setText(calculateAmountClass.getAmount().toString());
                } catch (Exception e) {
                }
            }
            @Override
            public void onFailure(String errorResponse) {
            }
        });
    }

    private void setRedeemPoints(String points, String accountId) {
        submitClick.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        SenderRedeemClass senderClass = new SenderRedeemClass();
        senderClass.identifierValue = new Utils().getIdentifierValue(this);
        senderClass.points = points;
        senderClass.eventKey = "renew";
        senderClass.accountId = accountId;
        Gson gson = new Gson();
        String json = gson.toJson(senderClass);
        JsonObject gsonObject = new JsonObject();
        JsonParser jsonParser = new JsonParser();
        gsonObject = (JsonObject) jsonParser.parse(json.toString());
        new BusinessManager().redeemPoints(this, gsonObject, new ApiCallResponse() {
            @Override
            public void onSuccess(Object responseObject, String responseMessage) {
                submitClick.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                GenralModel genralModel = (GenralModel) responseObject;
                if (genralModel.getErrorCode() == 0) {

                    SweetToast.success(ConfirmationRedeemPointsActivity.this, genralModel.getDescriptionCode(), 3000);
                    Intent intent = new Intent(ConfirmationRedeemPointsActivity.this, LoyaltyActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    SweetToast.error(ConfirmationRedeemPointsActivity.this, genralModel.getDescriptionCode(), 3000);
//                    Intent intent = new Intent(ConfirmationRedeemPointsActivity.this, LoyaltyActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
                }
            }
            @Override
            public void onFailure(String errorResponse) {
                progressBar.setVisibility(View.GONE);
                submitClick.setEnabled(true);
                SweetToast.error(ConfirmationRedeemPointsActivity.this, errorResponse, 3000);
                Intent intent = new Intent(ConfirmationRedeemPointsActivity.this, LoyaltyActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}