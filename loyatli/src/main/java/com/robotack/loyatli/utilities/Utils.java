package com.robotack.loyatli.utilities;

import android.content.Context;
import android.graphics.Typeface;

import com.google.android.gms.common.GoogleApiAvailability;
import com.robotack.loyatli.helpers.SharedPreferencesHelper;


import java.text.SimpleDateFormat;
import java.util.Date;

import static com.robotack.loyatli.helpers.PrefConstant.custumerID;
import static com.robotack.loyatli.helpers.PrefConstant.identifierValue;


public class Utils {

    public void setUserID(String userID, Context context) {
        SharedPreferencesHelper.putSharedPreferencesString(context, custumerID, userID);
    }

    public void setIdentifierValue(String userID, Context context) {
        SharedPreferencesHelper.putSharedPreferencesString(context, identifierValue, userID);
    }


    public String getUserId(Context context) {
        return SharedPreferencesHelper.getSharedPreferencesString(context, custumerID, "");
    }
    public String getIdentifierValue(Context context) {
        return SharedPreferencesHelper.getSharedPreferencesString(context, identifierValue, "");
    }

    public static Typeface SetTFace(Context context) {
        Typeface font;
        font = Typeface.createFromAsset(context.getAssets(), "capitalbank-medium.ttf");
        return font;
    }

    public String getDate(long time) {
        SimpleDateFormat sdf = null;
        try {
            sdf = new SimpleDateFormat("dd/MM/yyyy");
        } catch (Exception e) {
            return "";
        }
        try {
            sdf.format(new Date(time));
            return sdf.format(time);
        } catch (Exception e) {
            return "";
        }

    }

    public boolean isGMSAvailable(Context context) {
        try {


            GoogleApiAvailability gms = GoogleApiAvailability.getInstance();
            int isGMS = gms.isGooglePlayServicesAvailable(context);
            return isGMS == com.google.android.gms.common.ConnectionResult.SUCCESS;
        } catch (Exception e) {
            return true;
        }

    }
}
