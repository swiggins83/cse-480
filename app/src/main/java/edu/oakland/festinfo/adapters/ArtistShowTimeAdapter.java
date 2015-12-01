package edu.oakland.festinfo.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseException;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.oakland.festinfo.R;
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
    public View getView(int position, View convertView, ViewGroup parent) {
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
        Date startTime = artistShowTime.getStartTime();
        Date endTime = artistShowTime.getStartTime();

        String startDay = new SimpleDateFormat("EE").format(startTime);
        String endDay = new SimpleDateFormat("EE").format(endTime);

        String playingTimes = startDay + " " + startTime.getHours() + ":" + startTime.getMinutes() + " - " + endDay + " " + endTime.getHours() + ":" + endTime.getMinutes();
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

        return row;
    }

    static class ArtistShowTimeHolder {
        TextView nameTextView;
        TextView timeTextView;
        TextView locationTextView;
        CircleImageView artistImage;
    }

}
