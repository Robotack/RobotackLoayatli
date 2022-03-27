package com.robotack.loyalti.ui.Activites;

import static com.robotack.loyalti.helpers.PrefConstant.sdkLanguage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.robotack.loyalti.R;
import com.robotack.loyalti.helpers.PrefConstant;
import com.robotack.loyalti.managers.ApiCallResponse;
import com.robotack.loyalti.managers.BusinessManager;
import com.robotack.loyalti.models.CustomerDataModel;
import com.robotack.loyalti.models.GenralModel;
import com.robotack.loyalti.models.GetTokenListener;

import xyz.hasnat.sweettoast.SweetToast;

public class RegisterationActivity extends AppCompatActivity {
    ProgressBar progressBar;
    String userID = null;
    String LanguageValue = "en";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        LanguageValue = getIntent().getStringExtra("LanguageValue");
        userID = getIntent().getStringExtra("userID");
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        findViewById(R.id.registerBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerBtnClick();
            }
        });
    }

    private void registerBtnClick() {
        progressBar.setVisibility(View.VISIBLE);
        new BusinessManager().registerAPI(this, LoyaltyActivity.getTokenListener.getToken(), new ApiCallResponse() {
            @Override
            public void onSuccess(Object responseObject, String responseMessage) {
                GenralModel genralModel = null;
                try {
                    genralModel = (GenralModel) responseObject;
                    if (genralModel.getErrorCode() == 0) {
                        getCustomerInfo();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        try {
                            SweetToast.error(RegisterationActivity.this, genralModel.getDescriptionCode(), 3000);
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

            }
        });
    }

    private void getCustomerInfo() {
        new BusinessManager().getUserInfoApiCall(this, LoyaltyActivity.getTokenListener.getToken(), new ApiCallResponse() {
            @Override
            public void onSuccess(Object responseObject, String responseMessage) {
                progressBar.setVisibility(View.GONE);
                try {
                    CustomerDataModel customerDataModel = (CustomerDataModel) responseObject;
                    if (customerDataModel.getStatus().toString().equals("1") || customerDataModel.getStatus().toString().equals("2")) {
                        LoyaltyActivity.init(RegisterationActivity.this, userID, LanguageValue, new GetTokenListener() {
                            @Override
                            public String getToken() {
                                return LoyaltyActivity.getTokenListener.getToken();
                            }
                        });
                        finish();
                    }else {
                        SweetToast.error(RegisterationActivity.this, customerDataModel.getDescriptionCode(), 3000);

                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(String errorResponse) {
            }
        });
    }
}