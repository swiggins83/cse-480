package edu.oakland.festinfo.models;

import java.net.URI;
import java.util.Date;

public class Artist {

    private String name;
    private String location;
    private Date playTime;
    private byte[] imageData;
    private String imageLink;
    private double rating;

    public Artist(String name, String location, Date playTime, byte[] imageData, String imageLink, double rating) {
        this.name = name;
        this.location = location;
        this.playTime = playTime;
        this.imageData = imageData;
        this.imageLink = imageLink;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public Date getPlayTime() {
        return playTime;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public String getImageLink() {
        return imageLink;
    }

    public double getRating() {
        return rating;
    }

}
