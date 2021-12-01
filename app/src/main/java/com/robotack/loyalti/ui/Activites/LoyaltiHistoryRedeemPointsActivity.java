package com.robotack.loyalti.ui.Activites;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;


import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;
import com.robotack.loyalti.R;
import com.robotack.loyalti.helpers.LanguageHelper;
import com.robotack.loyalti.managers.ApiCallResponse;
import com.robotack.loyalti.managers.BusinessManager;
import com.robotack.loyalti.models.CustomerHistoryModel;

import com.robotack.loyalti.ui.Adapters.PointsHistoryPagerAdapter;
import com.robotack.loyalti.utilities.Utils;

import java.util.List;


public class LoyaltiHistoryRedeemPointsActivity extends FragmentActivity {
    ImageView backIcon;
    TextView toolbarTitle;
    Activity activity = this;
    TabLayout historyTabs;
    ViewPager historyViewPager;
    ShimmerFrameLayout mShimmerViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Utils().updateLangauge(this);
        setContentView(R.layout.activity_history);
        setToolbarView();
        getCustomerHistory();
    }

    private void setToolbarView ()
    {
        mShimmerViewContainer = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmer();
        historyTabs = (TabLayout) findViewById(R.id.historyTabs);
        historyViewPager = (ViewPager) findViewById(R.id.historyViewPager);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(R.string.history);
        backIcon = (ImageView) findViewById(R.id.backIcon);
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

    private void getCustomerHistory()
    {
        new BusinessManager().getUserHistoryApiCall(this,LoyaltyActivity.getTokenListener.getToken(), new ApiCallResponse() {
            @Override
            public void onSuccess(Object responseObject, String responseMessage) {
                try {
                    CustomerHistoryModel customerHistoryModel = (CustomerHistoryModel) responseObject;
                    setPointsHistoryAdapter(customerHistoryModel.getData());
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmer();
                } catch (Exception e) {

                }
            }
            @Override
            public void onFailure(String errorResponse) {

            }
        });
    }
    private void setPointsHistoryAdapter(List<CustomerHistoryModel.Datum> data) {
        PointsHistoryPagerAdapter  pointsHistoryPagerAdapter = new PointsHistoryPagerAdapter(getSupportFragmentManager(), activity,data);
        historyViewPager.setAdapter(pointsHistoryPagerAdapter);
        historyTabs.setupWithViewPager(historyViewPager);
        for (int tabCounter = 0; tabCounter < historyTabs.getTabCount(); tabCounter++) {
            TabLayout.Tab tab = historyTabs.getTabAt(tabCounter);
            tab.setCustomView(pointsHistoryPagerAdapter.getTabView(tabCounter));
        }
        TextView tvNameCategory = (TextView) historyTabs.getRootView().findViewById(R.id.tvNameCategory);
        LinearLayout categoryView = (LinearLayout) historyTabs.getRootView().findViewById(R.id.categoryView);
        categoryView.setBackground(activity.getDrawable(R.drawable.red_pager_shape));
        tvNameCategory.setTextColor(ContextCompat.getColor(this, R.color.whitesdk));
        historyTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView tvNameCategory = (TextView) tab.getCustomView().findViewById(R.id.tvNameCategory);
                tvNameCategory.setTextColor(ContextCompat.getColor(activity, R.color.whitesdk));
                LinearLayout categoryView = (LinearLayout) tab.getCustomView().findViewById(R.id.categoryView);
                categoryView.setBackground(activity.getDrawable(R.drawable.red_pager_shape));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tvNameCategory = (TextView) tab.getCustomView().findViewById(R.id.tvNameCategory);
                tvNameCategory.setTextColor(ContextCompat.getColor(activity, R.color.tab_text_colorsdk));
                LinearLayout categoryView = (LinearLayout) tab.getCustomView().findViewById(R.id.categoryView);
                categoryView.setBackground(activity.getDrawable(R.drawable.gray_pager_shape));
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}