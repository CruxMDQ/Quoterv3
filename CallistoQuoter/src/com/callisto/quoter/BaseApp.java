package com.callisto.quoter;

import android.app.Application;

public class BaseApp extends Application {
    Status mTest;

    @Override
    public void onCreate() {
        super.onCreate();

        mTest = new Status();
    }

    public Status getObserver() {
        return mTest;
    }

}

