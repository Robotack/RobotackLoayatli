package com.robotack.loyalti.applications;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.text.TextUtils;

import androidx.multidex.MultiDex;

import com.robotack.loyalti.helpers.SharedPrefConstants;

import java.util.Locale;

/**
 * Created by moayed on 12/16/17.
 */

public class SdkApplicationLoayalit extends Application {
    private static Context context;
    public void onCreate() {
        super.onCreate();
        SdkApplicationLoayalit.context = getApplicationContext();
        updateLanguage(context);
    }
    public static Context getAppContext() {
        return SdkApplicationLoayalit.context;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    public static void updateLanguage(Context ctx)
    {
        final SharedPreferences settings = context.getSharedPreferences(SharedPrefConstants.Language, 0);
        String lang = settings.getString(SharedPrefConstants.Locale, "en");
        updateLanguage(ctx, lang);
    }
    public static void updateLanguage(Context ctx, String lang)
    {
        SharedPreferences settings = getAppContext().getSharedPreferences(SharedPrefConstants.Language, MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putString(SharedPrefConstants.Locale, lang);
        prefEditor.commit();
        Configuration cfg = new Configuration();
        if (!TextUtils.isEmpty(lang)) {
            Locale locale = new Locale(lang);
            cfg.locale = locale;
        }
        else
            cfg.locale = Locale.getDefault();
        ctx.getResources().updateConfiguration(cfg, null);
    }
}