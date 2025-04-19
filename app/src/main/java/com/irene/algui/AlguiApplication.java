package com.irene.algui;

import android.app.Application;
import android.util.Log;


public class AlguiApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Main", "onCreate");
    }
}
