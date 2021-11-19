package com.robotack.loyalti.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;

import com.google.android.gms.common.GoogleApiAvailability;
import com.robotack.loyalti.helpers.ApiConstants;
import com.robotack.loyalti.helpers.LanguageHelper;
import com.robotack.loyalti.models.SecurityModel;

import java.security.spec.AlgorithmParameterSpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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
        Locale locale = new Locale(LanguageHelper.getCurrentLanguage(context));
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

    }

    public String getUserId(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return encryptWithoutTime(preferences.getString(custumerID, ""),context);
    }

    public  String encryptWithoutTime(String value, Context context) {
        String plainText = value;
        String escapedString;
        try {
            byte[] key = ApiConstants.KEY_AES.getBytes("UTF-8");
            byte[] ivs = ApiConstants.IV_AES.getBytes("UTF-8");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(ivs);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, paramSpec);
            escapedString = Base64.encodeToString(cipher.doFinal(plainText.getBytes("UTF-8")), Base64.DEFAULT).trim();
            escapedString = escapedString.replace("+", "__plus__");
            escapedString = escapedString.replace("/", "__slash__");
            escapedString = escapedString.replace("%", "__perc__");
            escapedString = escapedString.replace("=", "__equal__");
            return escapedString;
        } catch (Exception e) {

            return value;
        }
    }



    public static String decryptData(String text) throws Exception {
        text = text.replace("__plus__","+");
        text = text.replace( "__slash__","/");
        text = text.replace("__perc__", "%");
        text = text.replace("__equal__", "=");
        byte[] encryted_bytes = Base64.decode(text, Base64.DEFAULT);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        byte[] static_key = ApiConstants.KEY_AES.getBytes();
        byte[] ivs = ApiConstants.IV_AES.getBytes();
        SecretKeySpec keySpec = new SecretKeySpec(static_key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivs);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decrypted = cipher.doFinal(encryted_bytes);
        String result = new String(decrypted);
        return result;
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
