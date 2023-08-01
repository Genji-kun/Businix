package com.example.businix;

import android.app.Application;

import com.cloudinary.android.MediaManager;

public class BusinixApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MediaManager.init(this);
    }
}
