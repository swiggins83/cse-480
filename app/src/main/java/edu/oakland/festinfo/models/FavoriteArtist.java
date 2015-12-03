package edu.oakland.festinfo.models;

import android.graphics.drawable.Drawable;

public class FavoriteArtist extends Artist {

    private String nextPerformance;

    public FavoriteArtist(String name, byte[] image, double rating, String nextPerformance) {
        super(name, image, rating);
        this.nextPerformance = nextPerformance;
    }

    public String getNextPerformance() {
        return nextPerformance;
    }

}