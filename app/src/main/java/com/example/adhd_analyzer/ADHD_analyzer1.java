package com.example.adhd_analyzer;

import android.app.Application;
import android.content.Context;

public class ADHD_analyzer1 extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = context.getApplicationContext();
    }
}
