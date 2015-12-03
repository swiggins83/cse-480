package edu.oakland.festinfo.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import edu.oakland.festinfo.R;
import edu.oakland.festinfo.utils.ParseUtil;
import edu.oakland.festinfo.utils.StringUtils;

/**
 * Created by Steven on 12/2/2015.
 */
@EActivity(R.layout.activity_artist_profile)
public class ArtistProfileActivity extends BaseActivity {

    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById(R.id.artist_profile_header)
    TextView profileHeader;
    @ViewById(R.id.header_image)
    ImageView headerImage;
    @ViewById(R.id.artist_favorite_fab)
    FloatingActionButton floatingActionButton;

    @Extra
    String artistName;
    @Extra
    byte[] artistImageData;
    @Extra
    Intent pastIntent;

    @AfterViews
    void init() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Artist");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profileHeader.setText(artistName);
        Bitmap artistImage = BitmapFactory.decodeByteArray(artistImageData, 0, artistImageData.length);
        headerImage.setImageBitmap(artistImage);

    }

    @Click(R.id.artist_favorite_fab)
    public void favoriteArtist() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        Object favorites = currentUser.get("favorites");
        List<String> favoriteArtists = new ArrayList<>();
        if (favorites != null) {
            favoriteArtists = (List<String>) favorites;
            if (!favoriteArtists.contains(artistName)) {
                favoriteArtists.add(artistName);
                currentUser.put("favorites", favoriteArtists);
                currentUser.saveInBackground();
                String newArtistName = artistName.replace(" ", "");
                ParseInstallation.getCurrentInstallation().saveInBackground();
                ParseInstallation installation = new ParseInstallation();
                ParsePush push = new ParsePush();
                push.subscribeInBackground(newArtistName);
                installation.saveInBackground();
	    }
        } else {
            favoriteArtists.add(artistName);
            currentUser.put("favorites", favoriteArtists);
            currentUser.saveInBackground();
            String newArtistName = artistName.replace(" ", "");
            ParseInstallation.getCurrentInstallation().saveInBackground();
            ParseInstallation installation = new ParseInstallation();
            ParsePush push = new ParsePush();
            push.subscribeInBackground(newArtistName);
            installation.saveInBackground();
        }
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.cardview_dark_background)));
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
