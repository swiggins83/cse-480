package edu.oakland.festinfo.models;

import android.graphics.Bitmap;

public class User {
    public String name;
    public Bitmap portrait;

    public User(String name, Bitmap portrait) {
        this.name = name;
        this.portrait = portrait;
    }

    public String getName() {
        return name;
    }

    public Bitmap getPortrait() {
        return portrait;
    }
}
