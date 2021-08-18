package com.robotack.loyatli.managers;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.robotack.loyatli.helpers.ApiConstants;
import com.robotack.loyatli.models.CalculateAmountClass;
import com.robotack.loyatli.models.CustomerAccountsModel;
import com.robotack.loyatli.models.CustomerDataModel;
import com.robotack.loyatli.models.CustomerHistoryModel;
import com.robotack.loyatli.models.GenralModel;
import com.robotack.loyatli.utilities.Utils;


import java.util.HashMap;
import java.util.Map;

import static com.robotack.loyatli.helpers.ApiConstants.calculateAmountAPI;
import static com.robotack.loyatli.helpers.ApiConstants.redeemAPI;


public class BusinessManager {

    public void getUserInfoApiCall(Context context, final ApiCallResponse callResponse) {
        Map<String, String> Params = new HashMap<>();
        String url = ApiConstants.getUserInfoAPI +new Utils().getUserId(context);
        ConnectionManager.GET(context,url, Params, new ApiCallResponse() {
            @Override
            public void onSuccess(Object responseObject, String responseMessage) {
                try {
                    Gson gson = new Gson();
                    String json = responseObject.toString();
                    CustomerDataModel parseObject = gson.fromJson(json, CustomerDataModel.class);
                    callResponse.onSuccess(parseObject, responseMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(String errorResponse) {
                callResponse.onFailure(errorResponse);
            }
        });
    }


    public void getUserHistoryApiCall(Context context, final ApiCallResponse callResponse) {
        Map<String, String> Params = new HashMap<>();
        String url = ApiConstants.getUserHistoryAPI +new Utils().getIdentifierValue(context)+"?type=all&pageNumber=1&pageSize=1000";
        ConnectionManager.GET(context,url, Params, new ApiCallResponse() {
            @Override
            public void onSuccess(Object responseObject, String responseMessage) {
                try {
                    Gson gson = new Gson();
                    String json = responseObject.toString();
                    CustomerHistoryModel parseObject = gson.fromJson(json, CustomerHistoryModel.class);
                    callResponse.onSuccess(parseObject, responseMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(String errorResponse) {
                callResponse.onFailure(errorResponse);
            }
        });
    }

    public void getUserAccounts(Context context, final ApiCallResponse callResponse) {
        Map<String, String> Params = new HashMap<>();
        String url = ApiConstants.getUserInfoAPI +new Utils().getIdentifierValue(context) + ApiConstants.getUserAccountAPI;
        ConnectionManager.GET(context,url, Params, new ApiCallResponse() {
            @Override
            public void onSuccess(Object responseObject, String responseMessage) {
                try {
                    Gson gson = new Gson();
                    String json = responseObject.toString();
                    CustomerAccountsModel parseObject = gson.fromJson(json, CustomerAccountsModel.class);
                    callResponse.onSuccess(parseObject, responseMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(String errorResponse) {
                callResponse.onFailure(errorResponse);
            }
        });
    }


    public void postCalcautePoints(Context context, JsonObject gsonObject, final ApiCallResponse callResponse) {
        String url = calculateAmountAPI;
        new ConnectionManager().PostRAW(context,gsonObject,url, new ApiCallResponse() {
            @Override
            public void onSuccess(Object responseObject, String responseMessage) {
                try {
                    Gson gson = new Gson();
                    String json = responseObject.toString();
                    CalculateAmountClass parseObject = gson.fromJson(json, CalculateAmountClass.class);
                    callResponse.onSuccess(parseObject, responseMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(String errorResponse) {
                callResponse.onFailure(errorResponse);
            }
        });
    }

    public void redeemPoints(Context context, JsonObject gsonObject, final ApiCallResponse callResponse) {
        String url = redeemAPI;
        new ConnectionManager().PostRAW(context,gsonObject,url, new ApiCallResponse() {
            @Override
            public void onSuccess(Object responseObject, String responseMessage) {
                try {
                    Gson gson = new Gson();
                    String json = responseObject.toString();
                    GenralModel parseObject = gson.fromJson(json, GenralModel.class);
                    callResponse.onSuccess(parseObject, responseMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(String errorResponse) {
                callResponse.onFailure(errorResponse);
            }
        });
    }

}
