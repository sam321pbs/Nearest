package com.example.sammengistu.nearest.activities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.example.sammengistu.nearest.AddressLab;
import com.example.sammengistu.nearest.R;
import com.example.sammengistu.nearest.SortAddress;
import com.example.sammengistu.nearest.adapters.MapListAdapter;
import com.example.sammengistu.nearest.dialogs.SortDialog;
import com.example.sammengistu.nearest.models.Address;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    SortDialog.SortListener {

    private static final String TAG = "MapActivity";
    private static final int GET_SORT_TYPE = 5;
    private GoogleMap mMap;
    private ListView mCommuteInfoListView;
    private List<android.location.Address> geocodeMatches;
    private List<Address> mAddressesToShowOnMap = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private ArrayAdapter<Address> mAdapter;
    private List<Address> mAddresses;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        geocodeMatches = null;
        mMap.setMyLocationEnabled(true);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_maps_activity);
        myToolbar.setBackgroundColor(getResources().getColor(R.color.theme_primary));

        TextView sortTextView = (TextView) findViewById(R.id.sort_toolbar);
        sortTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SortDialog sortDialog = new SortDialog();
                sortDialog.show(getSupportFragmentManager(), "Sort dialog");
            }
        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (mGoogleApiClient == null) {
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        }

        mAddresses = AddressLab.get(getApplicationContext()).getmAddressBook();

        mAddresses = SortAddress.sortAddresses(mAddresses, true);

        for (Address a : mAddresses) {
            if (a.isShowOnMap()) {
                mAddressesToShowOnMap.add(a);
            }
        }

        mCommuteInfoListView = (ListView) findViewById(android.R.id.list);
        mCommuteInfoListView.getLayoutParams().height = 400;

        mAdapter = new MapListAdapter(this, mAddressesToShowOnMap);
        mCommuteInfoListView.setAdapter(mAdapter);

        mCommuteInfoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Address address = ((MapListAdapter) mCommuteInfoListView.getAdapter()).getItem(position);

                LatLng currentItemOnList = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentItemOnList, 15));
            }
        });
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only
     * ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    @SuppressWarnings("deprecation")
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.location_map))
                .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        for (Address addressToDisplay : AddressLab.get(getApplicationContext()).getmAddressBook()) {
            if (addressToDisplay.isShowOnMap()) {
                double latitude = 0;
                double longitude = 0;
                try {
                    geocodeMatches =
                        new Geocoder(this).getFromLocationName(
                            addressToDisplay.getFullAddress(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!geocodeMatches.isEmpty()) {
                    latitude = geocodeMatches.get(0).getLatitude();
                    longitude = geocodeMatches.get(0).getLongitude();
                    addressToDisplay.setLatitude(latitude);
                    addressToDisplay.setLongitude(longitude);
                }
                mMap.addMarker(
                    new MarkerOptions().position(new LatLng(latitude, longitude))
                        .visible(true)
                        .title(addressToDisplay.getTitle())
                );
            }
        }
    }

    private void zoomOnMyLocation() {

        double longitude = mLastLocation.getLongitude();
        double latitude = mLastLocation.getLatitude();
        LatLng currentItemOnList = new LatLng(latitude, longitude);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentItemOnList, 15));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();

        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connecting to api");

        if (ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
            mGoogleApiClient);

        zoomOnMyLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, boolean distance) {

        mAddresses = SortAddress.sortAddresses(mAddresses, distance);


        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
