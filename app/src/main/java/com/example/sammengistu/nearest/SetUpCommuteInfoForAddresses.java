package com.example.sammengistu.nearest;

import com.google.android.gms.maps.GoogleMap;

import com.example.sammengistu.nearest.adapters.CardViewMapInfoAdapter;
import com.example.sammengistu.nearest.models.Address;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
    public static Geocoder mGeoCoder;
    private Activity mAppContext;
    private Location mCurrentLocation;
    private RecyclerView mRecyclerView;

    public SetUpCommuteInfoForAddresses (Activity appContext, Location currentLocation, RecyclerView recyclerView){

        mCurrentLocation = currentLocation;
        mAppContext = appContext;
        mAddresses = AddressLab.get(appContext).getmAddressBook();
        mUrlForMaps = AddressLab.get(appContext).createAddressUrl();
        mGeoCoder = new Geocoder(appContext, Locale.getDefault());
        mRecyclerView = recyclerView;
    }

    public String createAddressUrl(List<Address> addressList) {

        String url = "";
        for (Address address : addressList) {
            url += (address.getGoogleFormattedAddress());
            url += ("|");
        }
        return (url.length() == 0 ? "" : url.substring(0, url.length() - 1));
    }

    public void setUpTravelInfo(final List<Address> addressList, final GoogleMap map) {

        if (addressList != null) {
            try {
                URL googlePlaces =
                        new URL("https://maps.googleapis.com/maps/api/distancematrix/json?" +
                                "origins=" + getAddressOfCurrentLocation(mCurrentLocation) +
                                "&destinations=" + URLEncoder.encode(
                            createAddressUrl(addressList), "UTF-8").replaceAll("\\+", "%20") +
                                "&units=imperial&types=geocode&language=en&sensor=true&key=" +
                            //Todo: Remove key
                            mAppContext.getString(R.string.api_key_url));

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
                                    addressList.get(i).setCommuteTime(destinationTimes.get(i));
                                    addressList.get(i).setDistance(destinationDistances.get(i));
                                }

                                mAppContext.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        List<Address> addressListToSort = SortAddress
                                            .sortAddresses(AddressLab.get(mAppContext)
                                                .getmAddressBook(), true);
                                        AddressLab.sAddressBook = addressListToSort;

                                        mRecyclerView.setAdapter(
                                            new CardViewMapInfoAdapter(mAppContext, map));
                                        mRecyclerView.getAdapter().notifyDataSetChanged();
                                    }
                                });
//                                mRecyclerView.setAdapter(new CardViewMapInfoAdapter(addressList));


//                                Intent intent = new Intent(currentActivity, mapClass);
//                                currentActivity.startActivity(intent);
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

    public static ArrayList<String> getDistanceOfCommutes(String jsonData, Context appContext)
        throws JSONException {

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

    public static String getAddressOfCurrentLocation(Location location) {

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
