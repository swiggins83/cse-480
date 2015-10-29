package edu.oakland.festinfo.activities;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.oakland.festinfo.R;

@EActivity(R.layout.activity_map)
public class MapPageActivity extends BaseActivity implements OnMapClickListener,
        OnMapLongClickListener, OnMarkerDragListener {

    final int RQS_GooglePlayServices = 1;
    private GoogleMap map;
    MapFragment mapFragment;

    private Spinner colorSpinner;

    String markerColor = "";

    @ViewById(R.id.locinfo)
    TextView tvLocInfo;


    String titleGet;
    Double geo1Lat;
    Double geo1Long;
    Float markerHue;
    String currentMovingMarker = "null";
    String currentObjectID = "Null";
    String currentMarkerTitle = "Null";


    @AfterViews
    void init() {
        FragmentManager fragmentManager = getFragmentManager();
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);
        map = mapFragment.getMap();

        map.setMyLocationEnabled(true);

        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        map.setOnMapClickListener(this);
        map.setOnMapLongClickListener(this);
        map.setOnMarkerDragListener(this);

        focusCamera();
        drawBoundaries();
        addListenerOnSpinnerItemSelection();


    }

    public void focusCamera() {
        // centers camera around OU
        LatLngBounds OAKLAND = new LatLngBounds(
                new LatLng(42.67076892499418, -83.21840766817331),
                new LatLng(42.67571776885421, -83.21245819330215));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(OAKLAND.getCenter(), 15));
    }

    public void drawBoundaries() {
        // draws rough lines around EC parking lot
        PolylineOptions rectOptions = new PolylineOptions()
                .add(new LatLng(42.6713583, -83.215614482)) // north west
                .add(new LatLng(42.670903, -83.215614482)) // south west
                .add(new LatLng(42.670903, -83.21417212)) // south east
                .add(new LatLng(42.671420707, -83.214113451))  // north east
                .add(new LatLng(42.6713583, -83.215614482)); // closing at north west

        Polyline polyline = map.addPolyline(rectOptions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_legalnotices:
                String LicenseInfo = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(getApplicationContext());
                AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(MapPageActivity.this);
                LicenseDialog.setTitle("Legal Notices");
                LicenseDialog.setMessage(LicenseInfo);
                LicenseDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addListenerOnSpinnerItemSelection() {
        colorSpinner = (Spinner) findViewById(R.id.color_spinner);
        colorSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }



    @Override
    protected void onResume(){
        super.onResume();
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (resultCode == ConnectionResult.SUCCESS) {
            Toast.makeText(getApplicationContext(), "isGooglePlayServicesAvailable SUCCESS", Toast.LENGTH_LONG).show();
        } else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
        }

        ParseQuery markerQuery = new ParseQuery("MapMarkers");
        markerQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    for (int i = 0; i < objects.size(); i++) {

                        titleGet = objects.get(i).getString("Title");
                        geo1Lat = objects.get(i).getParseGeoPoint("Location").getLatitude();
                        geo1Long = objects.get(i).getParseGeoPoint("Location").getLongitude();
                        markerHue = objects.get(i).getNumber("Color").floatValue();

                        map.addMarker(new MarkerOptions()
                                .position(new LatLng(geo1Lat,geo1Long))
                                .title(titleGet)
                                .draggable(true)
                                .icon(BitmapDescriptorFactory.defaultMarker(markerHue)));

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                }

            }

        });

    }

    @Override
    public void onMapLongClick(final LatLng point) {

        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        final ParseObject MapMarkers = new ParseObject("MapMarkers");
        MapMarkers.setACL(acl);

        markerColor = String.valueOf(colorSpinner.getSelectedItem());

        tvLocInfo.setText("New marker added@" + point.toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Custom Text:");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();

                if (markerColor.equals("Red")) {
                    map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_RED);
                    MapMarkers.saveInBackground();

                } else if (markerColor.equals("Azure")) {
                    map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_AZURE);
                    MapMarkers.saveInBackground();

                } else if (markerColor.equals("Orange")) {

                    map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_ORANGE);
                    MapMarkers.saveInBackground();

                } else if (markerColor.equals("Green")) {

                    map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_GREEN);
                    MapMarkers.saveInBackground();

                } else if (markerColor.equals("Violet")) {

                    map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_VIOLET);
                    MapMarkers.saveInBackground();

                } else if (markerColor.equals("Blue")) {

                    map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_BLUE);
                    MapMarkers.saveInBackground();

                } else if (markerColor.equals("Magenta")) {

                    map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_MAGENTA);
                    MapMarkers.saveInBackground();

                } else if (markerColor.equals("Rose")) {

                    map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_ROSE);
                    MapMarkers.saveInBackground();

                } else if (markerColor.equals("Yellow")) {

                    map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_YELLOW);
                    MapMarkers.saveInBackground();

                } else if (markerColor.equals("Cyan")) {

                    map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_CYAN);
                    MapMarkers.saveInBackground();

                } else {
                    map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_RED);
                    MapMarkers.saveInBackground();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    @Override
    public void onMapClick(LatLng point) {
        tvLocInfo.setText(point.toString());
        map.animateCamera(CameraUpdateFactory.newLatLng(point));
    }

    public void onMarkerDrag(Marker marker) {
        tvLocInfo.setText("Marker " + marker.getId() + "Drag@" + marker.getPosition());
    }

    public void onMarkerDragEnd(Marker marker) {
        tvLocInfo.setText("Marker " + marker.getId() + " DragEnd");

        geo1Lat = marker.getPosition().latitude;
        geo1Long = marker.getPosition().longitude;

        final ParseGeoPoint geoPoint = new ParseGeoPoint(marker.getPosition().latitude, marker.getPosition().longitude);

        final ParseQuery query = new ParseQuery("MapMarkers");
        query.whereEqualTo("Title", currentMovingMarker);
        query.getInBackground(currentObjectID, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {

                if (e == null) {
                    object.put("Location", geoPoint);
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }

            }
        });

    }

            public void onMarkerDragStart(Marker marker) {
                tvLocInfo.setText("Marker " + marker.getId() + " DragStart");
                ParseGeoPoint geoPoint = new ParseGeoPoint(marker.getPosition().latitude, marker.getPosition().longitude);
                currentMarkerTitle = marker.getTitle();

                ParseQuery query = new ParseQuery("MapMarkers");
                query.whereEqualTo("Title", currentMarkerTitle);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        if (e == null) {
                            for (int i = 0; i < objects.size(); i++) {
                                currentMovingMarker = objects.get(i).getString("Title");
                                currentObjectID = objects.get(i).getObjectId();
                            }
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }

                    }


                });

            }


        }

