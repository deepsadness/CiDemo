package com.example.cry.cidemo;

import android.app.Application;

/**
 * Created by Cry on 2018/1/18.
 */

public class App extends Application {
    private static App application;

    @Override
    public void onCreate() {
        super.onCreate();
        this.application = this;
    }

    public static App getApplication() {
        return application;
    }
}
