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

import com.example.sammengistu.nearest.Address;
import com.example.sammengistu.nearest.AddressLab;
import com.example.sammengistu.nearest.R;
import com.example.sammengistu.nearest.SetUpCommuteInfoForAddresses;
import com.example.sammengistu.nearest.adapters.MapListAdapter;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

public class MapsActivity extends FragmentActivity implements
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MapActivity";
    private GoogleMap mMap;
    private ListView mCommuteInfoListView;
    private List<android.location.Address> geocodeMatches;
    private List<Address> mAddressesToShowOnMap = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private ArrayAdapter<Address> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        geocodeMatches = null;
        mMap.setMyLocationEnabled(true);

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

        List<Address> addresses =
            AddressLab.get(getApplicationContext()).getmAddressBook();

        for (Address a : addresses) {
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
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
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

        if (mLastLocation != null) {
            zoomOnMyLocation();
            new LoadCommuteInfoTaskk(this, mCommuteInfoListView,  mLastLocation,
                mAddressesToShowOnMap, mMap).execute();
        } else {
            Log.i(TAG, "last location = null");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public class LoadCommuteInfoTaskk extends AsyncTask<Void, Void, Void> {

        private Location mCurrentLocation;
        private Activity mAppContext;
        private List<Address> mAddressesToShowOnMap;
        private ListView mCommuteInfoListView;
        private GoogleMap mGoogleMap;

        public LoadCommuteInfoTaskk (Activity appContext, ListView listView,
                                    Location currentLocation, List<Address> addressToShowOnMap,
                                    GoogleMap googleMap){

            mCurrentLocation = currentLocation;
            mAppContext = appContext;
            mAddressesToShowOnMap = addressToShowOnMap;
            mCommuteInfoListView = listView;
            mGoogleMap = googleMap;
        }
        @Override
        protected Void doInBackground(Void... params) {
            SetUpCommuteInfoForAddresses setUpCommuteInfoForAddresses =
                new SetUpCommuteInfoForAddresses(mAppContext, mCurrentLocation);

            setUpCommuteInfoForAddresses.setUpTravelInfo();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            //TODO: data not updated
            //  setUpTravelInfo();
            ((MapListAdapter) mCommuteInfoListView.getAdapter()).notifyDataSetChanged();
        }
    }

}
