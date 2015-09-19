package edu.oakland.festinfo.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import edu.oakland.festinfo.R;

public class FestivalAppIntro extends AppIntro {

    @Override
    public void init(Bundle savedInstanceState) {

                addSlide(AppIntroFragment.newInstance("First Slide", "Description", R.mipmap.ic_launcher, Color.parseColor("#2196F3")));
        addSlide(AppIntroFragment.newInstance("Second Slide", "Second Description", R.mipmap.ic_launcher, Color.parseColor("#3F51B5")));
        addSlide(AppIntroFragment.newInstance("Third Slide", "Third Description", R.mipmap.ic_launcher, Color.parseColor("#3F51B5")));
        addSlide(AppIntroFragment.newInstance("Fourth Slide", "Fourth Description", R.mipmap.ic_launcher, Color.parseColor("#3F51B5")));

        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));
        showSkipButton(true);
        showDoneButton(true);

    }

    private void loadMainActivity(){
        RegistrationActivity_
                .intent(this)
                .start();
    }
    @Override
    public void onSkipPressed(){

        loadMainActivity();

    }
    @Override
    public void onDonePressed(){

        loadMainActivity();
    }

    public void getStarted(View v)
    {
        loadMainActivity();
    }
}
