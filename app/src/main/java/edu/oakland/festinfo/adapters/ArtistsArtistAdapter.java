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
import edu.oakland.festinfo.models.Artist;

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

        holder.imageView = (CircleImageView) row.findViewById(R.id.artist_image);
        holder.nameTextView = (TextView) row.findViewById(R.id.artist_name);
        holder.ratingTextView = (TextView) row.findViewById(R.id.artist_rating);

        Artist artist = artists.get(position);
        Drawable artistImage = new BitmapDrawable(BitmapFactory.decodeByteArray(artist.getImageData(), 0, artist.getImageData().length));
        holder.imageView.setImageDrawable(artistImage);
        holder.nameTextView.setText(artist.getName());
        holder.ratingTextView.setText(String.valueOf(artist.getRating()));

        return row;
    }

    static class ArtistLayoutHolder {
        CircleImageView imageView;
        TextView nameTextView;
        TextView ratingTextView;
    }

}
