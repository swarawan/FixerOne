package com.swarawan.fixerone;

import android.app.Application;
import android.content.Context;

import timber.log.Timber;

/**
 * Created by rioswarawan on 12/12/17.
 */

public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static Context getContext() {
        return context;
    }
}
