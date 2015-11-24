package edu.oakland.festinfo.utils;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;

import org.androidannotations.annotations.EBean;

@EBean(scope = EBean.Scope.Singleton)
public class FacebookUtil {

    public static void getUserName(GraphRequest.Callback callback) {

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                null,
                HttpMethod.GET,
                callback
        ).executeAsync();

    }
}
