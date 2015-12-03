package edu.oakland.festinfo.activities;

import android.util.Log;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
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

    @ViewById(R.id.artists_favorites_list)
    ListView favoritesList;
    @ViewById(R.id.artists_artists_list)
    ListView artistsList;

    @Bean
    ParseUtil parseUtil;

    @AfterViews
    void init() {

        parseUtil.getAllArtists(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                List<Artist> artistList = new ArrayList<Artist>();
                for (ParseObject o : objects) {
                    String artistName = (String) o.get("name");
                    byte[] artistImageData = new byte[0];
                    try {
                        artistImageData = o.getParseFile("image").getData();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }

                    Artist artist = new Artist(artistName, artistImageData, 0.0);
                    artistList.add(artist);
                }
                setUpList(artistList);

            }
        });

    }

    private void setUpList(List<Artist> artistList) {
        favoritesList.setAdapter(new FavoritesArtistAdapter(this, R.layout.favorites_artist_list_cell, artistList));
    }

}
