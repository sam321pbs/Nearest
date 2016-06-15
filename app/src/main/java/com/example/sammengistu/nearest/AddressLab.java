package com.example.sammengistu.nearest;

import com.example.sammengistu.nearest.models.Address;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class AddressLab {
    private static final String FILENAME = "goal.json";
    private static final String TAG = "AddressLab";

    public static List<Address> sAddressBook;

    private static AddressLab sAddressLab;
    private AddressJSONSerializer mSerializer;

    public AddressLab(Context appContext) {

        sAddressBook = new ArrayList<>();

        mSerializer = new AddressJSONSerializer(appContext, FILENAME);

        try {
            sAddressBook = mSerializer.loadAddresses();
        } catch (Exception e) {
            Log.e(TAG, "Error loading todos: ", e);
        }
    }

    public static AddressLab get(Context c) {
        if (sAddressLab == null) {
            Log.i(TAG, "Address == null, setting up");
            sAddressLab = new AddressLab(c.getApplicationContext());
        }
        return sAddressLab;
    }

    public List<Address> getmAddressBook() {
        return sAddressBook;
    }

    public Address getAddress(UUID id) {
        for (Address a : sAddressBook) {
            if (a.getmId().equals(id)) {
                return a;
            }
        }
        return null;
    }

    public String createAddressUrl() {

        String url = "";
        for (Address address : sAddressBook) {
            url += (address.getGoogleFormattedAddress());
            url += ("|");
        }
        return (url.length() == 0 ? "" : url.substring(0, url.length() - 1));
    }

    public static String createSingleAddressUrl(Address address) {

        String url = "";

        url += (address.getGoogleFormattedAddress());
        url += ("|");

        return (url.length() == 0 ? "" : url.substring(0, url.length() - 1));
    }

    public void addAddress(Address address) {
        sAddressBook.add(address);
    }

    public void deleteAddress(Address address) {
        sAddressBook.remove(address);
    }

    public boolean saveAddress() {
        try {
            mSerializer.saveAddress(sAddressBook);
            Log.i(TAG, "crimes saved to file");
            return true;
        } catch (Exception e) {
            Log.i(TAG, "Error sacing crimes: " + e);
            return false;
        }
    }

    public void setList(List<Address> sortedList){
        sAddressBook = sortedList;
    }
}
