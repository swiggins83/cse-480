package edu.oakland.festinfo.utils;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
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

    public void changeEmail(String email, SaveCallback callback) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.setEmail(email);
        currentUser.saveInBackground(callback);
    }

    public void changeUsername(String username, SaveCallback callback) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.setUsername(username);
        currentUser.saveInBackground(callback);
    }

    public void changePassword() {
    }

    public void getArtistSchedule(FindCallback<ParseObject> callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Artist");
        query.whereExists("time");
        query.orderByAscending("time");
        query.findInBackground(callback);
    }

}
