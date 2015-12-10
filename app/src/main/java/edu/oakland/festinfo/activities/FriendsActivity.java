package edu.oakland.festinfo.activities;

import android.graphics.BitmapFactory;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.oakland.festinfo.R;
import edu.oakland.festinfo.adapters.FriendsFriendAdapter;
import edu.oakland.festinfo.models.User;
import edu.oakland.festinfo.utils.FacebookUtil;
import edu.oakland.festinfo.utils.ParseUtil;

/**
 * Created by Steven on 11/29/2015.
 */
@EActivity(R.layout.activity_friends)
public class FriendsActivity extends BaseActivity {

    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById(R.id.friends_list)
    ListView friendsListView;

    @Bean
    ParseUtil parseUtil;

    @AfterViews
    void init() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Friends");
        colorUpArrow();

        showSpinner("Loading friends...");

        FacebookUtil.getUserFriends(this, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                try {
                    JSONArray friendData = response.getJSONObject().getJSONArray("data");
                    List<String> friendNames = new ArrayList<>();
                    for (int i=0; i < friendData.length(); i++) {
                        JSONObject jsonObject = (JSONObject) friendData.get(i);
                        friendNames.add((String) jsonObject.get("name"));
                    }
                    collectUsers(friendNames);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dismissSpinner();
            }
        });
    }

    private void collectUsers(final List<String> friendNames) {
        final List<User> friendList = new ArrayList<>();
        parseUtil.retrieveUsersWithPhotos(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject o : objects) {
                    String username = (String) o.get("username");
                    if (friendNames.contains(username)) {
                        byte[] bitmapData = new byte[0];
                        try {
                            bitmapData = ((ParseFile) o.get("portrait")).getData();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        ParseGeoPoint parseGeoPoint = o.getParseGeoPoint("currentLocation");
                        boolean isSharingLocation = (parseGeoPoint.getLatitude() != 0 && parseGeoPoint.getLatitude() != 0);
                        User user = new User(
                                username,
                                (bitmapData.length > 0) ? BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length) : null,
                                isSharingLocation);

                        friendList.add(user);
                    }
                }
                setUpList(friendList);
            }
        });
    }

    private void setUpList(List<User> friendsList) {
        friendsListView.setAdapter(new FriendsFriendAdapter(this, R.layout.friends_friend_list_cell, friendsList));
    }

}
