package edu.oakland.festinfo.activities;

import java.util.ArrayList;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import edu.oakland.festinfo.R;

/**
 * Created by Devin on 11/9/2015.
 */
public class MapTestActivity extends FragmentActivity implements OnCameraChangeListener {

    private GoogleMap mMap;

    ArrayList<Geofence> mGeofences;

    ArrayList<LatLng> mGeofenceCoordinates;

    ArrayList<Integer> mGeofenceRadius;

    private GeofenceStore mGeofenceStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maptest);

        mGeofences = new ArrayList<Geofence>();
        mGeofenceCoordinates = new ArrayList<LatLng>();
        mGeofenceRadius = new ArrayList<Integer>();

        mGeofenceCoordinates.add(new LatLng(43.042861, -87.911559));
        mGeofenceCoordinates.add(new LatLng(43.042998, -87.909753));
        mGeofenceCoordinates.add(new LatLng(43.040732, -87.921364));
        mGeofenceCoordinates.add(new LatLng(43.039912, -87.897038));

        mGeofenceRadius.add(100);
        mGeofenceRadius.add(50);
        mGeofenceRadius.add(160);
        mGeofenceRadius.add(160);

        mGeofences.add(new Geofence.Builder()
            .setRequestId("Performing Arts Center")
            .setCircularRegion(mGeofenceCoordinates.get(0).latitude, mGeofenceCoordinates.get(0).longitude, mGeofenceRadius.get(0).intValue())
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setLoiteringDelay(30000)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                    | Geofence.GEOFENCE_TRANSITION_DWELL
                    | Geofence.GEOFENCE_TRANSITION_EXIT).build());

        mGeofences.add(new Geofence.Builder()
                .setRequestId("Starbucks")
                .setCircularRegion(mGeofenceCoordinates.get(1).latitude, mGeofenceCoordinates.get(1).longitude, mGeofenceRadius.get(1).intValue())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(30000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_DWELL
                        | Geofence.GEOFENCE_TRANSITION_EXIT).build());

        mGeofences.add(new Geofence.Builder()
                .setRequestId("Milwaukee Public Museum")
                .setCircularRegion(mGeofenceCoordinates.get(2).latitude, mGeofenceCoordinates.get(2).longitude, mGeofenceRadius.get(2).intValue())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(30000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT).build());

        mGeofences.add(new Geofence.Builder()
                .setRequestId("Milwaukee Art Museum")
                .setCircularRegion(mGeofenceCoordinates.get(3).latitude, mGeofenceCoordinates.get(3).longitude, mGeofenceRadius.get(3).intValue())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(30000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT).build());

        mGeofenceStore = new GeofenceStore(this, mGeofences);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            setUpMapIfNeeded();
        } else {
            GooglePlayServicesUtil.getErrorDialog(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this), this, 0);
        }
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.039634, -87.908395), 14));

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setIndoorEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(this);
    }

    @Override
    public void onCameraChange(CameraPosition position) {
        for (int i = 0; i < mGeofenceCoordinates.size(); i++) {
            mMap.addCircle(new CircleOptions().center(mGeofenceCoordinates.get(i))
                .radius(mGeofenceRadius.get(i).intValue())
                .fillColor(0x40ff0000)
                .strokeColor(Color.TRANSPARENT).strokeWidth(2));
        }
    }

}
