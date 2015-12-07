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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.oakland.festinfo.R;
import edu.oakland.festinfo.activities.ArtistProfileActivity_;
import edu.oakland.festinfo.activities.MapPageActivity_;
import edu.oakland.festinfo.models.Artist;
import edu.oakland.festinfo.utils.StringUtils;

public class FavoritesArtistAdapter extends ArrayAdapter<Artist> {

    private Context context;
    private int layoutResource;
    private List<Artist> favoriteArtists;

    public FavoritesArtistAdapter(Context context, int layoutResource, List<Artist> favoriteArtists) {
        super(context, layoutResource, favoriteArtists);
        this.context = context;
        this.layoutResource = layoutResource;
        this.favoriteArtists = favoriteArtists;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View row = convertView;
        FavoriteArtistHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResource, parent, false);
            holder = new FavoriteArtistHolder();
            row.setTag(holder);
        } else {
            holder = (FavoriteArtistHolder) row.getTag();
        }

        holder.nameTextView = (TextView) row.findViewById(R.id.favorites_artist_name);
        holder.nextPerformanceTextView = (TextView) row.findViewById(R.id.favorites_artist_next_performance);
        holder.imageView = (CircleImageView) row.findViewById(R.id.favorites_artist_image);

        final Artist favoriteArtist = favoriteArtists.get(position);
        holder.nameTextView.setText(favoriteArtist.getName());
        String playTime = new SimpleDateFormat("E h:mm a").format(favoriteArtist.getPlayTime());
        String nextPerformance = StringUtils.toTitleCase(favoriteArtist.getLocation()) + " at " + playTime;
        holder.nextPerformanceTextView.setText(nextPerformance);
        try {
            File artistImage = File.createTempFile("artistImage", "" + position);
            FileOutputStream fileOutputStream = new FileOutputStream(artistImage);
            fileOutputStream.write(favoriteArtist.getImageData());
            Picasso.with(context).load(artistImage).into(holder.imageView);
        } catch (IOException e) {
            e.printStackTrace();
        }

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapPageActivity_
                        .intent(context)
                        .extra("pastIntent", ((Activity) context).getIntent())
                        .extra("stageSelected", favoriteArtist.getLocation())
                        .start();
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArtistProfileActivity_
                        .intent(context)
                        .extra("artistName", favoriteArtist.getName())
                        .extra("artistImageURL", favoriteArtist.getImageLink())
                        .extra("pastIntent", ((Activity) context).getIntent())
                        .start();
            }
        });

        return row;
    }

    static class FavoriteArtistHolder {
        TextView nameTextView;
        TextView nextPerformanceTextView;
        CircleImageView imageView;
    }

}
