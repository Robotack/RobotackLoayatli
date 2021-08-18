package com.robotack.loyalti.ui.Adapters;

import android.app.Activity;
import android.content.res.Resources;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.robotack.loyalti.R;
import com.robotack.loyalti.models.CustomerHistoryModel;
import com.robotack.loyalti.utilities.Utils;

import java.util.List;

public class PointHistoryTypesAdapter extends RecyclerView.Adapter<PointHistoryTypesAdapter.CustomViewHolder> {
    private Activity mActivity;
    List<CustomerHistoryModel.Item> data;

    public PointHistoryTypesAdapter(Activity activity, List<CustomerHistoryModel.Item> data) {
        this.mActivity = activity;
        this.data = data;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_history, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int position) {

        try {
            holder.points.setText(data.get(position).getPoints().toString() + " " + mActivity.getResources().getString(R.string.point_));
        } catch (Resources.NotFoundException e) {

        }
        try {
            holder.redeemType.setText(Html.fromHtml(data.get(position).getType().toString()));
        } catch (Exception e) {

        }
        try {
            holder.details.setText(data.get(position).getTitle().toString());
        } catch (Exception e) {

        }
        try {
            holder.date.setText(new Utils().getDate(Long.parseLong(data.get(position).getDate().toString())));
        } catch (NumberFormatException e) {

        }
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView redeemType;
        TextView points;
        TextView details;

        CustomViewHolder(View itemView) {
            super(itemView);

            points = (TextView) itemView.findViewById(R.id.points);
            redeemType = (TextView) itemView.findViewById(R.id.redeemType);
            date = (TextView) itemView.findViewById(R.id.date);
            details = (TextView) itemView.findViewById(R.id.details);
        }
    }
}