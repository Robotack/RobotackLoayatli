package com.robotack.loyalti.managers;

import android.content.Context;


import com.google.gson.JsonObject;
import com.robotack.loyalti.helpers.LanguageHelper;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

import static com.robotack.loyalti.helpers.ApiConstants.SDK_VERSION;
import static com.robotack.loyalti.managers.ConnectionManager.APIService.BASE_URL;


/**
 * Created by moayed on 12/10/17.
 */

public class ConnectionManager {


    public static void GET(Context context, String URl, Map<String, String> Params, final ApiCallResponse callResponse) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(new ConnectionManager().getUserHeader(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService service = retrofit.create(APIService.class);
        Call<ResponseBody> call = service.GET(URl, Params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body() == null) {
                            callResponse.onSuccess(response.body().toString(), response.message());
                        } else {
                            callResponse.onSuccess(response.body().string(), response.message());
                        }
                    } else {
                        callResponse.onFailure("error");
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callResponse.onFailure(t.toString());
            }
        });

    }

    public void PostRAW(Context context , JsonObject requestBody, String URl, final ApiCallResponse callResponse) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(new ConnectionManager().getUserHeader(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ConnectionManager.APIService  service = retrofit.create(ConnectionManager.APIService .class);
        Call<ResponseBody> call = service.POST_RAW(URl, requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful())
                    {
                        callResponse.onSuccess(response.body().string(), response.code() + "");
                    }else {
                        callResponse.onFailure(response.code()+"");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callResponse.onFailure(t.toString());
            }
        });
    }


    public OkHttpClient getUserHeader(Context context) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.connectTimeout(1000, TimeUnit.SECONDS)
                .readTimeout(1000, TimeUnit.SECONDS).build();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder();
                builder.method(original.method(), original.body());
                builder.header("Accept-Language", LanguageHelper.getCurrentLanguage(context)).addHeader("Authorization","Um9ib3RhY2tfdmlyZ2luXzIwMjA=").addHeader("sdk_version",SDK_VERSION).addHeader("android_os_version",android.os.Build.VERSION.SDK_INT+"");
                return chain.proceed(builder.build());
            }
        });
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient client = httpClient.build();
        return client;

    }

    public interface APIService {
//        public static String BASE_URL = "https://platform.robotack.com:8443/uDemo/api/v1/";
        public static String BASE_URL = "https://robotack.au.ngrok.io/AdminPortal/api/v1/";


        @GET()
        public Call<ResponseBody> GET(@Url String url, @QueryMap Map<String, String> params);


        @Headers({"Content-Type:application/x-www-form-urlencoded"})
        @FormUrlEncoded
        @POST
        public Call<ResponseBody> POST_FormUrlEncoded(@Url String url, @FieldMap Map<String, String> params);



        @POST()
        Call<ResponseBody> POST_RAW(@Url String url, @Body JsonObject requestBody);


        @FormUrlEncoded
        @POST
        public Call<ResponseBody> POST_AUTH(@Url String url, @FieldMap Map<String, String> params, @Header("Authorization") String token);
    }

}
