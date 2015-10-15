package edu.oakland.festinfo.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.oakland.festinfo.R;
import edu.oakland.festinfo.models.FavoriteArtist;

public class FavoritesArtistAdapter extends ArrayAdapter<FavoriteArtist> {

    private Context context;
    private int layoutResource;
    private List<FavoriteArtist> favoriteArtists;

    public FavoritesArtistAdapter(Context context, int layoutResource, List<FavoriteArtist> favoriteArtists) {
        super(context, layoutResource, favoriteArtists);
        this.context = context;
        this.layoutResource = layoutResource;
        this.favoriteArtists = favoriteArtists;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
        holder.image = (TextView) row.findViewById(R.id.favorites_artist_image);

        FavoriteArtist favoriteArtist = favoriteArtists.get(position);
        holder.nameTextView.setText(favoriteArtist.getName());
        holder.nextPerformanceTextView.setText(favoriteArtist.getNextPerformance());
        holder.image.setImageDrawable(favoriteArtist.getImage());

        return row;
    }

    static class FavoriteArtistHolder {
        TextView nameTextView;
        TextView nextPerformanceTextView;
        CircleImageView image;
    }

}
