package com.example.sammengistu.nearest;

import com.example.sammengistu.nearest.models.Address;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SetUpCommuteInfoForAddresses {

    private static final String TAG = "SetUpCommute";
    private List<Address> mAddresses;
    private String mUrlForMaps;
    private Geocoder mGeoCoder;
    private Context mAppContext;
    private Location mCurrentLocation;
    private Address mSingleAddress;

    public SetUpCommuteInfoForAddresses (Context appContext, Location currentLocation){

        mCurrentLocation = currentLocation;
        mAppContext = appContext;
        mAddresses = AddressLab.get(appContext).getmAddressBook();
        mUrlForMaps = AddressLab.get(appContext).createAddressUrl();
        mGeoCoder = new Geocoder(appContext, Locale.getDefault());
    }

//    private void getGoogleJsonResponse(String url) throws NullPointerException{
//
//        if (mAddresses != null) {
//            try {
//                URL googlePlaces =
//                    new URL("https://maps.googleapis.com/maps/api/distancematrix/json?" +
//                        "origins=" + getAddressOfCurrentLocation() +
//                        "&destinations=" + URLEncoder.encode(url, "UTF-8").replaceAll("\\+", "%20") +
//                        "&units=imperial&types=geocode&language=en&sensor=true&key=" +
//                        //Todo: Remove key
//                        "AIzaSyCjZEowtt-LIAMjX9ghcVHWJ1nVc7V6DbE");
//
//                Log.i(TAG, googlePlaces + "");
//
//                OkHttpClient client = new OkHttpClient();
//
//                Request request = new Request.Builder()
//                    .url(googlePlaces)
//                    .build();
//
//                Call call = client.newCall(request);
//                call.enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Request request, IOException e) {
//
//                        Log.i(TAG, "failed getting data");
//                        Log.i(TAG, e.getMessage());
//
//                    }
//
//                    @Override
//                    public void onResponse(Response response) throws IOException {
//                        Log.i("Pop up", response.isSuccessful() + "");
//                    }
//                });
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (UnsupportedEncodingException e) {
//
//            }
//        } else {
//            Log.i(TAG, "its null");
//        }
//    }

    public void getTravelInfoSingleAddress(Address address, final TextView distance, final TextView eta) {
        mSingleAddress = new Address(address.getFullAddress());

        if (mAddresses != null) {
            try {
                URL googlePlaces =
                    new URL("https://maps.googleapis.com/maps/api/distancematrix/json?" +
                        "origins=" + getAddressOfCurrentLocation() +
                        "&destinations=" + URLEncoder.
                        encode(AddressLab.createSingleAddressUrl(address), "UTF-8").replaceAll("\\+", "%20") +
                        "&units=imperial&types=geocode&language=en&sensor=true&key=" +
                        //Todo: Remove key
                        "AIzaSyCjZEowtt-LIAMjX9ghcVHWJ1nVc7V6DbE");

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
                        Log.i("Pop up", response.isSuccessful() + "");

                        try {
                            String jsonData = response.body().string();
                            if (response.isSuccessful()) {
                                Log.i("Pop up", "Google respose");
                                List<String> destinationTimes = getDurationOfCommutes(jsonData, mAppContext);
                                List<String> destinationDistances = getDistanceOfCommutes(jsonData, mAppContext);
                                for (int i = 0; i < destinationTimes.size(); i++) {
                                    Log.i("Pop up", "eta time = " + destinationTimes.get(i));
                                    Log.i("Pop up", "dist = " + destinationDistances.get(i));
                                    eta.setText(destinationTimes.get(i));
                                    distance.setText(destinationDistances.get(i));
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            Log.i(TAG, "Failed " + e.getMessage());
//            Toast.makeText(mAppContext, "Error getting data", Toast.LENGTH_LONG).show();
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

    public void setUpTravelInfo() {
        if (mAddresses != null) {
            try {
                URL googlePlaces =
                        new URL("https://maps.googleapis.com/maps/api/distancematrix/json?" +
                                "origins=" + getAddressOfCurrentLocation() +
                                "&destinations=" + URLEncoder.encode(mUrlForMaps, "UTF-8").replaceAll("\\+", "%20") +
                                "&units=imperial&types=geocode&language=en&sensor=true&key=" +
                            //Todo: Remove key
                            "AIzaSyCjZEowtt-LIAMjX9ghcVHWJ1nVc7V6DbE");

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
                                List<String> destinationTimes = getDurationOfCommutes(jsonData, mAppContext);
                                List<String> destinationDistances = getDistanceOfCommutes(jsonData, mAppContext);
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

    public static JSONArray getFromJSONArray (String jsonData, Context appContext) throws JSONException{
        JSONObject jsonObject = new JSONObject(jsonData);

        JSONArray row = jsonObject.getJSONArray(appContext.getString(R.string.json_row));
        JSONObject object = row.getJSONObject(0);
        JSONArray elements = object.getJSONArray(appContext.getString(R.string.json_element));

        return elements;
    }

    public static List<String> getDurationOfCommutes(String jsonData, Context appContext) throws JSONException {

        List<String> destinationTimes = new ArrayList<>();

        JSONArray elements = getFromJSONArray(jsonData, appContext);

        for (int i = 0; i < elements.length(); i++) {

            JSONObject elementsData = elements.getJSONObject(i);

            JSONObject trueDuration = elementsData.getJSONObject(appContext.getString(R.string.json_duration));

            destinationTimes.add(trueDuration.getString(appContext.getString(R.string.json_text)));
        }

        return destinationTimes;
    }

    public static ArrayList<String> getDistanceOfCommutes(String jsonData, Context appContext) throws JSONException {

        ArrayList<String> destinationDistance = new ArrayList<>();

        JSONArray elements = getFromJSONArray(jsonData, appContext);

        for (int i = 0; i < elements.length(); i++) {

            JSONObject elementsData = elements.getJSONObject(i);

            JSONObject trueDuration = elementsData.getJSONObject(
                appContext.getString(R.string.json_distance));

            destinationDistance.add(trueDuration.getString(appContext.getString(R.string.json_text)));
        }

        return destinationDistance;
    }

    public String getAddressOfCurrentLocation() {

        double longitude = mCurrentLocation.getLongitude();
        double latitude = mCurrentLocation.getLatitude();
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
