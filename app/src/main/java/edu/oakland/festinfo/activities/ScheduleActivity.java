package edu.oakland.festinfo.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.oakland.festinfo.R;
import edu.oakland.festinfo.adapters.ArtistShowTimeAdapter;
import edu.oakland.festinfo.models.ArtistShowTime;
import edu.oakland.festinfo.utils.ParseUtil;
import edu.oakland.festinfo.utils.StringUtils;

/**
 * Created by Steven on 11/30/2015.
 */
@EActivity(R.layout.activity_schedule)
public class ScheduleActivity extends BaseActivity {

    @ViewById(R.id.schedule_artists_list)
    ListView artistListView;
    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @Bean
    ParseUtil parseUtil;

    @AfterViews
    void init() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        colorUpArrow();

        showSpinner("Loading schedule...");

        final List<ArtistShowTime> artistShowTimeList = new ArrayList<>();
        parseUtil.getArtistSchedule(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject o : objects) {

                        String artistName = (String) o.get("name");
                        Date startTime = new Date(o.getLong("startTimeDec3") * 1000);
                        Date endTime = new Date(o.getLong("endTimeDec3") * 1000);
                        String location = (String) o.get("location");
                        byte[] artistImageData = new byte[0];
                        try {
                            artistImageData = o.getParseFile("image").getData();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }

                        ArtistShowTime artistShowTime = new ArtistShowTime(
                                artistName,
                                startTime,
                                endTime,
                                StringUtils.toTitleCase(location),
                                artistImageData);

                        artistShowTimeList.add(artistShowTime);

                    }

                    setUpList(artistShowTimeList);
                } else {
                    Log.d("HEY", "" + e.getLocalizedMessage());
                }

                dismissSpinner();
            }
        });

    }

    public void setUpList(List<ArtistShowTime> artistShowTimes) {
        artistListView.setAdapter(new ArtistShowTimeAdapter(this, R.layout.artist_show_time, artistShowTimes));
    }

}
