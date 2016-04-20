package com.example.sammengistu.nearest.activities;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.example.sammengistu.nearest.Address;
import com.example.sammengistu.nearest.AddressLab;
import com.example.sammengistu.nearest.MapListAdapter;
import com.example.sammengistu.nearest.R;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity {

    private static final String TAG = "MapActivity";
    private GoogleMap mMap;
    private ListView lv;
    private List<android.location.Address> geocodeMatches;
    private List<Address> mAddressesToShowOnMap = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        geocodeMatches = null;

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
        mMap.setMyLocationEnabled(true);

        zoomOnMyLocation();

        ArrayList<Address> addresses =
                AddressLab.get(getApplicationContext()).getmAddressBook();

        for (Address a : addresses) {
            if (a.isShowOnMap()) {
                mAddressesToShowOnMap.add(a);
            }
        }

        final ArrayAdapter<Address> adapter = new MapListAdapter(MapsActivity.this, mAddressesToShowOnMap);

        lv = (ListView) findViewById(android.R.id.list);
        lv.getLayoutParams().height = 400;

        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Address address = ((MapListAdapter) lv.getAdapter()).getItem(position);

                LatLng currentItemOnList = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentItemOnList, 15));
            }
        });
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
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
        Log.i(TAG, "Setting up Map");
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
                Marker marker = mMap.addMarker(
                        new MarkerOptions().position(new LatLng(latitude, longitude))
                                .visible(true)
                                .title(addressToDisplay.getTitle())
                );
            }
        }
    }

    private void zoomOnMyLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        LatLng currentItemOnList = new LatLng(latitude, longitude);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentItemOnList, 15));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
      //  setUpTravelInfo();
        ((MapListAdapter) lv.getAdapter()).notifyDataSetChanged();
    }
}
