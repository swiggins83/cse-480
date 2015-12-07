package edu.oakland.festinfo.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.oakland.festinfo.R;
import edu.oakland.festinfo.activities.BaseActivity;
import edu.oakland.festinfo.activities.MapPageActivity_;
import edu.oakland.festinfo.models.ArtistShowTime;
import edu.oakland.festinfo.utils.StringUtils;

public class SimpleShowTimeAdapter extends ArrayAdapter<ArtistShowTime> {

    private Context context;
    private int layoutResource;
    private List<ArtistShowTime> showTimes;

    public SimpleShowTimeAdapter(Context context, int layoutResource, List<ArtistShowTime> showTimes) {
        super(context, layoutResource, showTimes);
        this.context = context;
        this.layoutResource = layoutResource;
        this.showTimes = showTimes;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View row = convertView;
        final SimpleShowTimeHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResource, parent, false);
            holder = new SimpleShowTimeHolder();
            row.setTag(holder);
        } else {
            holder = (SimpleShowTimeHolder) row.getTag();
        }

        holder.timeTextView = (TextView) row.findViewById(R.id.show_time_text);
        holder.locationTextView = (TextView) row.findViewById(R.id.show_location_text);

        final ArtistShowTime artistShowTime = showTimes.get(position);
        Date startDate = artistShowTime.getStartTime();
        Date endDate = artistShowTime.getStartTime();

        String startTime = new SimpleDateFormat("E h:mm a").format(startDate);
        String endTime = new SimpleDateFormat("E h:mm a").format(endDate);

        String playingTimes = "When: " + startTime + " - " + endTime;
        holder.timeTextView.setText(playingTimes);
        String location = "Where: " + StringUtils.toTitleCase(artistShowTime.getLocation());
        holder.locationTextView.setText(location);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapPageActivity_
                        .intent(context)
                        .extra("pastIntent", ((Activity) context).getIntent())
                        .extra("stageSelected", artistShowTime.getLocation())
                        .start();
            }
        });

        return row;
    }

    static class SimpleShowTimeHolder {
        TextView timeTextView;
        TextView locationTextView;
    }

}
