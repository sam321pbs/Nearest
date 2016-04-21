package com.example.sammengistu.nearest;

import com.google.android.gms.maps.GoogleMap;

import com.example.sammengistu.nearest.adapters.MapListAdapter;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.ListView;

import java.util.List;

public class LoadCommuteInfoTask extends AsyncTask<Void, Void, Void> {

    private Location mCurrentLocation;
    private Activity mAppContext;
    private List<Address> mAddressesToShowOnMap;
    private ListView mCommuteInfoListView;
    private GoogleMap mGoogleMap;

    public LoadCommuteInfoTask (Activity appContext, ListView listView,
                                Location currentLocation, List<Address> addressToShowOnMap,
                                GoogleMap googleMap){

        mCurrentLocation = currentLocation;
        mAppContext = appContext;
        mAddressesToShowOnMap = addressToShowOnMap;
        mCommuteInfoListView = listView;
        mGoogleMap = googleMap;
    }
    @Override
    protected Void doInBackground(Void... params) {
        SetUpCommuteInfoForAddresses setUpCommuteInfoForAddresses =
            new SetUpCommuteInfoForAddresses(mAppContext, mCurrentLocation);

        setUpCommuteInfoForAddresses.setUpTravelInfo();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        //TODO: data not updated
        //  setUpTravelInfo();
        ((MapListAdapter) mCommuteInfoListView.getAdapter()).notifyDataSetChanged();
    }
}
