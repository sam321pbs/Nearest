package com.example.sammengistu.nearest;

import android.app.Fragment;
import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by SamMengistu on 6/28/15.
 */
public class MapFragment extends Fragment {

    private static final String TAG = "MapFragment";
    private GoogleMap mMap;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_maps, parent, false);
        //setUpMapIfNeeded();


        return v;
    }
//        mMap.setMyLocationEnabled(true);

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    /***** Sets up the map if it is possible to do so *****/
    public  void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) MapFragment.fragmentManager
                    .findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null)
                setUpMap();
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null. **/

    private void setUpMap() {
        List<android.location.Address> geocodeMatches = null;

        for (Address a : AddressLab.get(getApplicationContext()).getmAddressBook()){
            if (a.isShowOnMap()) {
                Log.i(TAG, a.isShowOnMap() + "");
            }
        }

        for (Address a : AddressLab.get(getApplicationContext()).getmAddressBook()){
            if (a.isShowOnMap()) {
                //List<android.location.Address> geocodeMatches1 = null;
                double latitude = 0;
                double longitude = 0;
                try {
                    geocodeMatches =
                            new Geocoder(this).getFromLocationName(
                                    a.getFullAddress(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!geocodeMatches.isEmpty()) {
                    latitude = geocodeMatches.get(0).getLatitude();
                    longitude = geocodeMatches.get(0).getLongitude();
                }
                Marker marker = mMap.addMarker(
                        new MarkerOptions().position(new LatLng(latitude, longitude))
                                .visible(true)
                                .title(a.getTitle())
                );
            }
        }

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();


        float [] hold = new float[1];
        Location.distanceBetween(latitude, longitude, latitude + .0005, longitude, hold);

        Marker marker1 = mMap.addMarker(
                new MarkerOptions().position(new LatLng(latitude + .0005, longitude))
                        .visible(true)
        );

        Log.i(TAG, hold[0] + "");

        final LatLng MUSEUM =
                new LatLng(latitude, longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MUSEUM, 12));


        URL googlePlaces =
                new URL("https://maps.googleapis.com/maps/api/distancematrix/json?" +
                        "origins=10817+childs+st+Silver+Spring+MD&"+
                        "destinations=4614+Guilford+rd+college+park+md"+
                        "&types=geocode&language=en&sensor=true&key=AIzaSyCjZEowtt-LIAMjX9ghcVHWJ1nVc7V6DbE");

        https://maps.googleapis.com/maps/api/distancematrix/json?origins=10817+childs+st+Silver+Spring+MD&destinations=4614+Guilford+rd+college+park+md+&language=fr-FR&key=AIzaSyCjZEowtt-LIAMjX9ghcVHWJ1nVc7V6DbE


        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/distancematrix/json?origins=10817+childs+st+Silver+Spring+MD&destinations=4614+Guilford+rd+college+park+md+&language=fr-FR&key=AIzaSyCjZEowtt-LIAMjX9ghcVHWJ1nVc7V6DbE")
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String jsonData = response.body().string();
                    if (response.isSuccessful()) {
                        CurrentCommuteInfo currentCommuteInfo = getCurrentDetails(jsonData);

                    }
                } catch (Exception e) {
                    Log.i(TAG, e.getMessage());
                }
            }
        });



        String run(String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();





  //  }

    private CurrentCommuteInfo getCurrentDetails(String jsonData) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonData);
        String distance = jsonObject.getString("distance");
        Log.i(TAG + "SAM OVER HERE!", distance);

        return null;
    }
}


