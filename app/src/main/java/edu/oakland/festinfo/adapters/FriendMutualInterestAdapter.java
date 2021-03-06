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

public class FriendMutualInterestAdapter extends ArrayAdapter<Artist> {

    private Context context;
    private int layoutResource;
    private List<Artist> mutualInterests;

    public FriendMutualInterestAdapter(Context context, int layoutResource, List<Artist> mutualInterests) {
        super(context, layoutResource, mutualInterests);
        this.context = context;
        this.layoutResource = layoutResource;
        this.mutualInterests = mutualInterests;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        FriendMutualInterestHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResource, parent, false);
            holder = new FriendMutualInterestHolder();
            row.setTag(holder);
        } else {
            holder = (FriendMutualInterestHolder) row.getTag();
        }

        holder.nameTextView = (TextView) row.findViewById(R.id.friend_mutual_interest_name);
        holder.image = (CircleImageView) row.findViewById(R.id.friend_mutual_interest_image);

        Artist artist = mutualInterests.get(position);
        holder.nameTextView.setText(artist.getName());
        Drawable artistImage = new BitmapDrawable(BitmapFactory.decodeByteArray(artist.getImageData(), 0, artist.getImageData().length));
        holder.image.setImageDrawable(artistImage);

        return row;
    }

    static class FriendMutualInterestHolder {
        TextView nameTextView;
        CircleImageView image;
    }

}
