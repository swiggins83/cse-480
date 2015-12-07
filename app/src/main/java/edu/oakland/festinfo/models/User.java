package edu.oakland.festinfo.models;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class User {
    public String name;
    public Bitmap portrait;
    public boolean isSharingLocation;

    public User(String name, Bitmap portrait, boolean isSharingLocation) {
        this.name = name;
        this.portrait = portrait;
        this.isSharingLocation = isSharingLocation;
    }

    public String getName() {
        return name;
    }

    public Bitmap getPortrait() {
        return portrait;
    }

    public boolean getIsSharingLocation() {
        return isSharingLocation;
    }
}
