package edu.oakland.festinfo.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.oakland.festinfo.R;
import edu.oakland.festinfo.activities.BaseActivity;
import edu.oakland.festinfo.activities.MapPageActivity_;
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
        FriendLayoutHolder holder;

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
        holder.locationImage = (ImageView) row.findViewById(R.id.friends_friend_shared);

        final User user = friends.get(position);

        holder.nameTextView.setText(user.getName());
        holder.image.setImageBitmap(user.getPortrait());

        Drawable locationIcon = user.getIsSharingLocation() ?
                context.getDrawable(R.drawable.share_location_button_icon_on) :
                context.getDrawable(R.drawable.share_location_button_icon_off);

        holder.locationImage.setImageDrawable(locationIcon);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getIsSharingLocation()) {
                    MapPageActivity_
                            .intent(context)
                            .extra("friendToNavigateTo", user.getName())
                            .extra("pastIntent", ((Activity) context).getIntent())
                            .flags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                            .start();

                } else {
                    ((BaseActivity) context).showSnackBar(context, user.getName() + " isn't sharing their location!");
                }
            }
        });

        return row;
    }

    static class FriendLayoutHolder {
        TextView nameTextView;
        CircleImageView image;
        ImageView locationImage;
    }

}
