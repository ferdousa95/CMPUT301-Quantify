package com.example.quantify;

//this MapsActivity.java are modified from the google map documents
//https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private GoogleMap map;
    private Location lastKnownLocation;
    private CameraPosition cameraPosition;
    private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    private static double currentLatitude;
    private static double currentLongitude;
    private double outputLatitude;
    private double outputLongitude;

    ArrayList<String> experimentIDList;
    ArrayList<String> longitudeList;
    ArrayList<String> latitudeList;

    Marker mCurrLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        experimentIDList = intent.getStringArrayListExtra("Experimenter ID");
        longitudeList = intent.getStringArrayListExtra("longitude");
        latitudeList = intent.getStringArrayListExtra("latitude");

        Log.d("longitudeList", longitudeList.toString());
        Log.d("latitudeList", latitudeList.toString());

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        setContentView(R.layout.activity_maps);
        Places.initialize(getApplicationContext(), "AIzaSyCJlHSuzQa1lDg0hcbZAZaOik1m4h_Xvms");
        placesClient = Places.createClient(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);





    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setCompassEnabled(true);


        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(32, 34);
//        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//
//        // Add a new marker and move the camera
//        LatLng marker1 = new LatLng(45, 34);
//        map.addMarker(new MarkerOptions().position(marker1).title("Marker 1"));
//        map.moveCamera(CameraUpdateFactory.newLatLng(marker1));
//
//        // Add a marker and move the camera
//        LatLng marker2 = new LatLng(15, 20);
//        map.addMarker(new MarkerOptions().position(marker2).title("Marker 2"));
//        map.moveCamera(CameraUpdateFactory.newLatLng(marker2));

        for(int counter = 0; counter < longitudeList.size(); counter++){
            LatLng marker1 = new LatLng(Double.parseDouble(latitudeList.get(counter)), Double.parseDouble(longitudeList.get(counter)));
            map.addMarker(new MarkerOptions().position(marker1).title(experimentIDList.get(counter) + " performed a trial here!"));
            map.moveCamera(CameraUpdateFactory.newLatLng(marker1));
        }

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation(); //get the current location from the map

        //this.currentLatitude = lastKnownLocation.getLatitude();
        //this.currentLongitude = lastKnownLocation.getLongitude();

        //LatLng myMarker = new LatLng(currentLatitude, currentLongitude);
        //map.addMarker(new MarkerOptions().position(myMarker).title("myMarker"));
        //map.moveCamera(CameraUpdateFactory.newLatLng(myMarker));

    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                //currentLatitude = lastKnownLocation.getLatitude();
                                //currentLongitude = lastKnownLocation.getLongitude();
                                //LatLng myMarker = new LatLng(currentLatitude, currentLongitude);
                                //map.addMarker(new MarkerOptions().position(myMarker).title("My location"));

                                setLa(lastKnownLocation.getLatitude());
                                setLo(lastKnownLocation.getLongitude());

                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }

                    private Double getLa() {
                        return currentLatitude;
                    }

                    private Double getLo() {
                        return currentLongitude;
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
    public void setLa(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public double getCurrentLatitude() {
        return this.currentLatitude;
    }

    public double getCurrentLongitude() {
        return this.currentLongitude;
    }

    public Double getLa() {
        return currentLatitude;
    }

    public void setLo(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public Double getLo() {
        return currentLongitude;
    }

}
