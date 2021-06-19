package com.example.netmodule;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    private Retrofit retrofit;
    private RequestApi requestService;

    private RetrofitManager(){
        create();
    }

    public static RetrofitManager getInstance(){
        return Holder.INSTANCE;
    }

    private static final class Holder{
        private static final RetrofitManager INSTANCE = new RetrofitManager();
    }

    public void create(){
        Retrofit.Builder builder =new  Retrofit.Builder();
        builder.baseUrl("https://www.baidu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        retrofit = builder.client(httpBuilder.build()).build();
        requestService = retrofit.create(RequestApi.class);
    }

    public RequestApi getRequestService(){
        return requestService;
    }
}
