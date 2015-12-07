package edu.oakland.festinfo.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.oakland.festinfo.R;
import edu.oakland.festinfo.models.Artist;
import edu.oakland.festinfo.utils.StringUtils;

public class ArtistsArtistAdapter extends ArrayAdapter<Artist> {

    private Context context;
    private int layoutResource;
    private List<Artist> artists;

    public ArtistsArtistAdapter(Context context, int layoutResource, List<Artist> artists) {
        super(context, layoutResource, artists);
        this.context = context;
        this.layoutResource = layoutResource;
        this.artists = artists;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ArtistLayoutHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResource, parent, false);
            holder = new ArtistLayoutHolder();
            row.setTag(holder);
        } else {
            holder = (ArtistLayoutHolder) row.getTag();
        }

        holder.nameTextView = (TextView) row.findViewById(R.id.favorites_artist_name);
        holder.nextPerformanceTextView = (TextView) row.findViewById(R.id.favorites_artist_next_performance);
        holder.imageView = (CircleImageView) row.findViewById(R.id.favorites_artist_image);

        Artist artist = artists.get(position);
        try {
            File artistImage = File.createTempFile("artistImage", "" + position);
            FileOutputStream fileOutputStream = new FileOutputStream(artistImage);
            fileOutputStream.write(artist.getImageData());
            Picasso.with(context).load(artistImage).into(holder.imageView);
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.nameTextView.setText(artist.getName());
        String playTime = new SimpleDateFormat("E h:mm a").format(artist.getPlayTime());
        holder.nextPerformanceTextView.setText(StringUtils.toTitleCase(artist.getLocation()) + " at " + playTime);

        return row;
    }

    static class ArtistLayoutHolder {
        CircleImageView imageView;
        TextView nameTextView;
        TextView nextPerformanceTextView;
    }

}
