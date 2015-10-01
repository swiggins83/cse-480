package edu.oakland.festinfo.utils;

import com.parse.LogInCallback;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.androidannotations.annotations.EBean;

@EBean(scope = EBean.Scope.Singleton)
public class ParseUtil {

    public void signUp(String email, String username, String password, SignUpCallback callback) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            ParseUser.logOut();
        }
        ParseUser user = new ParseUser();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(callback);
    }

    public void signIn(String username, String password, LogInCallback callback) {
        ParseUser.logInInBackground(username, password, callback);
    }

}