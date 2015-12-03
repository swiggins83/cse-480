package edu.oakland.festinfo.models;

public class Artist {

    private String name;
    private byte[] imageData;
    private double rating;

    public Artist(String name, byte[] imageData, double rating) {
        this.name = name;
        this.imageData = imageData;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public double getRating() {
        return rating;
    }

}
