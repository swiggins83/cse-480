package edu.oakland.festinfo.models;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Devin on 11/2/2015.
 */
public class CombinedMarker {

    private Marker marker;
    private Circle circle;
    private String identification;
    private String markerType;

    public CombinedMarker(Marker m, Circle c, String id, String type) {
        marker = m;
        circle = c;
        identification = id;
        markerType = type;
    }

    public Marker getMarker(){
        return marker;
    }

    public Circle getCircle() {
        return circle;
    }

    public String getIdentification(){
        return identification;
    }

    public String getMarkerType() { return markerType;}

    public void setMarker(Marker m) {
        marker = m;
    }

    public void setCircle(Circle c) {
        circle = c;
    }

    public void setIdentification(String id){
        identification = id;
    }

    public void setMarkerType(String type) {markerType = type;}

}
