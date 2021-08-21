package com.robotack.loyalti.ui.Activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.robotack.loyalti.R;
import com.robotack.loyalti.helpers.LanguageHelper;
import com.robotack.loyalti.managers.ApiCallResponse;
import com.robotack.loyalti.managers.BusinessManager;
import com.robotack.loyalti.models.CustomerAccountsModel;
import com.robotack.loyalti.models.CustomerDataModel;
import com.robotack.loyalti.models.RedeemModel;
import com.robotack.loyalti.utilities.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.robotack.loyalti.applications.SdkApplicationLoayalit.updateLanguage;

public class RedeemPointsActivity extends AppCompatActivity {
    ImageView backIcon;
    TextView toolbarTitle;
    TextView submitBtn;
    TextView currentPoints;
    TextView currentPointsValue;
    TextView pleaseEnter;
    EditText pointsValue;
    Spinner accountsSpinner;
    String accountID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateLanguage(this);
        setContentView(R.layout.activity_redeem_points);
        setupViews();
        setToolbarView();
        getUserAccounts();
    }

    private void setToolbarView ()
    {
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RedeemModel redeemModel = new RedeemModel();
                redeemModel.setAccount(accountID);
                redeemModel.setPoints(pointsValue.getText().toString());
                startActivity(new Intent(RedeemPointsActivity.this,ConfirmationRedeemPointsActivity.class).putExtra("redeemModel",redeemModel));
            }
        });
        toolbarTitle.setText(R.string.redeem);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (LanguageHelper.getCurrentLanguage(this).equals("ar"))
        {
            backIcon.setScaleX(-1);
        }

    }

    private void setupViews()
    {
        accountsSpinner = (Spinner) findViewById(R.id.accountsSpinner);
        backIcon = (ImageView) findViewById(R.id.backIcon);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        submitBtn = (TextView) findViewById(R.id.submitBtn);
        pointsValue = (EditText) findViewById(R.id.pointsValue);
        pointsValue.setTypeface(Utils.SetTFace(this));
        pleaseEnter = (TextView) findViewById(R.id.pleaseEnter);
        currentPoints = (TextView) findViewById(R.id.currentPoints);
        currentPointsValue = (TextView) findViewById(R.id.currentPointsValue);

        CustomerDataModel customerDataModel = null;
        try {
            customerDataModel = (CustomerDataModel) getIntent().getSerializableExtra("customerDataModel");
            currentPoints.setText(customerDataModel.getCurrentPoints().toString());
            currentPointsValue.setText(getResources().getString(R.string.jod).replace("xxx", customerDataModel.getCurrentPointsValue().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        accountsSpinner.getBackground().setColorFilter(ContextCompat.getColor(this,
                R.color.gray_color), PorterDuff.Mode.SRC_ATOP);


        pointsValue.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence chars, int start,
                                      int before, int count) {
                if(chars.length() != 0)
                {
                    submitBtn.setEnabled(true);
                    submitBtn.setAlpha(1f);
                }else {
                    submitBtn.setEnabled(false);
                    submitBtn.setAlpha(0.7f);
                }

            }
        });
    }
    private void getUserAccounts()
    {

        new BusinessManager().getUserAccounts(this, new ApiCallResponse() {
            @Override
            public void onSuccess(Object responseObject, String responseMessage) {
                CustomerAccountsModel customerAccountsModel = (CustomerAccountsModel) responseObject;
                if (customerAccountsModel != null)
                {
                    if (!customerAccountsModel.getData().isEmpty())
                    {
                        setAccountsData(customerAccountsModel.getData());
                    }
                }
            }

            @Override
            public void onFailure(String errorResponse) {
            }
        });
    }

    private void setAccountsData(List<CustomerAccountsModel.Datum> data)
    {
        List<String> array = new ArrayList<>();
        for (int counter = 0 ; counter < data.size(); counter ++)
        {
            array.add(data.get(counter).getTitle());
        }
        ArrayAdapter<String> arrayAdapter5 = new ArrayAdapter<String>(this, R.layout.account_item, array);
        accountsSpinner.setAdapter(arrayAdapter5);
        accountsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                accountID = data.get(position).getValue();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


}