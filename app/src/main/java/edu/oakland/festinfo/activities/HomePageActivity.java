package edu.oakland.festinfo.activities;

import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import edu.oakland.festinfo.R;

@EActivity(R.layout.activity_main)
public class HomePageActivity extends AppCompatActivity {

    @Click(R.id.sign_up)
    public void signUp() {
        RegistrationActivity_
                .intent(this)
                .start();
    }

    @Click(R.id.sign_in)
    public void signIn() {
        LoginActivity_
                .intent(this)
                .start();
    }

}