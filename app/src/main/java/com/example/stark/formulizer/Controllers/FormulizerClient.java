package com.example.stark.formulizer.Controllers;

import android.content.Context;

import com.example.stark.formulizer.Utilities.PreferenceReader;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Stark on 01-03-2017.
 */

public class FormulizerClient {
    private static Retrofit retrofit = null;
    private static String TOKEN = "";
    private static String API_PATH = "";
    private static OkHttpClient newClient;
    private Context context;
    public FormulizerClient(Context context){
        this.context = context;
        PreferenceReader prf = new PreferenceReader(context);
        TOKEN = "JWT "+ prf.getTOKEN();
        API_PATH = prf.getApiPath();
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request orignal = chain.request();
                Request.Builder requestBuilder = orignal.newBuilder()
                        .header("Authorization",TOKEN);
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        newClient = client.build();
    }
    public Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(API_PATH)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(newClient)
                    .build();
        }
        return retrofit;
    }
}
