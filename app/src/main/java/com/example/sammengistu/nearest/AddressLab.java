package com.example.sammengistu.nearest;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by SamMengistu on 5/19/15.
 */
public class AddressLab {
    private static final String FILENAME = "goal.json";
    private static final String TAG = "GoalLab";

    private ArrayList<Address> mAddressBook;
    private ArrayList<Address> mAddressesToShowOnMap;

    private static AddressLab sAddressLab;
    private AddressJSONSerializer mSerializer;

    public AddressLab(Context appContext) {

        mAddressBook = new ArrayList<Address>();

        mSerializer = new AddressJSONSerializer(appContext, FILENAME);

        try {
            mAddressBook = mSerializer.loadAddresses();
        } catch (Exception e) {
            Log.e(TAG, "Error loading todos: ", e);
        }
}

    public static AddressLab get(Context c) {
        if (sAddressLab == null){
            sAddressLab = new AddressLab(c.getApplicationContext());
        }
        return sAddressLab;
    }

    public ArrayList<Address> getmAddressBook() {
        return mAddressBook;
    }

    public Address getAddress(UUID id) {
        for (Address a : mAddressBook) {
            if (a.getmId().equals(id)){
                return a;
            }
        }
        return null;
    }

    public String createAddressUrl() {

        String url = "";
        for (Address address : mAddressBook) {
                url += (address.getGoogleFormattedAddress());
                url += ("|");
            }
        return (url.length() == 0 ? "" : url.substring(0, url.length() - 1));
    }

    public void addAddress(Address address) {
        mAddressBook.add(address);
    }

    public void deleteAddress(Address address) {
        mAddressBook.remove(address);
    }

    public boolean saveAddress(){
        try {
            mSerializer.saveAddress(mAddressBook);
            Log.i(TAG, "crimes saved to file");
            return true;
        } catch (Exception e){
            Log.i(TAG, "Error sacing crimes: " + e);
            return false;
        }
    }
}
