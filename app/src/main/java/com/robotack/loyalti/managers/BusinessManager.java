package com.robotack.loyalti.managers;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.robotack.loyalti.helpers.ApiConstants;
import com.robotack.loyalti.models.AdsBannerModel;
import com.robotack.loyalti.models.CalculateAmountClass;
import com.robotack.loyalti.models.CustomerAccountsModel;
import com.robotack.loyalti.models.CustomerDataModel;
import com.robotack.loyalti.models.CustomerHistoryModel;
import com.robotack.loyalti.models.GenralModel;
import com.robotack.loyalti.models.StepsInfoModel;
import com.robotack.loyalti.utilities.Utils;

import java.util.HashMap;
import java.util.Map;

import static com.robotack.loyalti.helpers.ApiConstants.calculateAmountAPI;
import static com.robotack.loyalti.helpers.ApiConstants.gainAPI;
import static com.robotack.loyalti.helpers.ApiConstants.redeemAPI;
import static com.robotack.loyalti.helpers.ApiConstants.stepsInfo;

public class BusinessManager {

    public void getUserInfoApiCall(Context context,String token, final ApiCallResponse callResponse) {
        Map<String, String> Params = new HashMap<>();
        
        String url = ApiConstants.getUserInfoAPI + new Utils().getUserId(context);
        ConnectionManager.GET(context, url, Params,token, new ApiCallResponse() {
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

    public void getAdsBanner(Context context, String token, final ApiCallResponse callResponse) {
        Map<String, String> Params = new HashMap<>();
        String url = ApiConstants.AdBanner;
        ConnectionManager.GET(context, url, Params,token, new ApiCallResponse() {
            @Override
            public void onSuccess(Object responseObject, String responseMessage) {
                try {
                    Gson gson = new Gson();
                    String json = responseObject.toString();
                    AdsBannerModel parseObject = gson.fromJson(json, AdsBannerModel.class);
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


    public void getUserHistoryApiCall(Context context,String token, final ApiCallResponse callResponse) {
        Map<String, String> Params = new HashMap<>();
        String url = ApiConstants.getUserHistoryAPI + new Utils().getIdentifierValue(context) + "?type=all&pageNumber=1&pageSize=1000";
        ConnectionManager.GET(context, url, Params,token, new ApiCallResponse() {
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

    public void getUserAccounts(Context context,String token, final ApiCallResponse callResponse) {
        Map<String, String> Params = new HashMap<>();
        String url = ApiConstants.getUserInfoAPI + new Utils().getIdentifierValue(context) + ApiConstants.getUserAccountAPI;
        ConnectionManager.GET(context, url, Params,token, new ApiCallResponse() {
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


    public void postCalcautePoints(Context context, JsonObject gsonObject,String token, final ApiCallResponse callResponse) {
        String url = calculateAmountAPI;
        new ConnectionManager().PostRAW(context, gsonObject, url,token, new ApiCallResponse() {
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

    public void redeemPoints(Context context, JsonObject gsonObject,String token, final ApiCallResponse callResponse) {
        String url = redeemAPI;
        new ConnectionManager().PostRAW(context, gsonObject, url,token, new ApiCallResponse() {
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

    public void gainPoints(Context context, JsonObject gsonObject,String token, final ApiCallResponse callResponse) {
        String url = gainAPI;
        new ConnectionManager().PostRAW(context, gsonObject, url,token, new ApiCallResponse() {
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

    public void getStepsInfo(Context context,String token, final ApiCallResponse callResponse) {
        String url = stepsInfo;
        Map<String, String> Params = new HashMap<>();
        new ConnectionManager().GET(context, url, Params,token, new ApiCallResponse() {
            @Override
            public void onSuccess(Object responseObject, String responseMessage) {
                try {
                    Gson gson = new Gson();
                    String json = responseObject.toString();
                    StepsInfoModel parseObject = gson.fromJson(json, StepsInfoModel.class);
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
