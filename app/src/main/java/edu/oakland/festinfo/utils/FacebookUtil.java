package edu.oakland.festinfo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.androidannotations.annotations.EBean;

import edu.oakland.festinfo.activities.HomePageActivity_;

@EBean(scope = EBean.Scope.Singleton)
public class FacebookUtil {

    public static void getAnyUserInfo(String userId, GraphRequest.GraphJSONObjectCallback callback) {

        Log.d("HEYID", "" + userId);

        GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/{" + userId + "}",
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d("HEY", "" + response.getRawResponse());
                    }
                }
        ).executeAsync();

    }

    public static void getCurrentUserInfo(GraphRequest.GraphJSONObjectCallback callback) {

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                callback);
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,cover,picture,id");
        request.setParameters(parameters);
        request.executeAsync();

    }

    public static void getUserFriends(final Context context, GraphRequest.Callback callback) {

        if (AccessToken.getCurrentAccessToken() != null) {

            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/friends",
                    null,
                    HttpMethod.GET,
                    callback
            ).executeAsync();

        } else {
            View rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar.make(rootView, "HEY", Snackbar.LENGTH_LONG)
                    .setText("Not logged into Facebook")
                    .setAction("Connect", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ParseFacebookUtils.linkInBackground(ParseUser.getCurrentUser(), AccessToken.getCurrentAccessToken());
                            HomePageActivity_
                                    .intent(context)
                                    .flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .start();
                        }
                    })
                    .show();
        }

    }

}
