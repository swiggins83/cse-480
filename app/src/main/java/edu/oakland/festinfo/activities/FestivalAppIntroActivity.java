package edu.oakland.festinfo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

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
        checkIfLoggedIn();
    }

    @Override
    public void init(Bundle savedInstanceState) {

        addSlide(AppIntroFragment.newInstance("Introduction",
                "Interactive maps to guide you to the fun",
                R.mipmap.effmap,
                getResources().getColor(R.color.material_blue_500)));
        addSlide(AppIntroFragment.newInstance("Introduction",
                "Get notified about your favorite artists",
                R.mipmap.push,
                getResources().getColor(R.color.wallet_holo_blue_light)));
        addSlide(AppIntroFragment.newInstance("Introduction",
                "Place markers to find your way around",
                R.mipmap.markers,
                getResources().getColor(R.color.wallet_link_text_light)));
        addSlide(AppIntroFragment.newInstance("Introduction",
                "Easily find your friends and your campsite",
                R.mipmap.search,
                getResources().getColor(R.color.colorPrimary)));

        setSkipText("Login");
        setDoneText("Register");

        setBarColor(getResources().getColor(R.color.colorPrimaryLight));
        setSeparatorColor(getResources().getColor(R.color.colorAccent));

        showSkipButton(true);
        showDoneButton(true);

    }

    private void checkIfLoggedIn() {
        if (ParseUser.getCurrentUser() != null) {
            if (ParseUser.getCurrentUser().getUsername() != null) {
                HomePageActivity_
                        .intent(this)
                        .flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .start();
            }
        }
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
