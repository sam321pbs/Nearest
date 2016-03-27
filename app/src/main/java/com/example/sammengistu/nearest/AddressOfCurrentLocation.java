package com.example.sammengistu.nearest;

import android.util.Log;

/**
 * Created by SamMengistu on 6/28/15.
 */
public class AddressOfCurrentLocation {

    private static final String TAG = "CurrentLocation";
    private String mHouseNumber;
    private String mStreetName;
    private String mCity;
    private String mState;
    private int mZipCode;
    private String mStreetOrRoad;
    private String currentAddress;

    public AddressOfCurrentLocation(String currentAddressPassedIn){
        currentAddress = currentAddressPassedIn;

        containsWhiteSpace(currentAddress);
    }

    public String getStreetOrRoad() {
        return mStreetOrRoad;
    }

    public void setStreetOrRoad(String streetOrRoad) {
        mStreetOrRoad = streetOrRoad;
    }

    private void containsWhiteSpace(final String breakAddressDown) {
        if (breakAddressDown != null) {
            int whichWhiteSpace = 0;
            int prevWhiteSpace = 0;
            for (int i = 0; i < breakAddressDown.length(); i++) {

                if (Character.isWhitespace(breakAddressDown.charAt(i))) {
                    whichWhiteSpace += 1;
                    Log.i(TAG, whichWhiteSpace + "");
                    Log.i(TAG, i + "");
                    StringBuilder sb = new StringBuilder(i);
                    if (whichWhiteSpace == 1) {
                        for (int houseNumber = 0; houseNumber < i; houseNumber++) {
                            sb.append(breakAddressDown.charAt(houseNumber));
                        }
                        prevWhiteSpace = i;
                        mHouseNumber = sb.toString();
                    }
                    if (whichWhiteSpace == 2){
                        for (int streetName = prevWhiteSpace; streetName < i; streetName++) {
                            sb.append(breakAddressDown.charAt(streetName));
                        }
                        prevWhiteSpace = i;
                        mStreetName = sb.toString();
                    }
                    if (whichWhiteSpace == 3){
                        for (int streetOrRoad = prevWhiteSpace; streetOrRoad < i; streetOrRoad++) {
                            sb.append(breakAddressDown.charAt(streetOrRoad));
                        }
                        prevWhiteSpace = i;
                        mStreetOrRoad = sb.toString();
                    }
                    if (whichWhiteSpace == 4){
                        for (int state = prevWhiteSpace; state < i; state++) {
                            sb.append(breakAddressDown.charAt(state));
                        }
                        prevWhiteSpace = i;
                        mState = sb.toString();
                    }
                }
            }
        }
    }

    public String getHouseNumber() {
        return mHouseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        mHouseNumber = houseNumber;
    }

    public String getStreetName() {
        return mStreetName;
    }

    public void setStreetName(String streetName) {
        mStreetName = streetName;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

    public int getZipCode() {
        return mZipCode;
    }

    public void setZipCode(int zipCode) {
        mZipCode = zipCode;
    }
}
