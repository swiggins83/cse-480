package edu.oakland.festinfo.activities;

import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import edu.oakland.festinfo.R;
import edu.oakland.festinfo.utils.ParseUtil;

@EActivity(R.layout.activity_registration)
public class RegistrationActivity extends BaseActivity {

    @ViewById(R.id.email_edittext)
    EditText emailEditText;
    @ViewById(R.id.username_edittext)
    EditText usernameEditText;
    @ViewById(R.id.password_edittext)
    EditText passwordEditText;

    @Bean
    ParseUtil parseUtil;

    @Click(R.id.facebook_login_button)
    public void login() {

        List<String> permissions = new ArrayList<>();
        permissions.add("user_friends");
        permissions.add("user_photos");
        permissions.add("user_likes");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(RegistrationActivity.this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                } else if (user.isNew()) {
                    loadHomePage();
                } else {
                    // user logged in through facebook
                    loadHomePage();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Click(R.id.submit_button)
    public void submitForm() {

        if (!emailEditText.getText().toString().isEmpty()    &&
                !usernameEditText.getText().toString().isEmpty() &&
                !passwordEditText.getText().toString().isEmpty()) {

            String email = emailEditText.getText().toString();
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            final Activity parent = this;
            parseUtil.signUp(email, username, password, new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        showSnackBar(parent, e.getLocalizedMessage());
                    } else {
                        HomePageActivity_
                                .intent(parent)
                                .start();
                    }
                }
            });
        } else {
            showSnackBar(this, getString(R.string.incomplete_data));
        }

    }

    public void loadHomePage() {
        HomePageActivity_
                .intent(RegistrationActivity.this)
                .flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                .start();
    }
}
