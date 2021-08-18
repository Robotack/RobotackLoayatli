package com.robotack.loyatli.ui.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.robotack.loyalti.R;
import com.robotack.loyalti.models.CustomerHistoryModel;
import com.robotack.loyalti.ui.Fragments.PointsHistoryFragment;

import java.io.Serializable;
import java.util.List;

public class PointsHistoryPagerAdapter extends FragmentPagerAdapter {
    Context context;
    List<CustomerHistoryModel.Datum> data;

    public PointsHistoryPagerAdapter(FragmentManager fm, Context context, List<CustomerHistoryModel.Datum> data) {
        super(fm);
        this.context = context;
        this.data = data;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("key", (Serializable) data.get(position).getItems());
        PointsHistoryFragment insideCategoryFragment = new PointsHistoryFragment();
        insideCategoryFragment.setArguments(bundle);
        return insideCategoryFragment;
    }
    @Override
    public int getCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return data.get(position).getTitle();
    }
    public View getTabView(int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.pager_item, null);
        TextView tvNameCategory = (TextView) view.findViewById(R.id.tvNameCategory);
        LinearLayout categoryView = (LinearLayout) view.findViewById(R.id.categoryView);
        tvNameCategory.setText(data.get(position).getTitle());
        return view;
    }
}