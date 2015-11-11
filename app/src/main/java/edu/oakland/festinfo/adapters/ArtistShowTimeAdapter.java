package edu.oakland.festinfo.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
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
        ArtistShowTimeHolder holder = null;

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

        ArtistShowTime artistShowTime = showTimes.get(position);
        holder.nameTextView.setText(artistShowTime.getArtistName());
        holder.timeTextView.setText(artistShowTime.getTime().toString());
        holder.locationTextView.setText(artistShowTime.getLocation());

        Picasso.with(context).load(artistShowTime.getArtistImage()).into(holder.artistImage);

        return row;
    }

    static class ArtistShowTimeHolder {
        TextView nameTextView;
        TextView timeTextView;
        TextView locationTextView;
        CircleImageView artistImage;
    }

}
