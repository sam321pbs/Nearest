package com.example.sammengistu.nearest;

import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by SamMengistu on 7/7/15.
 */
public class SetUpCommuteInfoForAddresses {

    private static final String TAG = "SetUpCommute";
    private ArrayList<Address> mAddresses;
    private String mUrlForMaps;
    private Geocoder mGeoCoder;
    private LocationManager mLocationManager;

    public SetUpCommuteInfoForAddresses (Context appContext){

        mAddresses = new ArrayList<>();
        mAddresses = AddressLab.get(appContext).getmAddressBook();
        mUrlForMaps = AddressLab.get(appContext).createAddressUrl();
        mGeoCoder = new Geocoder(appContext, Locale.getDefault());
        mLocationManager =  (LocationManager) appContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public void setUpTravelInfo() {
        if (mAddresses != null) {
            try {
                URL googlePlaces =
                        new URL("https://maps.googleapis.com/maps/api/distancematrix/json?" +
                                "origins=" + getAddressOfCurrentLocation() +
                                "&destinations=" + URLEncoder.encode(mUrlForMaps, "UTF-8").replaceAll("\\+", "%20") +
                                "&units=imperial&types=geocode&language=en&sensor=true&key=AIzaSyCjZEowtt-LIAMjX9ghcVHWJ1nVc7V6DbE");

                Log.i(TAG, googlePlaces + "");

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(googlePlaces)
                        .build();

                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {

                        Log.i(TAG, "failed getting data");
                        Log.i(TAG, e.getMessage());

                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        try {
                            String jsonData = response.body().string();
                            if (response.isSuccessful()) {
                                ArrayList<String> destinationTimes = getDurationOfCommutes(jsonData);
                                ArrayList<String> destinationDistances = getDistanceOfCommutes(jsonData);
                                for (int i = 0; i < destinationTimes.size(); i++) {
                                    mAddresses.get(i).setDuration(destinationTimes.get(i));
                                    mAddresses.get(i).setDistance(destinationDistances.get(i));
                                }
                            }
                        } catch (Exception e) {
                            Log.i(TAG, "Failed " + e.getMessage());
                        }
                    }
                });

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {

            }
        } else {
            Log.i(TAG, "its null");
        }
    }

    private ArrayList<String> getDurationOfCommutes(String jsonData) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonData);

        ArrayList<String> destinationTimes = new ArrayList<>();

        JSONArray row = jsonObject.getJSONArray("rows");
        JSONObject object = row.getJSONObject(0);
        JSONArray elements = object.getJSONArray("elements");

        for (int i = 0; i < elements.length(); i++) {

            JSONObject elementsData = elements.getJSONObject(i);

            JSONObject trueDuration = elementsData.getJSONObject("duration");

            String durationString = trueDuration.getString("text");
            destinationTimes.add(durationString);
        }

        return destinationTimes;
    }

    private ArrayList<String> getDistanceOfCommutes(String jsonData) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonData);

        ArrayList<String> destinationDistance = new ArrayList<>();

        JSONArray row = jsonObject.getJSONArray("rows");
        JSONObject object = row.getJSONObject(0);
        JSONArray elements = object.getJSONArray("elements");

        for (int i = 0; i < elements.length(); i++) {

            JSONObject elementsData = elements.getJSONObject(i);

            JSONObject trueDuration = elementsData.getJSONObject("distance");

            String distanceString = trueDuration.getString("text");
            destinationDistance.add(distanceString);
        }

        return destinationDistance;
    }

    private String getAddressOfCurrentLocation() {
        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        String currentLocationAddress = "";


        StringBuilder builder = new StringBuilder();
        try {
            List<android.location.Address> address = mGeoCoder.getFromLocation(latitude, longitude, 1);
            int maxLines = address.get(0).getMaxAddressLineIndex();
            for (int i = 0; i < maxLines; i++) {
                String addressStr = address.get(0).getAddressLine(i);
                builder.append(addressStr);
                builder.append(" ");
            }

            currentLocationAddress = builder.toString(); //This is the complete address.
        } catch (IOException e) {
        } catch (NullPointerException e) {
        }
        currentLocationAddress = currentLocationAddress.replaceAll(",", "");
        currentLocationAddress = currentLocationAddress.replaceAll(" ", "+");

        currentLocationAddress = currentLocationAddress.substring(0, currentLocationAddress.length() - 1);

        Log.i(TAG, currentLocationAddress);

        return currentLocationAddress;
    }
}
