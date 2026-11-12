package com.example.munchly;

import android.app.Application;

import com.example.munchly.data.MunchlyRepository;

public class MunchlyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MunchlyRepository.getInstance().init(this);
    }
}
