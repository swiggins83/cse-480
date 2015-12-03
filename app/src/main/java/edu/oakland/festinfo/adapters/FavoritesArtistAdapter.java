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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.oakland.festinfo.R;
import edu.oakland.festinfo.activities.ArtistProfileActivity_;
import edu.oakland.festinfo.models.Artist;
import edu.oakland.festinfo.models.FavoriteArtist;

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
        holder.image = (CircleImageView) row.findViewById(R.id.favorites_artist_image);

        final Artist favoriteArtist = favoriteArtists.get(position);
        holder.nameTextView.setText(favoriteArtist.getName());
        Drawable artistImage = new BitmapDrawable(BitmapFactory.decodeByteArray(favoriteArtist.getImageData(), 0, favoriteArtist.getImageData().length));
        holder.image.setImageDrawable(artistImage);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArtistProfileActivity_
                        .intent(context)
                        .extra("artistName", favoriteArtist.getName())
                        .extra("artistImageData", favoriteArtist.getImageData())
                        .extra("pastIntent", ((Activity) context).getIntent())
                        .start();
            }
        });

        return row;
    }

    static class FavoriteArtistHolder {
        TextView nameTextView;
        TextView nextPerformanceTextView;
        CircleImageView image;
    }

}
