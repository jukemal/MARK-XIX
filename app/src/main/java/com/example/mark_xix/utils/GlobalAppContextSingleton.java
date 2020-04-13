package com.example.mark_xix.utils;

import android.content.Context;

public class GlobalAppContextSingleton {
    private static GlobalAppContextSingleton mInstance;
    private Context context;

    public static GlobalAppContextSingleton getInstance() {
        if (mInstance == null) mInstance = getSync();
        return mInstance;
    }

    private static synchronized GlobalAppContextSingleton getSync() {
        if (mInstance == null) mInstance =
                new GlobalAppContextSingleton();
        return mInstance;
    }

    public void initialize(Context context) {
        this.context = context;
    }

    public Context getApplicationContext() {
        return context;
    }
}