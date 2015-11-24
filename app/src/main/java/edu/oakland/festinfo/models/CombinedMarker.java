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

    public CombinedMarker(Marker m, Circle c, String id) {
        marker = m;
        circle = c;
        identification = id;
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

    public void setMarker(Marker m) {
        marker = m;
    }

    public void setCircle(Circle c) {
        circle = c;
    }

    public void setIdentification(String id){
        identification = id;
    }

}
