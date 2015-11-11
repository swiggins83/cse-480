package edu.oakland.festinfo.activities;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.service.carrier.CarrierMessagingService;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
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
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import edu.oakland.festinfo.R;

@EActivity(R.layout.activity_map)
public class MapPageActivity extends BaseActivity implements OnMapClickListener,
        OnMapLongClickListener, OnMarkerDragListener, GoogleMap.OnMarkerClickListener,
        ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status> {


    protected static final String TAG = "MapPageActivity";

    final int RQS_GooglePlayServices = 1;
    private GoogleMap map;
    MapFragment mapFragment;

    private Spinner colorSpinner;

    private GeofenceStore mGeofenceStore;

    String markerColor = "";

    @ViewById(R.id.locinfo)
    TextView tvLocInfo;

    @ViewById(R.id.debugTextview)
    TextView debugText;

    Circle mapCircle;
    ArrayList<Circle> circleArray = new ArrayList<Circle>();

    CombinedMarker combinedMarker;
    ArrayList<CombinedMarker> combinedArray = new ArrayList<CombinedMarker>();

    ArrayList<Geofence> geofenceArray = new ArrayList<Geofence>();

    GoogleApiClient mGoogleApiClient;
    private PendingIntent mGeofencePendingIntent = null;


    String titleGet;
    Double geo1Lat;
    Double geo1Long;
    Float markerHue;
    boolean checkHasCircle;
    String geofenceID;
    double geofenceRadius;

    String currentMovingMarker = "null";
    String currentObjectID = "Null";
    String currentCircleID;
    String currentMarkerTitle = "Null";

    boolean geofencesCreated = false;

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
        map.setOnMarkerClickListener(this);
        map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                tvLocInfo.setText("Info Window Selected");
                AlertDialog.Builder infowindowBuilder = new AlertDialog.Builder(MapPageActivity.this);
                infowindowBuilder.setTitle(R.string.infowindow_click_options)
                        .setItems(R.array.infowindow_choices_array, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        tvLocInfo.setText("Delete Marker Selected");
                                        AlertDialog.Builder deleteConfirmation = new AlertDialog.Builder(MapPageActivity.this);
                                        deleteConfirmation.setTitle("Warning");
                                        deleteConfirmation.setMessage("Are you sure you want to delete this marker?");
                                        deleteConfirmation.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ParseGeoPoint geoPoint = new ParseGeoPoint(marker.getPosition().latitude, marker.getPosition().longitude);
                                                currentMarkerTitle = marker.getTitle();

                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("MapMarkers");
                                                query.whereEqualTo("Title", currentMarkerTitle);
                                                query.getFirstInBackground(new GetCallback<ParseObject>() {
                                                    @Override
                                                    public void done(ParseObject object, ParseException e) {
                                                        String debug = "string: ";

                                                        try {
                                                            for (int i = 0; i < combinedArray.size(); i++) {
                                                                debug = combinedArray.get(i).getIdentification() + ":" + object.getString("GeofenceID") + ":" + combinedArray.get(i).getCircle().getId();
                                                                debugText.setText(debug);
                                                                if (combinedArray.get(i).getIdentification().equals(object.getString("GeofenceID"))) {
                                                                    combinedArray.get(i).getCircle().setRadius(0);
                                                                    combinedArray.get(i).getCircle().remove();
                                                                    combinedArray.get(i).getMarker().remove();
                                                                    combinedArray.remove(i);
                                                                    object.delete();
                                                                }

                                                            }
                                                        } catch (ParseException e1) {
                                                            e1.printStackTrace();
                                                        }
                                                        object.saveInBackground();

                                                    }
                                                });
                                            }
                                        });
                                        deleteConfirmation.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                        deleteConfirmation.show();
                                        break;

                                    case 1:
                                        tvLocInfo.setText("Add Geofence Selected");
                                        for (int i = 0; i < combinedArray.size(); i++) {
                                            if (combinedArray.get(i).getIdentification().equals(marker.getId())) {
                                                if (combinedArray.get(i).getCircle().getRadius() != 0) {
                                                    Toast.makeText(getApplicationContext(), "You already have created a geofence", Toast.LENGTH_SHORT).show();
                                                    break;
                                                } else {
                                                    final AlertDialog.Builder newRadiusInput = new AlertDialog.Builder(MapPageActivity.this);
                                                    newRadiusInput.setTitle("Enter new radius in Meters: ");
                                                    final EditText newInput = new EditText(MapPageActivity.this);
                                                    newInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                                                    newRadiusInput.setView(newInput);
                                                    newRadiusInput.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            if (newInput.getText().toString().matches("")) {
                                                                Toast.makeText(getApplicationContext(), "No Input Entered!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                final double radius = Double.parseDouble(newInput.getText().toString());
                                                                for (int i = 0; i < combinedArray.size(); i++) {
                                                                    if (combinedArray.get(i).getIdentification().equals(marker.getId())) {
                                                                        combinedArray.get(i).getCircle().setRadius(radius);
                                                                    }
                                                                }
                                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("MapMarkers");
                                                                query.findInBackground(new FindCallback<ParseObject>() {
                                                                    @Override
                                                                    public void done(List<ParseObject> objects, ParseException e) {
                                                                        if (e == null) {
                                                                            for (int i = 0; i < objects.size(); i++) {
                                                                                if (objects.get(i).getString("Title").equals(marker.getTitle())) {
                                                                                    objects.get(i).put("GeofenceRadius", radius);
                                                                                    objects.get(i).saveInBackground();
                                                                                }
                                                                            }
                                                                        } else {
                                                                            Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });

                                                            }
                                                        }
                                                    });
                                                    newRadiusInput.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();
                                                        }
                                                    });
                                                    newRadiusInput.show();
                                                }
                                            }
                                        }
                                        break;


                                    case 2:
                                        tvLocInfo.setText("Remove Geofence Selected");
                                        for (int i = 0; i < combinedArray.size(); i++) {
                                            if (combinedArray.get(i).getIdentification().equals(marker.getId())) {
                                                combinedArray.get(i).getCircle().setRadius(0);
                                            }
                                        }
                                        ParseQuery<ParseObject> query = ParseQuery.getQuery("MapMarkers");
                                        query.findInBackground(new FindCallback<ParseObject>() {
                                            @Override
                                            public void done(List<ParseObject> objects, ParseException e) {
                                                if (e == null) {
                                                    for (int i = 0; i < objects.size(); i++) {
                                                        if (objects.get(i).getString("Title").equals(marker.getTitle())) {
                                                            objects.get(i).put("GeofenceRadius", 0);
                                                            objects.get(i).saveInBackground();
                                                        }
                                                    }
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        break;

                                    case 3:
                                        tvLocInfo.setText("Change Geofence Size Selected");
                                        for (int i = 0; i < combinedArray.size(); i++) {
                                            if (combinedArray.get(i).getIdentification().equals(marker.getId())) {
                                                if (combinedArray.get(i).getCircle().getRadius() == 0) {
                                                    Toast.makeText(getApplicationContext(), "Geofence has not been created.", Toast.LENGTH_SHORT).show();
                                                    break;
                                                } else {
                                                    AlertDialog.Builder changeRadiusInput = new AlertDialog.Builder(MapPageActivity.this);
                                                    changeRadiusInput.setTitle("Enter new radius in Meters: ");
                                                    final EditText changeInput = new EditText(MapPageActivity.this);
                                                    changeInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                                                    changeRadiusInput.setView(changeInput);
                                                    changeRadiusInput.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            if (changeInput.getText().toString().matches("")) {
                                                                Toast.makeText(getApplicationContext(), "No Input Entered!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                final double radius = Double.parseDouble(changeInput.getText().toString());
                                                                for (int i = 0; i < combinedArray.size(); i++) {
                                                                    if (combinedArray.get(i).getIdentification().equals(marker.getId())) {
                                                                        combinedArray.get(i).getCircle().setRadius(radius);
                                                                    }
                                                                }
                                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("MapMarkers");
                                                                query.findInBackground(new FindCallback<ParseObject>() {
                                                                    @Override
                                                                    public void done(List<ParseObject> objects, ParseException e) {
                                                                        if (e == null) {
                                                                            for (int i = 0; i < objects.size(); i++) {
                                                                                if (objects.get(i).getString("Title").equals(marker.getTitle())) {
                                                                                    objects.get(i).put("GeofenceRadius", radius);
                                                                                    objects.get(i).saveInBackground();
                                                                                }
                                                                            }
                                                                        } else {
                                                                            Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                    changeRadiusInput.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();
                                                        }
                                                    });
                                                    changeRadiusInput.show();
                                                }
                                            }
                                        }

                                                    break;

                                                    default:
                                                        tvLocInfo.setText("No Option Selected");
                                                        break;

                                                }
                        }

            }

            );
            infowindowBuilder.show();
        }
    });

        focusCamera();
        drawBoundaries();
        addListenerOnSpinnerItemSelection();
        buildGoogleApiClient();

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
        //Pregenerated Markers for Geofences
        map.addMarker(new MarkerOptions()
        .title("EC")
        .position(new LatLng(42.671896, -83.215000))
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

        map.addMarker(new MarkerOptions()
        .title("MSC")
        .position(new LatLng(42.670989, -83.217028))
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

        map.addMarker(new MarkerOptions()
                .title("Rec")
                .position(new LatLng(42.673979, -83.212962))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

        map.addMarker(new MarkerOptions()
                .title("OC")
                .position(new LatLng(42.674286, -83.216577))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

        //Build predefined Circles
        //Engineering Center
        map.addCircle(new CircleOptions()
                .center(new LatLng(42.671896, -83.215000))
                .radius(100)
                .strokeColor(Color.MAGENTA)
                .strokeWidth(3));
        //Math and Science Center
        map.addCircle(new CircleOptions()
                .center(new LatLng(42.670989, -83.217028))
                .radius(100)
                .strokeColor(Color.MAGENTA)
                .strokeWidth(3));
        //Recreation Center
        map.addCircle(new CircleOptions()
                .center(new LatLng(42.673979, -83.212962))
                .radius(100)
                .strokeColor(Color.MAGENTA)
                .strokeWidth(3));
        //Oakland Center
        map.addCircle(new CircleOptions()
                .center(new LatLng(42.674286, -83.216577))
                .radius(100)
                .strokeColor(Color.MAGENTA)
                .strokeWidth(3));


        //Build static geofences here
        if (geofencesCreated == false) {
            //Engineering Center
            geofenceArray.add(new Geofence.Builder()
                    .setRequestId("EC")
                    .setLoiteringDelay(30000)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setCircularRegion(42.671896, -83.215000, 100)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                            | Geofence.GEOFENCE_TRANSITION_DWELL
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
            //Math and Science Center
            geofenceArray.add(new Geofence.Builder()
                    .setRequestId("MSC")
                    .setLoiteringDelay(30000)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setCircularRegion(42.670989, -83.217028, 100)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                            | Geofence.GEOFENCE_TRANSITION_DWELL
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
            //Recreation Center
            geofenceArray.add(new Geofence.Builder()
                    .setRequestId("Rec")
                    .setLoiteringDelay(30000)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setCircularRegion(42.673979, -83.212962, 100)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                            | Geofence.GEOFENCE_TRANSITION_DWELL
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
            //Oakland Center
            geofenceArray.add(new Geofence.Builder()
                    .setRequestId("OC")
                    .setLoiteringDelay(30000)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setCircularRegion(42.674286, -83.216577, 100)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                            | Geofence.GEOFENCE_TRANSITION_DWELL
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());

            mGeofenceStore = new GeofenceStore(MapPageActivity.this, geofenceArray);
            geofencesCreated = true;
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
                        checkHasCircle = objects.get(i).getBoolean("HasGeofence");
                        geofenceID = objects.get(i).getString("GeofenceID");
                        geofenceRadius = objects.get(i).getDouble("GeofenceRadius");


                        Marker marker = map.addMarker(new MarkerOptions()
                                .position(new LatLng(geo1Lat, geo1Long))
                                .title(titleGet)
                                .draggable(true)
                                .icon(BitmapDescriptorFactory.defaultMarker(markerHue)));


                        Circle circle = mapCircle = map.addCircle(new CircleOptions().center(new LatLng(geo1Lat, geo1Long)).radius(geofenceRadius));
                        int baseColor = Color.RED;
                        mapCircle.setStrokeColor(baseColor);
                        mapCircle.setStrokeWidth(2);

                        CombinedMarker combined = new CombinedMarker(marker, circle, marker.getId());
                        combinedArray.add(combined);

                        //Code to build the geofences
                        /*if (geofencesCreated = false) {
                            geofenceArray.add(new Geofence.Builder()
                                    .setRequestId(geofenceID)
                                    .setCircularRegion(geo1Lat, geo1Long, Float.valueOf(String.valueOf(geofenceRadius)))
                                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                    .setLoiteringDelay(30000)
                                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                                            | Geofence.GEOFENCE_TRANSITION_DWELL
                                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                                    .build());

                            LocationServices.GeofencingApi.addGeofences(
                                    mGoogleApiClient,
                                    getGeofencingRequest(fence),
                                    getGeofencePendingIntent()
                            ).setResultCallback(MapPageActivity.this);
                        } */
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
                    Marker newMarker = map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    Circle newCircle = map.addCircle(new CircleOptions().center(point).radius(10));
                    newCircle.setStrokeWidth(2);
                    newCircle.setStrokeColor(Color.RED);
                    CombinedMarker cm = new CombinedMarker(newMarker, newCircle, newMarker.getId());
                    combinedArray.add(cm);
                    debugText.setText(newMarker.getId() + newCircle.getId() + cm.getIdentification() + combinedArray.size());

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_RED);
                    MapMarkers.put("HasGeofence", false);
                    MapMarkers.put("GeofenceID", newMarker.getId());
                    MapMarkers.put("GeofenceRadius", 0);
                    MapMarkers.saveInBackground();


                } else if (markerColor.equals("Azure")) {
                    Marker newMarker = map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    Circle newCircle = map.addCircle(new CircleOptions().center(point).radius(10));
                    newCircle.setStrokeWidth(2);
                    newCircle.setStrokeColor(Color.RED);
                    CombinedMarker cm = new CombinedMarker(newMarker, newCircle, newMarker.getId());
                    combinedArray.add(cm);
                    debugText.setText(newMarker.getId() + newCircle.getId() + cm.getIdentification() + combinedArray.size());

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_AZURE);
                    MapMarkers.put("HasGeofence", false);
                    MapMarkers.put("GeofenceID", newMarker.getId());
                    MapMarkers.put("GeofenceRadius", 0);
                    MapMarkers.saveInBackground();


                } else if (markerColor.equals("Orange")) {

                    Marker newMarker = map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    Circle newCircle = map.addCircle(new CircleOptions().center(point).radius(10));
                    newCircle.setStrokeWidth(2);
                    newCircle.setStrokeColor(Color.RED);
                    CombinedMarker cm = new CombinedMarker(newMarker, newCircle, newMarker.getId());
                    combinedArray.add(cm);
                    debugText.setText(newMarker.getId() + newCircle.getId() + cm.getIdentification() + combinedArray.size());

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_ORANGE);
                    MapMarkers.put("HasGeofence", false);
                    MapMarkers.put("GeofenceID", newMarker.getId());
                    MapMarkers.put("GeofenceRadius", 0);
                    MapMarkers.saveInBackground();


                } else if (markerColor.equals("Green")) {

                    Marker newMarker = map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    Circle newCircle = map.addCircle(new CircleOptions().center(point).radius(10));
                    newCircle.setStrokeWidth(2);
                    newCircle.setStrokeColor(Color.RED);
                    CombinedMarker cm = new CombinedMarker(newMarker, newCircle, newMarker.getId());
                    combinedArray.add(cm);
                    debugText.setText(newMarker.getId() + newCircle.getId() + cm.getIdentification() + combinedArray.size());

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_GREEN);
                    MapMarkers.put("HasGeofence", false);
                    MapMarkers.put("HasGeofence", newMarker.getId());
                    MapMarkers.put("GeofenceRadius", 0);
                    MapMarkers.saveInBackground();


                } else if (markerColor.equals("Violet")) {

                    Marker newMarker = map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                    Circle newCircle = map.addCircle(new CircleOptions().center(point).radius(10));
                    newCircle.setStrokeWidth(2);
                    newCircle.setStrokeColor(Color.RED);
                    CombinedMarker cm = new CombinedMarker(newMarker, newCircle, newMarker.getId());
                    combinedArray.add(cm);
                    debugText.setText(newMarker.getId() + newCircle.getId() + cm.getIdentification() + combinedArray.size());

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_VIOLET);
                    MapMarkers.put("HasGeofence", false);
                    MapMarkers.put("GeofenceID", newMarker.getId());
                    MapMarkers.put("GeofenceRadius", 0);
                    MapMarkers.saveInBackground();


                } else if (markerColor.equals("Blue")) {

                    Marker newMarker = map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    Circle newCircle = map.addCircle(new CircleOptions().center(point).radius(10));
                    newCircle.setStrokeWidth(2);
                    newCircle.setStrokeColor(Color.RED);
                    CombinedMarker cm = new CombinedMarker(newMarker, newCircle, newMarker.getId());
                    combinedArray.add(cm);
                    debugText.setText(newMarker.getId() + newCircle.getId() + cm.getIdentification() + combinedArray.size());

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_BLUE);
                    MapMarkers.put("HasGeofence", false);
                    MapMarkers.put("GeofenceID", newMarker.getId());
                    MapMarkers.put("GeofenceRadius", 0);
                    MapMarkers.saveInBackground();


                } else if (markerColor.equals("Magenta")) {

                    Marker newMarker = map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                    Circle newCircle = map.addCircle(new CircleOptions().center(point).radius(10));
                    newCircle.setStrokeWidth(2);
                    newCircle.setStrokeColor(Color.RED);
                    CombinedMarker cm = new CombinedMarker(newMarker, newCircle, newMarker.getId());
                    combinedArray.add(cm);
                    debugText.setText(newMarker.getId() + newCircle.getId() + cm.getIdentification() + combinedArray.size());

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_MAGENTA);
                    MapMarkers.put("HasGeofence", false);
                    MapMarkers.put("GeofenceID", newMarker.getId());
                    MapMarkers.put("GeofenceRadius", 0);
                    MapMarkers.saveInBackground();


                } else if (markerColor.equals("Rose")) {

                    Marker newMarker = map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                    Circle newCircle = map.addCircle(new CircleOptions().center(point).radius(10));
                    newCircle.setStrokeWidth(2);
                    newCircle.setStrokeColor(Color.RED);
                    CombinedMarker cm = new CombinedMarker(newMarker, newCircle, newMarker.getId());
                    combinedArray.add(cm);
                    debugText.setText(newMarker.getId() + newCircle.getId() + cm.getIdentification() + combinedArray.size());

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_ROSE);
                    MapMarkers.put("HasGeofence", false);
                    MapMarkers.put("GeofenceID", newMarker.getId());
                    MapMarkers.put("GeofenceRadius", 0);
                    MapMarkers.saveInBackground();


                } else if (markerColor.equals("Yellow")) {

                    Marker newMarker = map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    Circle newCircle = map.addCircle(new CircleOptions().center(point).radius(10));
                    newCircle.setStrokeWidth(2);
                    newCircle.setStrokeColor(Color.RED);
                    CombinedMarker cm = new CombinedMarker(newMarker, newCircle, newMarker.getId());
                    combinedArray.add(cm);
                    debugText.setText(newMarker.getId() + newCircle.getId() + cm.getIdentification() + combinedArray.size());

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_YELLOW);
                    MapMarkers.put("HasGeofence", false);
                    MapMarkers.put("GeofenceID", newMarker.getId());
                    MapMarkers.put("GeofenceRadius", 0);
                    MapMarkers.saveInBackground();


                } else if (markerColor.equals("Cyan")) {

                    Marker newMarker = map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                    Circle newCircle = map.addCircle(new CircleOptions().center(point).radius(10));
                    newCircle.setStrokeWidth(2);
                    newCircle.setStrokeColor(Color.RED);
                    CombinedMarker cm = new CombinedMarker(newMarker, newCircle, newMarker.getId());
                    combinedArray.add(cm);
                    debugText.setText(newMarker.getId() + newCircle.getId() + cm.getIdentification() + combinedArray.size());

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_CYAN);
                    MapMarkers.put("HasGeofence", false);
                    MapMarkers.put("GeofenceID", newMarker.getId());
                    MapMarkers.put("GeofenceRadius", 0);
                    MapMarkers.saveInBackground();


                } else {
                    Marker newMarker = map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    Circle newCircle = map.addCircle(new CircleOptions().center(point).radius(10));
                    newCircle.setStrokeWidth(2);
                    newCircle.setStrokeColor(Color.RED);
                    CombinedMarker cm = new CombinedMarker(newMarker, newCircle, newMarker.getId());
                    combinedArray.add(cm);
                    debugText.setText(newMarker.getId() + newCircle.getId() + cm.getIdentification() + combinedArray.size());

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_RED);
                    MapMarkers.put("HasGeofence", false);
                    MapMarkers.put("GeofenceID", newMarker.getId());
                    MapMarkers.put("GeofenceRadius", 0);
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
    public boolean onMarkerClick(Marker marker) {

        tvLocInfo.setText("Marker Clicked");
        map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        marker.showInfoWindow();


        return true;
    }



    @Override
    public void onMapClick(LatLng point) {
        tvLocInfo.setText(point.toString());
        map.animateCamera(CameraUpdateFactory.newLatLng(point));

    }

    public void onMarkerDrag(Marker marker) {
        tvLocInfo.setText("Marker " + marker.getId() + "Drag@" + marker.getPosition());
    }

    public void onMarkerDragEnd(final Marker marker) {
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
                    for (int i = 0; i < combinedArray.size(); i++) {
                        if (combinedArray.get(i).getIdentification().equals(currentObjectID)) {
                            combinedArray.get(i).getCircle().setCenter(marker.getPosition());
                        }
                    }
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


    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection Suspended");
    }

    private GeofencingRequest getGeofencingRequest(Geofence geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        //Reuses the intent if application has it already
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);

        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void onResult(Status status) {
        if (status.isSuccess()) {
            Toast.makeText(this, "Created Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Not Created", Toast.LENGTH_SHORT).show();
        }
    }

}

