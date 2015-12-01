package edu.oakland.festinfo.models;

import java.util.Date;

public class ArtistShowTime {

    private String artistName;
    private Date startTime;
    private Date endTime;
    private String location;
    private byte[] artistImageData;

    public ArtistShowTime(String artistName, Date startTime, Date endTime, String location, byte[] artistImageData) {
        this.artistName = artistName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.artistImageData = artistImageData;
    }

    public String getArtistName() {
        return artistName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public String getLocation() {
        return location;
    }

    public byte[] getArtistImageData() {
        return artistImageData;
    }

}
