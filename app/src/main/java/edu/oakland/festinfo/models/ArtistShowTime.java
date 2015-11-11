package edu.oakland.festinfo.models;

import android.net.Uri;

import java.util.Date;

public class ArtistShowTime {

    private String artistName;
    private Date time;
    private String location;
    private Uri artistImage;

    public ArtistShowTime(String artistName, Date time, String location, Uri artistImage) {
        this.artistName = artistName;
        this.time = time;
        this.location = location;
        this.artistImage = artistImage;
    }

    public String getArtistName() {
        return artistName;
    }

    public Date getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public Uri getArtistImage() {
        return artistImage;
    }

}
