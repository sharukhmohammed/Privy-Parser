package com.findprivy.privyparser.app;

import android.app.Application;
import android.content.res.Configuration;

import io.realm.Realm;

public class PrivyCustApp extends Application
{

    @Override
    public void onCreate()
    {
        super.onCreate();
        Realm.init(this);
    }

    @Override
    public void onLowMemory()
    {
        // This is called when the overall system is running low on memory,
        // and would like actively running processes to tighten their belts.
        // Overriding this method is totally optional!
        super.onLowMemory();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        // Called by the system when the device configuration changes while your component is running.
        // Overriding this method is totally optional!
        super.onConfigurationChanged(newConfig);
    }
}
