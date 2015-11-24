package edu.oakland.festinfo.activities;

import android.content.Intent;
import android.util.Log;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.LoginButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.oakland.festinfo.R;
import edu.oakland.festinfo.utils.ParseUtil;
import edu.oakland.festinfo.utils.StringUtils;

@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    @ViewById(R.id.username_edittext)
    EditText usernameEditText;
    @ViewById(R.id.password_edittext)
    EditText passwordEditText;

    @ViewById(R.id.facebook_login_button)
    LoginButton loginButton;

    @Bean
    ParseUtil parseUtil;

    @AfterViews
    void init() {
        FacebookSdk.sdkInitialize(this.getApplicationContext());
    }

    @Click(R.id.facebook_login_button)
    public void login() {

        ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, null, new LogInCallback() {
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
    public void signIn() {

        if (!usernameEditText.getText().toString().isEmpty() &&
                !passwordEditText.getText().toString().isEmpty()) {

            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            parseUtil.signIn(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e != null) {
                        showSnackBar(LoginActivity.this, e.getLocalizedMessage());
                    } else {
                        HomePageActivity_
                                .intent(LoginActivity.this)
                                .flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
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
                .intent(LoginActivity.this)
                .flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                .start();
    }

}
