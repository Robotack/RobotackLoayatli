package com.robotack.loyalti.ui.Activites;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.robotack.loyalti.R;
import com.robotack.loyalti.models.GetTokenListener;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);
        LoyaltyActivity.init(this, "UAT-00281253", "en", new GetTokenListener() {
            @Override
            public String getToken() {
                return "test";
            }
        });
    }
}