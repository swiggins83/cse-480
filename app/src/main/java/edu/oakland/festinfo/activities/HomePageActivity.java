package edu.oakland.festinfo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.parse.ParseUser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import edu.oakland.festinfo.R;

@EActivity(R.layout.activity_main)
public class HomePageActivity extends AppCompatActivity {

    @ViewById(R.id.current_user)
    TextView currentUserTextView;

    @AfterViews
    public void init() {
        ParseUser parseUser = new ParseUser();
        currentUserTextView.setText(parseUser.getCurrentUser().getUsername());
    }


    @Click(R.id.sign_in)
    public void signIn() {
        LoginActivity_
                .intent(this)
                .start();
    }

}