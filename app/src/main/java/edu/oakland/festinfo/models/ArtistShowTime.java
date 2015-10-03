package edu.oakland.festinfo.models;

public class ArtistShowTime {

    public String date;
    public String time;
    public String location;

    public ArtistShowTime(String date, String time, String location) {
        this.date = date;
        this.time = time;
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

}
