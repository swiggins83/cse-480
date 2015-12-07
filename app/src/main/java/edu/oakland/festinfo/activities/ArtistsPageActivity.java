package edu.oakland.festinfo.activities;

import android.app.ProgressDialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.oakland.festinfo.R;
import edu.oakland.festinfo.adapters.FavoritesArtistAdapter;
import edu.oakland.festinfo.models.Artist;
import edu.oakland.festinfo.utils.ParseUtil;

/**
 * Created by Steven on 11/30/2015.
 */
@EActivity(R.layout.activity_artists)
public class ArtistsPageActivity extends BaseActivity {

    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById(R.id.artists_favorites_list)
    ListView favoritesList;
    @ViewById(R.id.artists_artists_list)
    ListView artistsList;

    @Bean
    ParseUtil parseUtil;

    private FavoritesArtistAdapter favoritesArtistAdapter;
    private FavoritesArtistAdapter fullArtistAdapter;

    @AfterViews
    void init() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Artists");
        colorUpArrow();

        showSpinner("Loading artist list...");

        parseUtil.getAllArtists(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {

                    final List<Artist> artistList = new ArrayList<>();
                    for (ParseObject o : objects) {
                        String artistName = (String) o.get("name");
                        String artistLocation = (String) o.get("location");
                        Date artistPlayTime = new Date(o.getLong("startTimeDec10") * 1000);
                        byte[] artistImageData = new byte[0];
                        String artistImageLink = "";
                        try {
                            artistImageData = o.getParseFile("image").getData();
                            artistImageLink = o.getParseFile("image").getUrl();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }

                        Artist artist = new Artist(artistName, artistLocation, artistPlayTime, artistImageData, artistImageLink, 0.0);
                        List<String> artistNames = new ArrayList<>();
                        for (Artist a : artistList) {
                            artistNames.add(a.getName());
                        }
                        if (!artistNames.contains(artist.getName())) {
                            artistList.add(artist);
                        }
                    }

                    List<String> favoriteArtistStrings = parseUtil.getFavoriteArtists();
                    List<Artist> favoriteArtists = new ArrayList<>();
                    for (int i = 0; i < artistList.size(); i++) {
                        if (favoriteArtistStrings.contains(artistList.get(i).getName())) {
                            if (!favoriteArtists.contains(artistList.get(i))) {
                                favoriteArtists.add(artistList.get(i));
                            }
                        }
                    }

                    setUpFavoriteArtistList(favoriteArtists);
                    setUpFullArtistList(artistList);

                } else {
                    showSnackBar(ArtistsPageActivity.this, "Something went wrong. Try again in a bit.");
                }

                dismissSpinner();

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (favoritesArtistAdapter != null &&
                fullArtistAdapter != null &&
                favoritesList != null &&
                artistsList != null) {
            resetLists();
        }
    }

    @UiThread
    public void resetLists() {
        favoritesArtistAdapter.notifyDataSetChanged();
        fullArtistAdapter.notifyDataSetChanged();
        favoritesList.requestLayout();
        artistsList.requestLayout();
    }

    private void setUpFavoriteArtistList(List<Artist> favoriteArtists) {
        favoritesArtistAdapter = new FavoritesArtistAdapter(this, R.layout.favorites_artist_list_cell, favoriteArtists);
        favoritesList.setAdapter(favoritesArtistAdapter);
        setListViewHeightBasedOnChildren(favoritesList);
    }

    private void setUpFullArtistList(List<Artist> artistList) {
        fullArtistAdapter = new FavoritesArtistAdapter(this, R.layout.favorites_artist_list_cell, artistList);
        artistsList.setAdapter(fullArtistAdapter);
        setListViewHeightBasedOnChildren(artistsList);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

}
