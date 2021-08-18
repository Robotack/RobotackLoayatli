package com.robotack.loyatli.helpers;



import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by moayed on 11/27/17.
 */

public class LanguageHelper {

    public static String getCurrentLanguage (Context context)
    {
        final SharedPreferences settings = context.getSharedPreferences(SharedPrefConstants.Language, 0);
        String lang = settings.getString(SharedPrefConstants.Locale, "en");
        Locale locale =  context.getResources().getConfiguration().locale;
        if (lang.contains("en"))
        {
            return "en";
        }else
        {
            return "ar";
        }
    }


    public static void setTextLanguage(Context context, TextView view, String textEn, String textAr) {
        //ToDo remove
//
        if (getCurrentLanguage(context) == "ar")
        {
            view.setText(textAr);
            view.setGravity(Gravity.RIGHT | Gravity.CENTER);
        }else
        {
            view.setText(textEn);
            view.setGravity(Gravity.LEFT | Gravity.CENTER);
        }

    }

    public static void setTextLanguage(Context context, TextView view, String text) {
        if (getCurrentLanguage(context) == "ar")
        {
            view.setText(text);
            view.setGravity(Gravity.RIGHT | Gravity.CENTER);
        }else
        {
            view.setText(text);
            view.setGravity(Gravity.LEFT | Gravity.CENTER);
        }
    }


    public static void setTextLanguage(Context context, EditText view, String textAr, String textEn) {
        if (getCurrentLanguage(context) == "ar")
        {
            view.setText(textAr);
            view.setGravity(Gravity.RIGHT | Gravity.CENTER);
        }else
        {
            view.setText(textEn);
            view.setGravity(Gravity.LEFT | Gravity.CENTER);
        }
    }


    public static void setTextLanguage(Context context, Button view, String textEn, String textAr) {
        if (getCurrentLanguage(context) == "ar")
        {
            view.setText(textAr);
            view.setGravity(Gravity.CENTER);
        }else
        {
            view.setText(textEn);
            view.setGravity(Gravity.CENTER);
        }
    }
}
