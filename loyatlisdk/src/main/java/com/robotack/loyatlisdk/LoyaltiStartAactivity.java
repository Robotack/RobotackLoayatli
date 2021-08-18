package com.robotack.loyatlisdk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

public class LoyaltiStartAactivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loyalti_start_aactivity);

        Intent intent = new Intent();
        intent.setClassName(this, "com.robotack.loyalti.ui.Activites.LoyaltyActivity");
        startActivity(intent);
    }
}