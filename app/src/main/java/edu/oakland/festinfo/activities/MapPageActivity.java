package edu.oakland.festinfo.activities;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import edu.oakland.festinfo.R;

@EActivity(R.layout.activity_map)
public class MapPageActivity extends BaseActivity implements OnMapReadyCallback {

    @AfterViews
    void init() {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        LatLng sydney = new LatLng(-33.867, 151.206);
        double lat = -33.867;
        double lon = 151.206;

        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        mMap.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia")
                .position(sydney));

    }



}
