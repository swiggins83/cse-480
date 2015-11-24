package edu.oakland.festinfo;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class ApplicationStarter extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "5NCv1Uqfleqxcn30mWAMFHYf8GaQGUeEl4Yma9Lk", "0XIJP8UiTzNc64qHAHE2EK01T2Z0j1eDZk8nfJLd");
        ParseFacebookUtils.initialize(this);

        ParseUser.enableRevocableSessionInBackground();

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}

