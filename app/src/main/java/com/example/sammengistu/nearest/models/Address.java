package com.example.sammengistu.nearest.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;


public class Address {

    private static final String JSON_ID = "Address.Id";
    private static final String JSON_TITLE = "Address.Title";
    private static final String JSON_FULL_ADDRESS = "Address.FullAdress";

    private UUID mId;
    private String mFullAddress;
    private String mTitle;
    private boolean mShowOnMap;
    private double mLatitude;
    private double mLongitude;
    private String mDistance;
    private String mDuration;

    public Address(String fullAddress) {
        mId = UUID.randomUUID();
        mFullAddress = fullAddress;
    }

    public Address(JSONObject json) throws JSONException {

        mId = UUID.fromString(json.getString(JSON_ID));
        if (json.has(JSON_TITLE)) {
            mTitle = json.getString(JSON_TITLE);
        }
        if (json.has(JSON_FULL_ADDRESS)){
            mFullAddress = json.getString(JSON_FULL_ADDRESS);
        }
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mId);
        json.put(JSON_TITLE, mTitle);
        json.put(JSON_FULL_ADDRESS, mFullAddress);

        return json;
    }

    public String getFullAddress(){
        return mFullAddress;
    }

    public UUID getmId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public boolean isShowOnMap() {
        return mShowOnMap;
    }

    public void setShowOnMap(boolean mShowOnMap) {
        this.mShowOnMap = mShowOnMap;
    }

    public String getGoogleFormattedAddress(){
        return  getFullAddress().replaceAll(",", "").replaceAll(" ", "+");
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String distance) {
        mDistance = distance;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        mDuration = duration;
    }
}
