package com.robotack.loyalti.ui.Activites;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.robotack.loyalti.R;
import com.robotack.loyalti.helpers.LanguageHelper;

import com.robotack.loyalti.ui.Fragments.LoyaltiHMStepsFragment;
import com.robotack.loyalti.ui.Fragments.LoyaltiStepsFragment;
import com.robotack.loyalti.utilities.Utils;


public class LoyaltiStepsActivity extends AppCompatActivity {
    ImageView backIcon;
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Utils().updateLangauge(this);
        setContentView(R.layout.activity_steps_activity);
        setToolbarView();

        if (new Utils().isGMSAvailable(this)) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.coordinator, new LoyaltiStepsFragment()).commit();
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.coordinator, new LoyaltiHMStepsFragment()).commit();
        }

    }

    private void setToolbarView() {

        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(R.string.claim_steps);
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
    }
}