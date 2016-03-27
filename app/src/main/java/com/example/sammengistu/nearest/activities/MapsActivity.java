package com.example.sammengistu.nearest.activities;

import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sammengistu.nearest.Address;
import com.example.sammengistu.nearest.AddressLab;
import com.example.sammengistu.nearest.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity {

    private static final String TAG = "MapActivity";
    private GoogleMap mMap;
    private ListView lv;
    private List<android.location.Address> geocodeMatches;
    private ArrayList<Address> mAddressesToShowOnMap = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        geocodeMatches = null;

        mMap.setMyLocationEnabled(true);

        zoomOnMyLocation();

        ArrayList<Address> addresses =
                AddressLab.get(getApplicationContext()).getmAddressBook();

        for (Address a : addresses) {
            if (a.isShowOnMap()) {
                mAddressesToShowOnMap.add(a);
            }
        }

        final ArrayAdapter<Address> adapter = new MapListAdapter(mAddressesToShowOnMap);

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

    public class MapListAdapter extends ArrayAdapter<Address> {

        public MapListAdapter(ArrayList<Address> addresses) {
            super(MapsActivity.this, 0, addresses);
        }

        @Override
        public View getView(int postion, View convertView, ViewGroup parent) {
            //If a view wasent given to us
            if (convertView == null) {
                convertView = getLayoutInflater()
                        .inflate(R.layout.map_list_item_address, null);
            }

            Address a = getItem(postion);

            TextView addressTitleTextView = (TextView)
                    convertView.findViewById(R.id.list_item_title_of_address);
            TextView streetTextView = (TextView)
                    convertView.findViewById(R.id.list_item_streetTextView);
            TextView commuteTime = (TextView)
                    convertView.findViewById(R.id.list_item_duration_details);
            TextView commuteDistance = (TextView)
                    convertView.findViewById(R.id.list_item_distance_details);

            if (a.getDuration() != null) {
                commuteTime.setText(a.getDuration());
            } else {
                commuteTime.setText("0 min");
            }

            if (a.getDistance() != null) {
                commuteDistance.setText(a.getDistance());
            } else {
                commuteDistance.setText("0 miles");
            }

            addressTitleTextView.setText(a.getTitle());
            streetTextView.setText(a.getStreet());

            return convertView;
        }
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
