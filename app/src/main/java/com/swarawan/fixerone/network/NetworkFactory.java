package com.swarawan.fixerone.network;

import com.swarawan.fixerone.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by rioswarawan on 12/12/17.
 */

public class NetworkFactory {

    private static final String FIXER_URL = "https://api.fixer.io";

    public static NetworkService create() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FIXER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client())
                .build();
        return retrofit.create(NetworkService.class);
    }

    private static OkHttpClient client() {
        return new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE)
                )
                .build();
    }
}
