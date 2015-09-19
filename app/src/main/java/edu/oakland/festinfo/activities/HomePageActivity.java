package edu.oakland.festinfo.activities;

import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import edu.oakland.festinfo.R;

@EActivity(R.layout.activity_main)
public class HomePageActivity extends AppCompatActivity {

    @Click(R.id.sign_up)
    public void signUpFormLauncher() {
        RegistrationActivity_
                .intent(this)
                .start();
    }
}