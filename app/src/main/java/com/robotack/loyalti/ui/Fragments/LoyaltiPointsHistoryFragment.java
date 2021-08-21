package com.robotack.loyalti.ui.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.robotack.loyalti.R;
import com.robotack.loyalti.models.CustomerHistoryModel;
import com.robotack.loyalti.ui.Adapters.PointHistoryTypesAdapter;

import java.util.ArrayList;
import java.util.List;

public class LoyaltiPointsHistoryFragment extends Fragment {
    private View rootView = null;
    private Context context;
    private List<CustomerHistoryModel.Item> data = new ArrayList<>();
    RecyclerView historyRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        View rootView = inflater.inflate(R.layout.fragment_point_history, container, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(lp);
        context = getActivity();
        data = (List<CustomerHistoryModel.Item>) getArguments().getSerializable("key");
        historyRecyclerView = rootView.findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        historyRecyclerView.setAdapter(new PointHistoryTypesAdapter(getActivity(),data));
        return rootView;
    }
}