package com.robotack.loyalti.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.android.gms.common.GoogleApiAvailability;
import com.robotack.loyalti.helpers.LanguageHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.robotack.loyalti.helpers.PrefConstant.custumerID;
import static com.robotack.loyalti.helpers.PrefConstant.identifierValue;

public class Utils {

    public void setUserID(String userID, Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(custumerID, userID);
        edit.commit();
    }

    public void setIdentifierValue(String userID, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(identifierValue, userID);
        edit.commit();
    }
    public void updateLangauge(Context context)
    {
        Configuration cfg = new Configuration();
        if (!TextUtils.isEmpty(LanguageHelper.getCurrentLanguage(context))) {
            Locale locale = new Locale(LanguageHelper.getCurrentLanguage(context));
            cfg.locale = locale;
        }
        else
            cfg.locale = Locale.getDefault();
        context.getResources().updateConfiguration(cfg, null);
    }

    public String getUserId(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(custumerID, "");
    }
    public String getIdentifierValue(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(identifierValue, "");
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
