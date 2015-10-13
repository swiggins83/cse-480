package edu.oakland.festinfo.activities;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import edu.oakland.festinfo.R;

@EActivity(R.layout.activity_map)
public class MapPageActivity extends BaseActivity implements OnMapClickListener, OnMapLongClickListener, OnMarkerDragListener {

    final int RQS_GooglePlayServices = 1;
    private GoogleMap map;
    MapFragment mapFragment;

    @ViewById(R.id.locinfo)
    TextView tvLocInfo;

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

    @Override
    protected void onResume(){
        super.onResume();
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (resultCode == ConnectionResult.SUCCESS) {
            Toast.makeText(getApplicationContext(), "isGooglePlayServicesAvailable SUCESS", Toast.LENGTH_LONG).show();
        } else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
        tvLocInfo.setText("New marker added@" + point.toString());
        map.addMarker(new MarkerOptions()
                .position(point)
                .draggable(true));
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
    }

    public void onMarkerDragStart(Marker marker) {
        tvLocInfo.setText("Marker " + marker.getId() + " DragStart");
    }

}
