package com.example.unreallover.mediaplayer.Utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by Unreal Lover on 2017/11/1.
 */

public class MyApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        context = getApplicationContext();
    }
    public static Context getContext() {
        return context;
    }
}