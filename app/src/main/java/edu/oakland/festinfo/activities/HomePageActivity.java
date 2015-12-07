package edu.oakland.festinfo.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.oakland.festinfo.R;
import edu.oakland.festinfo.utils.FacebookUtil;
import edu.oakland.festinfo.utils.ParseUtil;

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

    @ViewById(R.id.image_slider)
    SliderLayout sliderLayout;

    @ViewById(R.id.map_button)
    RelativeLayout mapButton;
    @ViewById(R.id.friends_button)
    RelativeLayout friendsButton;
    @ViewById(R.id.artists_button)
    RelativeLayout artistsButton;
    @ViewById(R.id.schedule_button)
    RelativeLayout scheduleButton;

    @Bean
    ParseUtil parseUtil;

    private boolean imagesLoaded = false;

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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!imagesLoaded) {
            loadImageInto(getDrawable(R.drawable.maps), mapButton);
            loadImageInto(getDrawable(R.drawable.festfriends), friendsButton);
            loadImageInto(getDrawable(R.drawable.dj), artistsButton);
            loadImageInto(getDrawable(R.drawable.schedule), scheduleButton);
            imagesLoaded = true;
        }

    }

    public void loadImageInto(Drawable drawable, RelativeLayout button) {
        int w = button.getWidth();
        int h = button.getHeight();
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        b = Bitmap.createScaledBitmap(b, w, h, false);
        button.setBackground(new BitmapDrawable(getResources(), b));
    }

    @AfterViews
    void init() {

        setSupportActionBar(toolbar);

        navigationView.setCheckedItem(0);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                menuItem.setChecked(true);
                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {

                    // replacing the main content with chosen Fragment;
                    case R.id.home:
                        HomePageActivity_
                                .intent(HomePageActivity.this)
                                .start();
                        return true;
                    case R.id.weather:
                        switchFragment(new WeatherPageActivity(), "Weather");
                        return true;
                    case R.id.tickets:
                        switchFragment(new TicketsPageActivity(), "Tickets");
                        return true;
                    case R.id.settings_button:
                        SettingsPageActivity_
                                .intent(HomePageActivity.this)
                                .extra("pastIntent", HomePageActivity.this.getIntent())
                                .start();
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

        HashMap<String,Integer> file_maps = new HashMap<>();
        file_maps.put("", R.drawable.sherwood_forest);
        file_maps.put(" ", R.drawable.ef_image);
        file_maps.put("  ", R.drawable.explosion);
        file_maps.put("   ", R.drawable.stage);

        for(String name : file_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop);

            sliderLayout.addSlider(textSliderView);
        }

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

        MenuItem searchItem = menu.findItem(R.id.search_icon);

        SearchManager searchManager = (SearchManager) HomePageActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(HomePageActivity.this.getComponentName()));
        }

        return super.onCreateOptionsMenu(menu);
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
                .extra("pastIntent", this.getIntent())
                .start();
    }

    @Click(R.id.artists_button)
    public void launchArtistsActivity() {
        ArtistsPageActivity_
                .intent(this)
                .extra("pastIntent", this.getIntent())
                .start();
    }

    @Click(R.id.friends_button)
    public void launchFriendsActivity() {
        FriendsActivity_
                .intent(this)
                .start();
    }

    @Click(R.id.schedule_button)
    public void launchScheduleActivity() {
        ScheduleActivity_
                .intent(this)
                .start();
    }

}
