package com.robotack.loyalti.ui.Adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.viewpager.widget.PagerAdapter;
import com.robotack.loyalti.R;
import com.robotack.loyalti.models.AdsBannerModel;
import java.util.List;

public class ImageSlideAdapter extends PagerAdapter {

    private Activity activity;
    List<AdsBannerModel.Datum> slider;

    public ImageSlideAdapter(Activity context, List<AdsBannerModel.Datum> slider) {
        this.activity = context;
        this.slider = slider;
    }

    @Override
    public int getCount() {
        return slider.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_image_slider_row, container, false);
        final ImageView sliderImageView = (ImageView) view.findViewById(R.id.sliderImageView);
        byte[] imageByteArray = new byte[0];
        try {
            imageByteArray = Base64.decode(slider.get(position).getImage(), Base64.DEFAULT);
        } catch (Exception e) {
        }
        Bitmap decodedByte = null;
        try {
            decodedByte = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        } catch (Exception e) {
        }
        try {
            sliderImageView.setImageBitmap(decodedByte);
        } catch (Exception e) {

        }

        container.addView(view);
        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }}

