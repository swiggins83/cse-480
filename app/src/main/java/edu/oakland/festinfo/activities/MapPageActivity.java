package edu.oakland.festinfo.activities;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import edu.oakland.festinfo.R;
import edu.oakland.festinfo.models.CombinedMarker;

@EActivity(R.layout.activity_map)
public class MapPageActivity extends BaseActivity implements OnMapClickListener,
        OnMapLongClickListener, OnMarkerDragListener,
        ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status> {

    protected static final String TAG = "MapPageActivity";

    final int TRIPOLEE = 1;
    final int RANCH_AREA = 2;
    final int SHERWOOD_COURT = 3;
    final int JUBILEE = 4;
    final int THE_OBSERVATORY = 5;
    final int THE_HANGAR = 6;
    final int FOREST_STAGE = 7;

    final int RQS_GooglePlayServices = 1;
    private GoogleMap map;
    MapFragment mapFragment;

    private Spinner colorSpinner;
    private Spinner mapKeySpinner;

    private GeofenceStore mGeofenceStore;

    String markerColor = "";

    @ViewById(R.id.locinfo)
    TextView tvLocInfo;

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @Extra
    Intent pastIntent;
    @Extra
    String stageSelected = "";

    //Arrays for map key spinner
    String[] strings = {"All", "Food/Drink", "First Aid", "Hammock Zones", "ATM", "Lost & Found",
            "Hot Air Balloon Rides", "Access Shuttle", "Restrooms", "Tents", "Stages"};
    int arr_images[] = {R.drawable.place_marker_24dp, R.drawable.orange_marker_24dp, R.drawable.red_marker_24dp,
            R.drawable.blue_marker_24dp, R.drawable.violet_marker_24dp,
            R.drawable.azure_marker_24dp, R.drawable.rose_marker_24dp, R.drawable.green_marker_24dp,
            R.drawable.yellow_marker_24dp, R.drawable.cyan_marker_24dp, R.drawable.magenta_marker_24dp};

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
    String currentMarkerTitle = "Null";

    boolean geofencesCreated = false;

    @AfterViews
    void init() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mapKeySpinner = (Spinner) findViewById(R.id.mapkey_spinner);
        mapKeySpinner.setAdapter(new MyAdapter(MapPageActivity.this, R.layout.row, strings));
        ParseInstallation.getCurrentInstallation().saveInBackground();
        FragmentManager fragmentManager = getFragmentManager();
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);
        map = mapFragment.getMap();

        map.setMyLocationEnabled(true);

        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        map.setOnMapClickListener(this);
        map.setOnMapLongClickListener(this);
        map.setOnMarkerDragListener(this);
        map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                //tvLocInfo.setText("Info Window Selected");
                if (marker.getTitle().equals("Ranch Area") || marker.getTitle().equals("Sherwood Court")
                        || marker.getTitle().equals("Tripolee") || marker.getTitle().equals("The Observatory")
                        || marker.getTitle().equals("The Hangar") || marker.getTitle().equals("Jubilee")
                        || marker.getTitle().equals("Forest Stage")) {
                    AlertDialog.Builder infowindowBuilderGeofence = new AlertDialog.Builder(MapPageActivity.this);
                    infowindowBuilderGeofence.setTitle(R.string.infowindow_geofence_click_options)
                            .setItems(R.array.infowindow_geofence_choices_array, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {

                                                case 0:
                                                    //tvLocInfo.setText("Favorite Stage Selected");
                                                    ParseInstallation installationFavorite = ParseInstallation.getCurrentInstallation();
                                                    ParsePush push = new ParsePush();
                                                    if (marker.getTitle().equals("Ranch Area")) {
                                                        installationFavorite.put("RanchAreaFavorited", true);
                                                        push.subscribeInBackground("RanchArea");
                                                        installationFavorite.saveInBackground();
                                                    } else if (marker.getTitle().equals("Sherwood Court")) {
                                                        installationFavorite.put("SherwoodCourtFavorited", true);
                                                        push.subscribeInBackground("SherwoodCourt");
                                                        installationFavorite.saveInBackground();
                                                    } else if (marker.getTitle().equals("Tripolee")) {
                                                        installationFavorite.put("TripoleeFavorited", true);
                                                        push.subscribeInBackground("Tripolee");
                                                        installationFavorite.saveInBackground();
                                                    } else if (marker.getTitle().equals("The Hangar")) {
                                                        installationFavorite.put("TheHangarFavorited", true);
                                                        push.subscribeInBackground("TheHanagar");
                                                        installationFavorite.saveInBackground();
                                                    } else if (marker.getTitle().equals("Jubilee")) {
                                                        installationFavorite.put("JubileeFavorited", true);
                                                        push.subscribeInBackground("Jubilee");
                                                        installationFavorite.saveInBackground();
                                                    } else if (marker.getTitle().equals("Forest Stage")) {
                                                        installationFavorite.put("ForestStageFavorited", true);
                                                        push.subscribeInBackground("ForestStage");
                                                        installationFavorite.saveInBackground();
                                                    } else if (marker.getTitle().equals("The Observatory")) {
                                                        installationFavorite.put("TheObservatoryFavorited", true);
                                                        push.subscribeInBackground("TheObservatory");
                                                        installationFavorite.saveInBackground();
                                                    }
                                                    break;

                                                case 1:
                                                    //tvLocInfo.setText("Unfavorite Stage Selected");
                                                    ParseInstallation installationUnfavorite = ParseInstallation.getCurrentInstallation();
                                                    ParsePush pushRemove = new ParsePush();
                                                    if (marker.getTitle().equals("Ranch Area")) {
                                                        installationUnfavorite.put("RanchAreaFavorited", false);
                                                        pushRemove.unsubscribeInBackground("RanchArea");
                                                        installationUnfavorite.saveInBackground();
                                                    } else if (marker.getTitle().equals("Sherwood Court")) {
                                                        installationUnfavorite.put("SherwoodCourtFavorited", false);
                                                        pushRemove.unsubscribeInBackground("SherwoodCourt");
                                                        installationUnfavorite.saveInBackground();
                                                    } else if (marker.getTitle().equals("Tripolee")) {
                                                        installationUnfavorite.put("TripoleeFavorited", false);
                                                        pushRemove.unsubscribeInBackground("Tripolee");
                                                        installationUnfavorite.saveInBackground();
                                                    } else if (marker.getTitle().equals("The Hangar")) {
                                                        installationUnfavorite.put("TheHangar", false);
                                                        pushRemove.unsubscribeInBackground("TheHanagar");
                                                        installationUnfavorite.saveInBackground();
                                                    } else if (marker.getTitle().equals("Jubilee")) {
                                                        installationUnfavorite.put("JubileeFavorited", false);
                                                        pushRemove.unsubscribeInBackground("Jubilee");
                                                        installationUnfavorite.saveInBackground();
                                                    } else if (marker.getTitle().equals("Forest Stage")) {
                                                        installationUnfavorite.put("ForestStageFavorited", false);
                                                        pushRemove.unsubscribeInBackground("ForestStage");
                                                        installationUnfavorite.saveInBackground();
                                                    } else if (marker.getTitle().equals("The Observatory")) {
                                                        installationUnfavorite.put("TheObservatoryFavorited", false);
                                                        pushRemove.unsubscribeInBackground("TheObservatory");
                                                        installationUnfavorite.saveInBackground();
                                                    }
                                                    break;

                                            }
                                        }

                                    }

                            );
                    infowindowBuilderGeofence.show();
                } else {
                    AlertDialog.Builder infowindowBuilderNoGeofence = new AlertDialog.Builder(MapPageActivity.this);
                    infowindowBuilderNoGeofence.setTitle(R.string.infowindow_nogeofence_click_options)
                            .setItems(R.array.infowindow_nogeofence_choices_array, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case 0:
                                                    //tvLocInfo.setText("Delete Marker Selected");
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

                                                                    try {
                                                                        for (int i = 0; i < combinedArray.size(); i++) {
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
                                                    //tvLocInfo.setText("Change title selected");
                                                    AlertDialog.Builder changeTitleInput = new AlertDialog.Builder(MapPageActivity.this);
                                                    changeTitleInput.setTitle("Enter new Title: ");
                                                    final EditText changeInput = new EditText(MapPageActivity.this);
                                                    changeInput.setInputType(InputType.TYPE_CLASS_TEXT);
                                                    changeTitleInput.setView(changeInput);
                                                    changeTitleInput.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            if (changeInput.getText().toString().matches("")) {
                                                                Toast.makeText(getApplicationContext(), "No Input Entered!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("MapMarkers");
                                                                query.findInBackground(new FindCallback<ParseObject>() {
                                                                    @Override
                                                                    public void done(List<ParseObject> objects, ParseException e) {
                                                                        if (e == null) {
                                                                            for (int i = 0; i < objects.size(); i++) {
                                                                                if (objects.get(i).getString("Title").equals(marker.getTitle())) {
                                                                                    objects.get(i).put("Title", changeInput.getText().toString());
                                                                                    objects.get(i).saveInBackground();
                                                                                }
                                                                            }
                                                                        } else {
                                                                            Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                                for (int i = 0; i < combinedArray.size(); i++) {
                                                                    if (combinedArray.get(i).getIdentification().equals(marker.getId())) {
                                                                        combinedArray.get(i).getMarker().setTitle(changeInput.getText().toString());
                                                                    }
                                                                }

                                                            }
                                                        }
                                                    });
                                                    changeTitleInput.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();
                                                        }
                                                    });
                                                    changeTitleInput.show();
                                                    break;

                                            }
                                        }

                                    }

                            );
                    infowindowBuilderNoGeofence.show();
                }
            }
        });

        focusCamera();
        buildGoogleApiClient();
    }



    public class MyAdapter extends ArrayAdapter<String> {
        public MyAdapter(Context context, int textViewResourceId, String[] objects){
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflator = getLayoutInflater();
            View row = inflator.inflate(R.layout.row, parent, false);
            TextView mainLabel = (TextView)row.findViewById(R.id.company);
            mainLabel.setText(strings[position]);

            ImageView icon = (ImageView)row.findViewById(R.id.image);
            icon.setImageResource(arr_images[position]);

            return row;
        }


    }

    public void focusCamera() {
        // centers camera around OU
        LatLngBounds OAKLAND = new LatLngBounds(
                new LatLng(42.67076892499418, -83.21840766817331),
                new LatLng(42.67571776885421, -83.21245819330215));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(OAKLAND.getCenter(), 15));
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
            case android.R.id.home:
                NavUtils.navigateUpTo(this, pastIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();
        combinedArray.clear();
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (resultCode == ConnectionResult.SUCCESS) {
            //Toast.makeText(getApplicationContext(), "isGooglePlayServicesAvailable SUCCESS", Toast.LENGTH_LONG).show();
        } else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
        }

        //Pregenerated Markers for Geofences
        Marker m1 = map.addMarker(new MarkerOptions()
        .title("Ranch Area")
        .position(new LatLng(42.671896, -83.215000))
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));


        Marker m2 = map.addMarker(new MarkerOptions()
        .title("Sherwood Court")
        .position(new LatLng(42.670989, -83.217028))
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));


        Marker m3 = map.addMarker(new MarkerOptions()
                .title("Tripolee")
                .position(new LatLng(42.673979, -83.212962))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));


        Marker m4 = map.addMarker(new MarkerOptions()
                .title("The Hangar")
                .position(new LatLng(42.674286, -83.216577))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));


        Marker m5 = map.addMarker(new MarkerOptions()
                .title("Jubilee")
                .position(new LatLng(42.672307, -83.210057))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));


        Marker m6 = map.addMarker(new MarkerOptions()
            .title("Forest Stage")
            .position(new LatLng(42.677103, -83.213855))
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));


        Marker m7 = map.addMarker(new MarkerOptions()
            .title("The Observatory")
            .position(new LatLng(42.677953, -83.219222))
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));


        if (!stageSelected.isEmpty()) {
            centerMapOnMarker();
        }

        //Build predefined Circles
        //Ranch Area
        Circle c1 = map.addCircle(new CircleOptions()
                .center(new LatLng(42.671896, -83.215000))
                .radius(100)
                .strokeColor(Color.MAGENTA)
                .strokeWidth(3));
        //Sherwood Court
        Circle c2 = map.addCircle(new CircleOptions()
                .center(new LatLng(42.670989, -83.217028))
                .radius(100)
                .strokeColor(Color.MAGENTA)
                .strokeWidth(3));
        //Tripolee
        Circle c3 = map.addCircle(new CircleOptions()
                .center(new LatLng(42.673979, -83.212962))
                .radius(100)
                .strokeColor(Color.MAGENTA)
                .strokeWidth(3));
        //The Hangar
        Circle c4 = map.addCircle(new CircleOptions()
                .center(new LatLng(42.674286, -83.216577))
                .radius(100)
                .strokeColor(Color.MAGENTA)
                .strokeWidth(3));
        //Jubilee
        Circle c5 = map.addCircle(new CircleOptions()
            .center(new LatLng(42.672307, -83.210057))
            .radius(100)
            .strokeColor(Color.MAGENTA)
            .strokeWidth(3));
        //Forest Stage
        Circle c6 = map.addCircle(new CircleOptions()
                .center(new LatLng(42.677103, -83.213855))
                .radius(100)
                .strokeColor(Color.MAGENTA)
                .strokeWidth(3));
        //The Observatory
        Circle c7 = map.addCircle(new CircleOptions()
                .center(new LatLng(42.677953, -83.219222))
                .radius(100)
                .strokeColor(Color.MAGENTA)
                .strokeWidth(3));

        combinedArray.add(new CombinedMarker(m1, c1, m1.getId(), "stage"));
        combinedArray.add(new CombinedMarker(m2, c2, m2.getId(), "stage"));

        //Build static geofences here
        if (geofencesCreated == false) {
            //Ranch Area
            geofenceArray.add(new Geofence.Builder()
                    .setRequestId("RanchArea")
                    .setLoiteringDelay(20000)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setCircularRegion(42.671896, -83.215000, 100)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
            //Sherwood Court
            geofenceArray.add(new Geofence.Builder()
                    .setRequestId("SherwoodCourt")
                    .setLoiteringDelay(20000)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setCircularRegion(42.670989, -83.217028, 100)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
            //Tripolee
            geofenceArray.add(new Geofence.Builder()
                    .setRequestId("Tripolee")
                    .setLoiteringDelay(20000)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setCircularRegion(42.673979, -83.212962, 100)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
            //The Hangar
            geofenceArray.add(new Geofence.Builder()
                    .setRequestId("TheHangar")
                    .setLoiteringDelay(20000)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setCircularRegion(42.674286, -83.216577, 100)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
            //Jubilee
            geofenceArray.add(new Geofence.Builder()
                    .setRequestId("Jubilee")
                    .setLoiteringDelay(20000)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setCircularRegion(42.672307, -83.210057, 100)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
            //Forest Stage
            geofenceArray.add(new Geofence.Builder()
                    .setRequestId("ForestStage")
                    .setLoiteringDelay(20000)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setCircularRegion(42.677103, -83.213855, 100)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
            //The Observatory
            geofenceArray.add(new Geofence.Builder()
                    .setRequestId("TheObservatory")
                    .setLoiteringDelay(20000)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setCircularRegion(42.677953, -83.219222, 100)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL
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

                        if (markerHue == BitmapDescriptorFactory.HUE_RED) {
                            CombinedMarker combined = new CombinedMarker(marker, circle, marker.getId(), "first aid");
                            combinedArray.add(combined);
                        } else if (markerHue == BitmapDescriptorFactory.HUE_AZURE) {
                            CombinedMarker combined = new CombinedMarker(marker, circle, marker.getId(), "lost found");
                            combinedArray.add(combined);
                        } else if (markerHue == BitmapDescriptorFactory.HUE_ORANGE) {
                            CombinedMarker combined = new CombinedMarker(marker, circle, marker.getId(), "food");
                            combinedArray.add(combined);
                        } else if (markerHue == BitmapDescriptorFactory.HUE_GREEN) {
                            CombinedMarker combined = new CombinedMarker(marker, circle, marker.getId(), "shuttle");
                            combinedArray.add(combined);
                        } else if (markerHue == BitmapDescriptorFactory.HUE_VIOLET) {
                            CombinedMarker combined = new CombinedMarker(marker, circle, marker.getId(), "atm");
                            combinedArray.add(combined);
                        } else if (markerHue == BitmapDescriptorFactory.HUE_MAGENTA) {
                            CombinedMarker combined = new CombinedMarker(marker, circle, marker.getId(), "stage");
                            combinedArray.add(combined);
                        } else if (markerHue == BitmapDescriptorFactory.HUE_ROSE) {
                            CombinedMarker combined = new CombinedMarker(marker, circle, marker.getId(), "hot air");
                            combinedArray.add(combined);
                        } else if (markerHue == BitmapDescriptorFactory.HUE_BLUE) {
                            CombinedMarker combined = new CombinedMarker(marker, circle, marker.getId(), "hammock");
                            combinedArray.add(combined);
                        } else if (markerHue == BitmapDescriptorFactory.HUE_CYAN) {
                            CombinedMarker combined = new CombinedMarker(marker, circle, marker.getId(), "tents");
                            combinedArray.add(combined);
                        } else if (markerHue == BitmapDescriptorFactory.HUE_YELLOW) {
                            CombinedMarker combined = new CombinedMarker(marker, circle, marker.getId(), "restrooms");
                            combinedArray.add(combined);
                        }

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
        mapKeySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 switch (position){
                     case 0:
                         for (int i = 0; i < combinedArray.size(); i++) {
                             combinedArray.get(i).getMarker().setVisible(true);
                             combinedArray.get(i).getCircle().setVisible(true);
                         }
                         break;
                     case 1:
                         for (int i = 0; i < combinedArray.size(); i++) {
                             if (combinedArray.get(i).getMarkerType().equals("food")) {
                                 combinedArray.get(i).getMarker().setVisible(true);
                                 combinedArray.get(i).getCircle().setVisible(true);
                             } else {
                                 combinedArray.get(i).getMarker().setVisible(false);
                                 combinedArray.get(i).getCircle().setVisible(false);
                             }
                         }
                         break;
                     case 2:
                         for (int i = 0; i < combinedArray.size(); i++) {
                             if (combinedArray.get(i).getMarkerType().equals("first aid")) {
                                 combinedArray.get(i).getMarker().setVisible(true);
                                 combinedArray.get(i).getCircle().setVisible(true);
                             } else {
                                 combinedArray.get(i).getMarker().setVisible(false);
                                 combinedArray.get(i).getCircle().setVisible(false);
                             }
                         }
                         break;
                     case 3:
                         for (int i = 0; i < combinedArray.size(); i++) {
                             if (combinedArray.get(i).getMarkerType().equals("hammock")) {
                                 combinedArray.get(i).getMarker().setVisible(true);
                                 combinedArray.get(i).getCircle().setVisible(true);
                             } else {
                                 combinedArray.get(i).getMarker().setVisible(false);
                                 combinedArray.get(i).getCircle().setVisible(false);
                             }
                         }
                         break;
                     case 4:
                         for (int i = 0; i < combinedArray.size(); i++) {
                             if (combinedArray.get(i).getMarkerType().equals("atm")) {
                                 combinedArray.get(i).getMarker().setVisible(true);
                                 combinedArray.get(i).getCircle().setVisible(true);
                             } else {
                                 combinedArray.get(i).getMarker().setVisible(false);
                                 combinedArray.get(i).getCircle().setVisible(false);
                             }
                         }
                         break;
                     case 5:
                         for (int i = 0; i < combinedArray.size(); i++) {
                             if (combinedArray.get(i).getMarkerType().equals("lost found")) {
                                 combinedArray.get(i).getMarker().setVisible(true);
                                 combinedArray.get(i).getCircle().setVisible(true);
                             } else {
                                 combinedArray.get(i).getMarker().setVisible(false);
                                 combinedArray.get(i).getCircle().setVisible(false);
                             }
                         }
                         break;
                     case 6:
                         for (int i = 0; i < combinedArray.size(); i++) {
                             if (combinedArray.get(i).getMarkerType().equals("hot air")) {
                                 combinedArray.get(i).getMarker().setVisible(true);
                                 combinedArray.get(i).getCircle().setVisible(true);
                             } else {
                                 combinedArray.get(i).getMarker().setVisible(false);
                                 combinedArray.get(i).getCircle().setVisible(false);
                             }
                         }
                         break;
                     case 7:
                         for (int i = 0; i < combinedArray.size(); i++) {
                             if (combinedArray.get(i).getMarkerType().equals("shuttle")) {
                                 combinedArray.get(i).getMarker().setVisible(true);
                                 combinedArray.get(i).getCircle().setVisible(true);
                             } else {
                                 combinedArray.get(i).getMarker().setVisible(false);
                                 combinedArray.get(i).getCircle().setVisible(false);
                             }
                         }
                         break;
                     case 8:
                         for (int i = 0; i < combinedArray.size(); i++) {
                             if (combinedArray.get(i).getMarkerType().equals("restrooms")) {
                                 combinedArray.get(i).getMarker().setVisible(true);
                                 combinedArray.get(i).getCircle().setVisible(true);
                             } else {
                                 combinedArray.get(i).getMarker().setVisible(false);
                                 combinedArray.get(i).getCircle().setVisible(false);
                             }
                         }
                         break;
                     case 9:
                         for (int i = 0; i < combinedArray.size(); i++) {
                             if (combinedArray.get(i).getMarkerType().equals("tents")) {
                                 combinedArray.get(i).getMarker().setVisible(true);
                                 combinedArray.get(i).getCircle().setVisible(true);
                             } else {
                                 combinedArray.get(i).getMarker().setVisible(false);
                                 combinedArray.get(i).getCircle().setVisible(false);
                             }
                         }
                         break;
                     case 10:
                         for (int i = 0; i < combinedArray.size(); i++) {
                             if (combinedArray.get(i).getMarkerType().equals("stage")) {
                                 combinedArray.get(i).getMarker().setVisible(true);
                                 combinedArray.get(i).getCircle().setVisible(true);
                             } else {
                                 combinedArray.get(i).getMarker().setVisible(false);
                                 combinedArray.get(i).getCircle().setVisible(false);
                             }
                         }
                         break;
                 }
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    private void centerMapOnMarker() {
        for (int i = 0; i < combinedArray.size(); i++) {
            if (combinedArray.get(i).getMarker().getTitle().toLowerCase().equals(stageSelected.toLowerCase())) {
                LatLng point = combinedArray.get(i).getMarker().getPosition();
                map.animateCamera(CameraUpdateFactory.newLatLng(point));
                combinedArray.get(i).getMarker().showInfoWindow();
                break;
            }
        }
    }

    @Override
    public void onMapLongClick(final LatLng point) {

        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        final ParseObject MapMarkers = new ParseObject("MapMarkers");
        MapMarkers.setACL(acl);

        AlertDialog.Builder chooseColor = new AlertDialog.Builder(this);
        chooseColor.setTitle("Choose a Color: ")
                .setItems(R.array.choose_color_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            markerColor = "Red";
                        } else if (which == 1) {
                            markerColor = "Azure";
                        } else if (which == 2) {
                            markerColor = "Orange";
                        } else if (which == 3) {
                            markerColor = "Green";
                        } else if (which == 4) {
                            markerColor = "Violet";
                        } else if (which == 5) {
                            markerColor = "Blue";
                        } else if (which == 6) {
                            markerColor = "Magenta";
                        } else if (which == 7) {
                            markerColor = "Rose";
                        } else if (which == 8) {
                            markerColor = "Yellow";
                        } else if (which == 9) {
                            markerColor = "Cyan";
                        }
                    }
                });

        //tvLocInfo.setText("New marker added@" + point.toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Custom Text:");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();

                //tvLocInfo.setText(text + " marker added!");

                if (markerColor.equals("Red")) {
                    Marker newMarker = map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    Circle newCircle = map.addCircle(new CircleOptions().center(point).radius(0));
                    newCircle.setStrokeWidth(2);
                    newCircle.setStrokeColor(Color.RED);
                    CombinedMarker cm = new CombinedMarker(newMarker, newCircle, newMarker.getId(), "first aid");
                    combinedArray.add(cm);

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
                    Circle newCircle = map.addCircle(new CircleOptions().center(point).radius(0));
                    newCircle.setStrokeWidth(2);
                    newCircle.setStrokeColor(Color.RED);
                    CombinedMarker cm = new CombinedMarker(newMarker, newCircle, newMarker.getId(), "lost found");
                    combinedArray.add(cm);

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
                    Circle newCircle = map.addCircle(new CircleOptions().center(point).radius(0));
                    newCircle.setStrokeWidth(2);
                    newCircle.setStrokeColor(Color.RED);
                    CombinedMarker cm = new CombinedMarker(newMarker, newCircle, newMarker.getId(), "food");
                    combinedArray.add(cm);

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
                    Circle newCircle = map.addCircle(new CircleOptions().center(point).radius(0));
                    newCircle.setStrokeWidth(2);
                    newCircle.setStrokeColor(Color.RED);
                    CombinedMarker cm = new CombinedMarker(newMarker, newCircle, newMarker.getId(), "shuttle");
                    combinedArray.add(cm);

                    ParseGeoPoint geoPoint = new ParseGeoPoint(point.latitude, point.longitude);

                    MapMarkers.put("Title", text);
                    MapMarkers.put("Location", geoPoint);
                    MapMarkers.put("Color", BitmapDescriptorFactory.HUE_GREEN);
                    MapMarkers.put("HasGeofence", false);
                    MapMarkers.put("GeofenceID", newMarker.getId());
                    MapMarkers.put("GeofenceRadius", 0);
                    MapMarkers.saveInBackground();
                    //tvLocInfo.setText("Green Stored");

                } else if (markerColor.equals("Violet")) {

                    Marker newMarker = map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(text)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                    Circle newCircle = map.addCircle(new CircleOptions().center(point).radius(0));
                    newCircle.setStrokeWidth(2);
                    newCircle.setStrokeColor(Color.RED);
                    CombinedMarker cm = new CombinedMarker(newMarker, newCircle, newMarker.getId(), "atm");
                    combinedArray.add(cm);

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
                    Circle newCircle = map.addCircle(new CircleOptions().center(point).radius(0));
                    newCircle.setStrokeWidth(2);
                    newCircle.setStrokeColor(Color.RED);
                    CombinedMarker cm = new CombinedMarker(newMarker, newCircle, newMarker.getId(), "hammock");
                    combinedArray.add(cm);

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
                    Circle newCircle = map.addCircle(new CircleOptions().center(point).radius(0));
                    newCircle.setStrokeWidth(2);
                    newCircle.setStrokeColor(Color.RED);
                    CombinedMarker cm = new CombinedMarker(newMarker, newCircle, newMarker.getId(), "stage");
                    combinedArray.add(cm);

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
                    Circle newCircle = map.addCircle(new CircleOptions().center(point).radius(0));
                    newCircle.setStrokeWidth(2);
                    newCircle.setStrokeColor(Color.RED);
                    CombinedMarker cm = new CombinedMarker(newMarker, newCircle, newMarker.getId(), "hot air");
                    combinedArray.add(cm);

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
                    Circle newCircle = map.addCircle(new CircleOptions().center(point).radius(0));
                    newCircle.setStrokeWidth(2);
                    newCircle.setStrokeColor(Color.RED);
                    CombinedMarker cm = new CombinedMarker(newMarker, newCircle, newMarker.getId(), "restrooms");
                    combinedArray.add(cm);

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
                    Circle newCircle = map.addCircle(new CircleOptions().center(point).radius(0));
                    newCircle.setStrokeWidth(2);
                    newCircle.setStrokeColor(Color.RED);
                    CombinedMarker cm = new CombinedMarker(newMarker, newCircle, newMarker.getId(), "tents");
                    combinedArray.add(cm);

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
                    Circle newCircle = map.addCircle(new CircleOptions().center(point).radius(0));
                    newCircle.setStrokeWidth(2);
                    newCircle.setStrokeColor(Color.RED);
                    CombinedMarker cm = new CombinedMarker(newMarker, newCircle, newMarker.getId(), "first aid");
                    combinedArray.add(cm);

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
        chooseColor.show();
    }

    @Override
    public void onMapClick(LatLng point) {
        //tvLocInfo.setText(point.toString());
        map.animateCamera(CameraUpdateFactory.newLatLng(point));

    }

    public void onMarkerDrag(Marker marker) {
        //tvLocInfo.setText("Marker " + marker.getId() + "Drag");
    }

    public void onMarkerDragEnd(final Marker marker) {
        //tvLocInfo.setText("Marker " + marker.getId() + " DragEnd");

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
        //tvLocInfo.setText("Marker " + marker.getId() + " DragStart");
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

    @Click(R.id.share_button_on)
    public void shareLocation() {
        if (map.getMyLocation() == null) {
            Toast.makeText(getApplicationContext(), "Cannot share location. Is your GPS on?", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Sharing location", Toast.LENGTH_SHORT).show();
            ParseUser user = ParseUser.getCurrentUser();
            ParseGeoPoint geoPoint = new ParseGeoPoint(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
            user.put("currentLocation", geoPoint);
            user.saveInBackground();
            //tvLocInfo.setText("Stored Location to Parse");
            FloatingActionButton fabOn = (FloatingActionButton)findViewById(R.id.share_button_on);
            fabOn.setVisibility(View.INVISIBLE);
            FloatingActionButton fabOff = (FloatingActionButton)findViewById(R.id.share_button_off);
            fabOff.setVisibility(View.VISIBLE);
        }
    }

    @Click(R.id.share_button_off)
    public void shareLocationOff() {
        Toast.makeText(getApplicationContext(), "No longer sharing location", Toast.LENGTH_SHORT).show();
        ParseGeoPoint point = new ParseGeoPoint(0,0);
        ParseUser user = ParseUser.getCurrentUser();
        user.put("currentLocation", point);
        user.saveInBackground();
        //tvLocInfo.setText("Removed Location from Parse");
        FloatingActionButton fabOff = (FloatingActionButton)findViewById(R.id.share_button_off);
        fabOff.setVisibility(View.INVISIBLE);
        FloatingActionButton fabOn = (FloatingActionButton)findViewById(R.id.share_button_on);
        fabOn.setVisibility(View.VISIBLE);
    }

    @Click(R.id.search_for_marker)
    public void searchForMarker() {
        AlertDialog.Builder searchInput = new AlertDialog.Builder(MapPageActivity.this);
        searchInput.setTitle("Enter Search Text: ");
        final EditText changeInput = new EditText(MapPageActivity.this);
        changeInput.setInputType(InputType.TYPE_CLASS_TEXT);
        searchInput.setView(changeInput);
        searchInput.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean foundMarker = false;
                if (changeInput.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "No Input Entered!", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < combinedArray.size(); i++) {
                        if (combinedArray.get(i).getMarker().getTitle().toLowerCase().equals(changeInput.getText().toString().toLowerCase())) {
                            LatLng point = combinedArray.get(i).getMarker().getPosition();
                            map.animateCamera(CameraUpdateFactory.newLatLng(point));
                            combinedArray.get(i).getMarker().showInfoWindow();
                            foundMarker = true;
                            break;
                        }
                    }
                    if (foundMarker == false) {
                        Toast.makeText(MapPageActivity.this, "Cannot find marker", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        searchInput.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        searchInput.show();
    }

    @Click(R.id.navigate_to_friend_button)
    public void launchNavigation() {
        final ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        ParseGeoPoint friendLocation = null;
        final ParseGeoPoint yourLocation = user.getParseGeoPoint("currentLocation");
        final ArrayList<ParseGeoPoint> friendLocations = new ArrayList<ParseGeoPoint>();
        final ArrayList<String> friendNames = new ArrayList<String>();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                for (int i = 0; i < objects.size(); i++) {
                    if (objects.get(i).getUsername() != user.getUsername()) {
                        if (objects.get(i).getParseGeoPoint("currentLocation") != null) {
                            if (objects.get(i).getParseGeoPoint("currentLocation").getLatitude() != 0
                                    && objects.get(i).getParseGeoPoint("currentLocation").getLongitude() != 0) {
                                friendLocations.add(objects.get(i).getParseGeoPoint("currentLocation"));
                                friendNames.add(objects.get(i).getUsername());
                                Log.d(TAG, friendNames.toString());
                            }
                        }
                    }
                }
                String[] list = new String[friendNames.size()];
                for (int j = 0; j < friendNames.size(); j++) {
                    list[j] = friendNames.get(j);
                    Log.v(TAG, list.toString());
                }
                AlertDialog.Builder dialog = new AlertDialog.Builder(MapPageActivity.this);
                dialog.setTitle("Choose a friend to navigate to: ")
                        .setItems(list, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int friendNames) {
                                ParseGeoPoint point = friendLocations.get(friendNames);
                                String url = "http://maps.google.com/maps?saddr=" + yourLocation.getLatitude() + ","
                                        + yourLocation.getLongitude() + "&daddr=" + point.getLatitude() + ","
                                        + point.getLongitude();
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(intent);
                            }
                        });
                dialog.create();
                dialog.show();
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

    //Should set coordinates to (0,0) when user powers off their device if they did not turn sharing off.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ParseGeoPoint point = new ParseGeoPoint(0,0);
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("currentLocation", point);
        installation.saveInBackground();
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

    public void onResult(Status status) {
        if (status.isSuccess()) {
            Toast.makeText(this, "Created Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Not Created", Toast.LENGTH_SHORT).show();
        }
    }

}

//old add geofence code
                                        /*
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
                                                                        geofenceArray.add(new Geofence.Builder()
                                                                                .setRequestId(combinedArray.get(i).getMarker().getTitle())
                                                                                .setLoiteringDelay(30000)
                                                                                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                                                                .setCircularRegion(combinedArray.get(i).getMarker().getPosition().latitude,
                                                                                        combinedArray.get(i).getMarker().getPosition().longitude,
                                                                                        (float) radius)
                                                                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                                                                                        | Geofence.GEOFENCE_TRANSITION_DWELL
                                                                                        | Geofence.GEOFENCE_TRANSITION_EXIT)
                                                                                .build());

                                                                        mGeofenceStore = new GeofenceStore(MapPageActivity.this, geofenceArray);
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
                                        */
//old remove geofence code
                                        /*for (int i = 0; i < combinedArray.size(); i++) {
                                            if (combinedArray.get(i).getIdentification().equals(marker.getId())) {
                                                for (int j = 0; j < geofenceArray.size(); j++) {
                                                    if (geofenceArray.get(j).getRequestId().equals(marker.getTitle())) {
                                                        Log.d(TAG, "Geofence removed");
                                                        geofenceArray.remove(j);
                                                    }
                                                }
                                                combinedArray.get(i).getCircle().setRadius(0);
                                            }
                                        }
                                        mGeofenceStore = new GeofenceStore(MapPageActivity.this, geofenceArray);
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
                                        }); */
//old change geofence code
                                        /*for (int i = 0; i < combinedArray.size(); i++) {
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
                                        }*/

