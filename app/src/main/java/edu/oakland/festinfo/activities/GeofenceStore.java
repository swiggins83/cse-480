package edu.oakland.festinfo.activities;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;

/**
 * Created by Devin on 11/9/2015.
 */
public class GeofenceStore implements ConnectionCallbacks, OnConnectionFailedListener,
        ResultCallback<Status>, LocationListener {

    private final String TAG = this.getClass().getSimpleName();

    private Context mContext;

    private GoogleApiClient mGoogleApiClient;

    private PendingIntent mPendingIntent;

    private ArrayList<Geofence> mGeofences;

    private GeofencingRequest mGeofencingRequest;

    private LocationRequest mLocationRequest;

    public GeofenceStore(Context context, ArrayList<Geofence> geofences) {
        mContext = context;
        mGeofences = geofences;
        mPendingIntent = null;

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(10000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mGoogleApiClient.connect();

    }

    public void disconnect() {
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onResult(Status result) {
        if (result.isSuccess()) {
            Log.v(TAG, "Success!");
        } else if (result.hasResolution()) {
            Log.v(TAG, "Canceled");
        } else if (result.isInterrupted()) {
            Log.v(TAG, "Interrupted");
        } else {

        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(TAG, "Connection failed");
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mGeofencingRequest = new GeofencingRequest.Builder().addGeofences(mGeofences).build();

        mPendingIntent = createRequestPendingIntent();

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        PendingResult<Status> pendingResult = LocationServices.GeofencingApi
                .addGeofences(mGoogleApiClient, mGeofencingRequest, mPendingIntent);

        pendingResult.setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int cause){
        Log.v(TAG, "Connection suspended");
    }

    private PendingIntent createRequestPendingIntent() {
        if (mPendingIntent == null) {
            Log.v(TAG, "Creating PendingIntent");
            Intent intent = new Intent(mContext, GeofenceIntentService.class);
            mPendingIntent = PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return mPendingIntent;
    }

    @Override
    public void onLocationChanged(Location location) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        ParseGeoPoint geopoint = installation.getParseGeoPoint("currentLocation");
        if (geopoint.getLatitude() != 0 && geopoint.getLongitude() != 0) {
            ParseGeoPoint newLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
            installation.put("currentLocation", newLocation);
            installation.saveInBackground();
        } else if (geopoint == null) {

        }
    }

}
