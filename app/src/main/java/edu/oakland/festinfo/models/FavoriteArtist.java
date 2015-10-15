package edu.oakland.festinfo.models;

import android.graphics.drawable.Drawable;

public class FavoriteArtist extends Artist {

    private String nextPerformance;

    public FavoriteArtist(String name, Drawable image, String nextPerformance) {
        super(name, image);
        this.nextPerformance = nextPerformance;
    }

    public String getNextPerformance() {
        return nextPerformance;
    }

}
