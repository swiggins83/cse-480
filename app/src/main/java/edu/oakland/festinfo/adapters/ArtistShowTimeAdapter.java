package edu.oakland.festinfo.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
import edu.oakland.festinfo.activities.ArtistProfileActivity_;
import edu.oakland.festinfo.activities.BaseActivity;
import edu.oakland.festinfo.activities.MapPageActivity_;
import edu.oakland.festinfo.models.ArtistShowTime;

public class ArtistShowTimeAdapter extends ArrayAdapter<ArtistShowTime> {

    private Context context;
    private int layoutResource;
    private List<ArtistShowTime> showTimes;

    public ArtistShowTimeAdapter(Context context, int layoutResource, List<ArtistShowTime> showTimes) {
        super(context, layoutResource, showTimes);
        this.context = context;
        this.layoutResource = layoutResource;
        this.showTimes = showTimes;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View row = convertView;
        final ArtistShowTimeHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResource, parent, false);
            holder = new ArtistShowTimeHolder();
            row.setTag(holder);
        } else {
            holder = (ArtistShowTimeHolder) row.getTag();
        }

        holder.nameTextView = (TextView) row.findViewById(R.id.show_name_text);
        holder.timeTextView = (TextView) row.findViewById(R.id.show_time_text);
        holder.locationTextView = (TextView) row.findViewById(R.id.show_location_text);
        holder.artistImage = (CircleImageView) row.findViewById(R.id.artist_location_image);

        final ArtistShowTime artistShowTime = showTimes.get(position);
        holder.nameTextView.setText(artistShowTime.getArtistName());
        Date startDate = artistShowTime.getStartTime();
        Date endDate = artistShowTime.getStartTime();

        String startTime = new SimpleDateFormat("E h:mm a").format(startDate);
        String endTime = new SimpleDateFormat("E h:mm a").format(endDate);

        String playingTimes = startTime + " - " + endTime;
        holder.timeTextView.setText(playingTimes);
        holder.locationTextView.setText(artistShowTime.getLocation());

        try {
            File artistImage = File.createTempFile("artistImage", "" + position);
            FileOutputStream fileOutputStream = new FileOutputStream(artistImage);
            fileOutputStream.write(artistShowTime.getArtistImageData());
            Picasso.with(context).load(artistImage).into(holder.artistImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        holder.artistImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ArtistProfileActivity_
                            .intent(context)
                            .extra("artistName", artistShowTime.getArtistName())
                            .extra("artistImageData", artistShowTime.getArtistImageData())
                            .extra("pastIntent", ((Activity) context).getIntent())
                            .start();
                } catch (Exception e) {
                    ((BaseActivity) context).showSnackBar(context, "Something went wrong!");
                }
            }
        });

        return row;
    }

    static class ArtistShowTimeHolder {
        TextView nameTextView;
        TextView timeTextView;
        TextView locationTextView;
        CircleImageView artistImage;
    }

}
