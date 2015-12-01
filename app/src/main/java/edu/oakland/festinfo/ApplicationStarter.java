package edu.oakland.festinfo;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class ApplicationStarter extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "5NCv1Uqfleqxcn30mWAMFHYf8GaQGUeEl4Yma9Lk", "0XIJP8UiTzNc64qHAHE2EK01T2Z0j1eDZk8nfJLd");
        ParseFacebookUtils.initialize(this);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParseUser.enableRevocableSessionInBackground();

        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);
    }
}

