package edu.oakland.festinfo.activities;

import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.oakland.festinfo.R;
import edu.oakland.festinfo.adapters.ArtistShowTimeAdapter;
import edu.oakland.festinfo.models.ArtistShowTime;
import edu.oakland.festinfo.utils.ParseUtil;

/**
 * Created by steven on 10/3/15.
 */

@EActivity(R.layout.activity_artist_detail)
public class ArtistDetailActivity extends BaseActivity {

    @ViewById(R.id.show_time_list)
    ListView showTimeList;

    @AfterViews
    void init() {

        List<ArtistShowTime> showTimes = new ArrayList<>();
        //showTimes.add(new ArtistShowTime("NOW", "fedFOI", "KJSADLKJALS"));

        ArtistShowTimeAdapter adapter = new ArtistShowTimeAdapter(this, R.layout.artist_show_time, showTimes);
        showTimeList.setAdapter(adapter);

    }

}
