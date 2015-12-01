package edu.oakland.festinfo.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.oakland.festinfo.R;
import edu.oakland.festinfo.utils.FacebookUtil;

@EActivity(R.layout.activity_main)
public class HomePageActivity extends BaseActivity {

    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById(R.id.navigation_view)
    NavigationView navigationView;
    @ViewById(R.id.drawer)
    DrawerLayout drawerLayout;
    @ViewById(R.id.profile_header)
    CardView profileHeader;
    @ViewById(R.id.username)
    TextView usernameTextView;
    @ViewById(R.id.profile_image)
    CircleImageView profileImage;

    @Override
    public void onResume() {
        super.onResume();
        // check if user exists
        if (ParseUser.getCurrentUser().getUsername() == null) {
            LoginActivity_
                    .intent(this)
                    .flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
                    .start();
        } else {
            // check if user linked to facebook
            if (AccessToken.getCurrentAccessToken() != null) {
                fetchFacebookData();
            } else {
                // personalize app with parse info instead
                setUsernameText(ParseUser.getCurrentUser().getUsername());
            }
        }
    }

    private void fetchFacebookData() {

        FacebookUtil.getCurrentUserInfo(new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                setHeaderName(response);
                setProfileImage(response);
                setProfileHeader(response);
            }
        });

    }

    private void setProfileHeader(GraphResponse response) {
        try {
            Uri uri = Uri.parse(response.getJSONObject()
                    .getJSONObject("cover")
                    .getString("source"));
            Picasso.with(HomePageActivity.this).load(uri).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    profileHeader.setBackground(new BitmapDrawable(getResources(), bitmap));
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setProfileImage(GraphResponse response) {
        try {
            Uri uri = Uri.parse(response.getJSONObject()
                    .getJSONObject("picture")
                    .getJSONObject("data")
                    .getString("url"));
            Picasso.with(HomePageActivity.this).load(uri).placeholder(R.drawable.ic_account_circle).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    profileImage.setImageBitmap(bitmap);

                    // convert bitmap to byte array
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    byte[] bitmapData = bos.toByteArray();

                    ParseFile pf = new ParseFile(bitmapData);
                    ParseUser.getCurrentUser().put("portrait", pf);
                    ParseUser.getCurrentUser().saveInBackground();

                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) { }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) { }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setHeaderName(GraphResponse response) {
        try {
            String userName = (String) response.getJSONObject().get("name");
            ParseUser.getCurrentUser().setUsername(userName);
            setUsernameText(userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setUsernameText(String userName) {
        usernameTextView.setText(userName);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @AfterViews
    void init() {

        setSupportActionBar(toolbar);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                menuItem.setChecked(true);
                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {

                    //Replacing the main content with chosen Fragment;
                    case R.id.search:
                        switchFragment(new SearchPageActivity(), "Search");
                        return true;
                    case R.id.directions:
                        switchFragment(new DirectionsPageActivity(), "Directions");
                        return true;
                    case R.id.weather:
                        switchFragment(new WeatherPageActivity(), "Weather");
                        return true;
                    case R.id.tickets:
                        switchFragment(new TicketsPageActivity(), "Tickets");
                        return true;
                    case R.id.notifications:
                        switchFragment(new NotificationsPageActivity(), "Notifications");
                        return true;
                    case R.id.logout:
                        ParseUser.logOut();
                        LoginManager.getInstance().logOut();
                        LoginActivity_
                                .intent(HomePageActivity.this)
                                .flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                .start();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

    }

    public void switchFragment(Fragment f, String label) {
        Toast.makeText(getApplicationContext(), label + " Selected", Toast.LENGTH_SHORT).show();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, f);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Click(R.id.profile_header)
    public void launchUserProfileActivity() {
        UserProfileActivity_
                .intent(this)
                .start();
    }

    @Click(R.id.map_button)
    public void launchMapActivity() {
        MapPageActivity_
                .intent(this)
                .start();
    }

    @Click(R.id.friends_button)
    public void launchFriendsActivity() {
        FriendsActivity_
                .intent(this)
                .start();
    }

    @Click(R.id.favorites_button)
    public void launchScheduleActivity() {
        ScheduleActivity_
                .intent(this)
                .start();
    }

    @Click(R.id.settings_button)
    public void launchSettingActivity(){
        SettingsPageActivity_
                .intent(this)
                .start();
    }

    @Click(R.id.favorites_button)
    public void showFavorites() {
        ArtistDetailActivity_
                .intent(this)
                .start();
    }



}
