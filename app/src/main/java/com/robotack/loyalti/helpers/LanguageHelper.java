package com.robotack.loyalti.helpers;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

import static com.robotack.loyalti.helpers.PrefConstant.identifierValue;


public class LanguageHelper {

    public static String getCurrentLanguage (Context context)
    {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String lang = preferences.getString(SharedPrefConstants.Language, "en");
        if (lang.contains("en"))
        {
            return "en";
        }else
        {
            return "ar";
        }
    }
}
