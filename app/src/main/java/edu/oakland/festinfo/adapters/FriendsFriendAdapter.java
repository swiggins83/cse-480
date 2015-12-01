package edu.oakland.festinfo.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.oakland.festinfo.R;
import edu.oakland.festinfo.models.FavoriteArtist;
import edu.oakland.festinfo.models.User;

public class FriendsFriendAdapter extends ArrayAdapter<User> {

    private Context context;
    private int layoutResource;
    private List<User> friends;

    public FriendsFriendAdapter(Context context, int layoutResource, List<User> friends) {
        super(context, layoutResource, friends);
        this.context = context;
        this.layoutResource = layoutResource;
        this.friends = friends;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        FriendLayoutHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResource, parent, false);
            holder = new FriendLayoutHolder();
            row.setTag(holder);
        } else {
            holder = (FriendLayoutHolder) row.getTag();
        }

        holder.nameTextView = (TextView) row.findViewById(R.id.friends_friend_name);
        holder.image = (CircleImageView) row.findViewById(R.id.friends_friend_image);

        User user = friends.get(position);

        holder.nameTextView.setText(user.getName());
        holder.image.setImageBitmap(user.getPortrait());

        return row;
    }

    static class FriendLayoutHolder {
        TextView nameTextView;
        CircleImageView image;
    }

}
