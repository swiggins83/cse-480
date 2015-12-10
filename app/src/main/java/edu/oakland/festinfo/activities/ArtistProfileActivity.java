package edu.oakland.festinfo.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.oakland.festinfo.R;
import edu.oakland.festinfo.adapters.SimpleShowTimeAdapter;
import edu.oakland.festinfo.models.ArtistShowTime;
import edu.oakland.festinfo.utils.ParseUtil;

/**
 * Created by Steven on 12/2/2015.
 */
@EActivity(R.layout.activity_artist_profile)
public class ArtistProfileActivity extends BaseActivity {

    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById(R.id.header_image)
    ImageView headerImage;
    @ViewById(R.id.artist_show_times)
    ListView artistShowTimeList;
    @ViewById(R.id.artist_favorite_fab)
    FloatingActionButton floatingActionButton;

    @Bean
    ParseUtil parseUtil;

    @Extra
    String artistName;
    @Extra
    String artistImageURL;
    @Extra
    byte[] artistImageData;
    @Extra
    Intent pastIntent;

    @AfterViews
    void init() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(artistName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        colorUpArrow();

        if (artistImageURL != null) {
            Picasso.with(this)
                    .load(artistImageURL)
                    .into(headerImage);
        } else if (artistImageData != null) {
            Bitmap b = BitmapFactory.decodeByteArray(artistImageData, 0, artistImageData.length);
            headerImage.setImageDrawable(new BitmapDrawable(getResources(), b));
        }

        final List<ArtistShowTime> showTimes = new ArrayList<>();
        parseUtil.getArtistInfo(artistName, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject o : objects) {
                    String location =  o.getString("location");
                    Date startDate = new Date(o.getLong("startTimeDec10") * 1000);
                    Date endDate = new Date(o.getLong("endTimeDec10") * 1000);
                    ArtistShowTime showTime = new ArtistShowTime(startDate, endDate, location);
                    showTimes.add(showTime);
                }

                setUpShowTimeList(showTimes);
            }
        });

        ParseUser currentUser = ParseUser.getCurrentUser();
        boolean favorited = false;
        for (Object favorite : currentUser.getList("favorites")) {
            if (artistName.equals(favorite)) {
                floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccentDark)));
                favorited = true;
                break;
            }
        }
    }

    private void setUpShowTimeList(List<ArtistShowTime> showTimes) {
        artistShowTimeList.setAdapter(new SimpleShowTimeAdapter(this, R.layout.simple_show_time, showTimes));
    }

    @Click(R.id.artist_favorite_fab)
    public void favoriteArtist() {
        final ParseUser currentUser = ParseUser.getCurrentUser();
        Object favorites = currentUser.get("favorites");
        List<String> favoriteArtists = new ArrayList<>();
        if (favorites != null) {
            favoriteArtists = (List<String>) favorites;
            if (!favoriteArtists.contains(artistName)) {
                favoriteArtists.add(artistName);
                currentUser.put("favorites", favoriteArtists);
                currentUser.saveInBackground();
                String newArtistName = artistName.replace(" ", "");
                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                ParsePush push = new ParsePush();
                push.subscribeInBackground(newArtistName);
                installation.saveInBackground();
                showSnackBar(this, "Favorited " + artistName + "!");
            } else {
                final List<String> finalFavoriteArtists = favoriteArtists;
                showSnackBarWithAction(this, artistName + " is already one of your favorites!", "Unfavorite?", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < finalFavoriteArtists.size(); i++) {
                            if (finalFavoriteArtists.get(i).equals(artistName)) {
                                finalFavoriteArtists.remove(i);
                            }
                        }
                        currentUser.put("favorites", finalFavoriteArtists);
                        currentUser.saveInBackground();
                        String newArtistName = artistName.replace(" ", "");
                        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                        ParsePush push = new ParsePush();
                        push.unsubscribeInBackground(newArtistName);
                        installation.saveInBackground();
                        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    }
                });
            }
        } else {
            favoriteArtists.add(artistName);
            currentUser.put("favorites", favoriteArtists);
            currentUser.saveInBackground();
            String newArtistName = artistName.replace(" ", "");
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            ParsePush push = new ParsePush();
            push.subscribeInBackground(newArtistName);
            installation.saveInBackground();
        }
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccentDark)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpTo(this, pastIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
