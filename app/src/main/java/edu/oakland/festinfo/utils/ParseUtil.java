package edu.oakland.festinfo.utils;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import edu.oakland.festinfo.R;

public class ParseUtil {

    public static void signUp(final Context context, String email, String username, String password) {
        ParseUser user = new ParseUser();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // new user successfully created
                } else {
                    View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
                    Snackbar
                            .make(rootView, R.string.error_occurred, Snackbar.LENGTH_LONG)
                            .show(); // Don’t forget to show!
                }
            }
        });
    }

}