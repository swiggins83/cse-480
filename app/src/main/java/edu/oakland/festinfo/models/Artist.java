package edu.oakland.festinfo.models;

import android.graphics.drawable.Drawable;

public class Artist {

    private String name;
    private Drawable image;
    private double rating;

    public Artist(String name, Drawable image, double rating) {
        this.name = name;
        this.image = image;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public Drawable getImage() {
        return image;
    }

    public double getRating() {
        return rating;
    }

}
