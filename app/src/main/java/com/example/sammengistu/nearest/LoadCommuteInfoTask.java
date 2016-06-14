package com.example.sammengistu.nearest;

import com.google.android.gms.maps.GoogleMap;

import com.example.sammengistu.nearest.models.Address;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LoadCommuteInfoTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "LoadCommuteInfoTask55";
    private Location mCurrentLocation;
    private Activity mAppContext;
    private List<Address> mAddressesToShowOnMap;
    private List<SetUpCommuteInfoForAddresses.CommuteInfoBundle> mCommuteInfoBundles;
    private RecyclerView.Adapter mCommuteInfoRecyclerViewAdapter;
    private GoogleMap mGoogleMap;
    private RecyclerView mRecyclerViewAddresses;

    public LoadCommuteInfoTask (Activity appContext, RecyclerView.Adapter recyclerView,
                                Location currentLocation, List<Address> addressToShowOnMap,
                                GoogleMap googleMap, RecyclerView recyclerViewAddresses){

        mCurrentLocation = currentLocation;
        mAppContext = appContext;
        mAddressesToShowOnMap = addressToShowOnMap;
//        mCommuteInfoRecyclerViewAdapter = new CardViewMapInfoAdapter(addressToShowOnMap);
        mGoogleMap = googleMap;
        mRecyclerViewAddresses = recyclerViewAddresses;
    }

    @Override
    protected void onPreExecute() {
        mRecyclerViewAddresses.setAdapter(mCommuteInfoRecyclerViewAdapter);
        mCommuteInfoBundles = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Void... params) {
//        SetUpCommuteInfoForAddresses setUpCommuteInfoForAddresses =
//            new SetUpCommuteInfoForAddresses(mAppContext, mCurrentLocation);
//
//       mCommuteInfoBundles = setUpCommuteInfoForAddresses.setUpTravelInfo(mAppContext,
//           mAddressesToShowOnMap);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        //TODO: data not updated
        //  setUpTravelInfo();

        List<Address> addressList = mAddressesToShowOnMap;
        Log.i(TAG, "Commute size = " + mCommuteInfoBundles.size());

        for (int i = 0 ; i < mAddressesToShowOnMap.size(); i++) {
            Log.i(TAG, "AddressList size = " + addressList.size());
            Log.i(TAG, "Commute info bundle duration = " +
                mAddressesToShowOnMap.get(i).getCommuteTime());
        }
//        mRecyclerViewAddresses.setAdapter(new CardViewMapInfoAdapter(
//            mAddressesToShowOnMap));
//       mRecyclerViewAddresses.getAdapter().notifyDataSetChanged();
    }
}
