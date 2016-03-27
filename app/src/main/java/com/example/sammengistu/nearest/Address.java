package com.example.sammengistu.nearest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by SamMengistu on 6/23/15.
 */
public class Address {

    private static final String JSON_ID = "Address.Id";
    private static final String JSON_TITLE = "Address.Title";
    private static final String JSON_STREET = "Address.Street";
    private static final String JSON_CITY = "Address.City";
    private static final String JSON_STATE = "Address.State";
    private static final String JSON_ZIPCODE = "Address.ZipCode";
    private static final String JSON_SHOW_ON_MAP = "Address.ShowOnMap";

    private UUID mId;
    private String mTitle;
    private String mStreet;
    private String mCity;
    private String mState;
    private boolean mShowOnMap;
    private int mZipCode;
    private double latitude;
    private double longitude;
    private String mDistance;
    private String mDuration;

    public Address() {
        mId = UUID.randomUUID();
    }

    public Address(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        if (json.has(JSON_TITLE)) {
            mTitle = json.getString(JSON_TITLE);
        }
        if (json.has(JSON_STREET)) {
            mStreet = json.getString(JSON_STREET);
        }
        if (json.has(JSON_CITY)) {
            mCity = json.getString(JSON_CITY);
        }
        if (json.has(JSON_STATE)) {
            mState = json.getString(JSON_STATE);
        }
        if (json.has(JSON_SHOW_ON_MAP)){
            mShowOnMap = json.getBoolean(JSON_SHOW_ON_MAP);
        }
        if (json.has(JSON_ZIPCODE)){
            mZipCode = json.getInt(JSON_ZIPCODE);
        }
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mId);
        json.put(JSON_TITLE, mTitle);
        json.put(JSON_STREET, mStreet);
        json.put(JSON_CITY, mCity);
        json.put(JSON_STATE, mState);
        json.put(JSON_ZIPCODE, mZipCode);
        json.put(JSON_SHOW_ON_MAP, mShowOnMap);

        return json;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public UUID getmId() {
        return mId;
    }

    public void setmId(UUID mId) {
        this.mId = mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getStreet() {
        return mStreet;
    }

    public void setStreet(String mStreet) {
        this.mStreet = mStreet.trim();
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String mCity) {
        this.mCity = mCity.trim();
    }

    public String getState() {
        return mState;
    }

    public void setState(String mState) {
        this.mState = mState.trim();
    }

    public int getZipCode() {
        return mZipCode;
    }

    public void setZipCode(int mZipCode) {
        this.mZipCode = mZipCode;
    }

    public boolean isShowOnMap() {
        return mShowOnMap;
    }

    public void setShowOnMap(boolean mShowOnMap) {
        this.mShowOnMap = mShowOnMap;
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

    public String getFullAddress() {
        return mStreet + " "  + mCity + " " + mState + " " + mZipCode;
    }

    public String getGoogleFormattedAddress(){
        return  mStreet.replaceAll(" ", "+") + "+" +
                mCity.replaceAll(" ", "+") + "+" + mState.replaceAll(" ", "+")+ "+" + mZipCode;
    }
}
