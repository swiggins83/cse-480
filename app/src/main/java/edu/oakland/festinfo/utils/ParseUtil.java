package edu.oakland.festinfo.utils;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import edu.oakland.festinfo.R;
import edu.oakland.festinfo.activities.HomePageActivity_;

@EBean(scope = EBean.Scope.Singleton)
public class ParseUtil {

    @Background
    public void signUp(final Context context, String email, String username, String password) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            ParseUser.logOut();
        }

        ParseUser user = new ParseUser();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    HomePageActivity_
                            .intent(context)
                            .start();
                } else {
                    View rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
                    Snackbar
                            .make(rootView, R.string.error_occurred, Snackbar.LENGTH_LONG)
                            .setText(StringUtils.capitalizeFirstLetter(e.getLocalizedMessage()))
                            .show();
                }
            }
        });
    }

    @Background
    public void signIn(final Context context, String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            View rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Snackbar
                            .make(rootView, R.string.login_occurred, Snackbar.LENGTH_LONG)
                            .setText(user.getUsername() + " logged in!")
                            .show();
                } else {
                    Snackbar
                            .make(rootView, R.string.error_occurred, Snackbar.LENGTH_LONG)
                            .setText(StringUtils.capitalizeFirstLetter(e.getLocalizedMessage()))
                            .show();
                }
            }
        });
    }

}