package edu.oakland.festinfo.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.facebook.appevents.AppEventsLogger;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.parse.ParseUser;

import edu.oakland.festinfo.R;

/**
 * Created by Devin on 9/22/2015.
 */
public class FestivalAppIntroActivity extends AppIntro {

    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);

        if (ParseUser.getCurrentUser().getUsername() != null) {
            HomePageActivity_
                    .intent(this)
                    .flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .start();
        }
    }

    @Override
    public void init(Bundle savedInstanceState) {

        addSlide(AppIntroFragment.newInstance("Introduction", "Interactive map with custom markers that makes searching easy.", R.mipmap.effmap, Color.parseColor("red")));
        addSlide(AppIntroFragment.newInstance("Introduction", "Markers can be favorited and viewed on a seperate page to make finding your favorite artists easier.", R.mipmap.markers, Color.parseColor("blue")));
        addSlide(AppIntroFragment.newInstance("Introduction", "Ability to search for bands playing at the festival by artist an location.", R.mipmap.search, Color.parseColor("magenta")));
        addSlide(AppIntroFragment.newInstance("Introduction", "Notifications display as events happen to remind users about events going on.", R.mipmap.push, Color.parseColor("black")));

        setBarColor(Color.parseColor("grey"));
        setSeparatorColor(Color.parseColor("aqua"));
        showSkipButton(true);
        showDoneButton(true);

    }

    private void launchLoginActivity(){
        LoginActivity_
                .intent(this)
                .flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .start();
    }

    private void launchRegistrationActivity(){
        RegistrationActivity_
                .intent(this)
                .flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .start();
    }

    @Override
    public void onSkipPressed(){
        launchLoginActivity();
    }

    @Override
    public void onDonePressed(){
        launchRegistrationActivity();
    }

}
