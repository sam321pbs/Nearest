package com.example.sammengistu.nearest.fragments;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import com.example.sammengistu.nearest.AddressLab;
import com.example.sammengistu.nearest.R;
import com.example.sammengistu.nearest.SetUpCommuteInfoForAddresses;
import com.example.sammengistu.nearest.adapters.AddressAdapter;
import com.example.sammengistu.nearest.dialogs.PopUpMapDialog;
import com.example.sammengistu.nearest.dialogs.TypeTitleDialog;
import com.example.sammengistu.nearest.models.Address;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class AddressesListFragment extends Fragment implements AbsListView.OnItemClickListener,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "AddressListFragment";
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int GET_TITLE = 2;

    private List<Address> mAddresses;

    private AbsListView mListView;
    private ListAdapter mAdapter;
    private Address mSelectedAddress;
    private GoogleApiClient mGoogleApiClient;
    private SetUpCommuteInfoForAddresses mSetUpCommuteInfoForAddresses;
    private FloatingActionButton mFloatingActionButtonAddAddress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mAddresses = AddressLab.get(getActivity()).getmAddressBook();

        mAdapter = new AddressAdapter(getActivity(), mAddresses);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
            .addConnectionCallbacks(AddressesListFragment.this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();

        mGoogleApiClient.connect();

    }

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_addresses, container, false);

        // Set the adapter
        mListView = (AbsListView) v.findViewById(android.R.id.list);

        mListView.setAdapter(mAdapter);
        registerForContextMenu(mListView);

        mListView.setOnItemClickListener(this);

        mFloatingActionButtonAddAddress = (FloatingActionButton) v.findViewById(R.id.fab_add);

        mFloatingActionButtonAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                        .build();

                    Intent intent =
                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .build(getActivity());

                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                    Log.i(TAG, e.getMessage());
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                    Log.i(TAG, e.getMessage());
                }
            }
        });

        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Address address = ((AddressAdapter) mListView.getAdapter()).getItem(position);
//
//        mSetUpCommuteInfoForAddresses = new SetUpCommuteInfoForAddresses(getActivity(),
//            getLastKnownLocation());

        getTravelInfoSingleAddress(address);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_addresses, menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {

            if (resultCode == getActivity().RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);

                Log.i(TAG, "Place: " + place.getAddress());

                mSelectedAddress = null;

                mSelectedAddress = new Address(place.getAddress().toString());

                TypeTitleDialog typeTitleDialog = new TypeTitleDialog();
                typeTitleDialog.setTargetFragment(AddressesListFragment.this,
                    GET_TITLE);

                typeTitleDialog.show(getFragmentManager(), TypeTitleDialog.TYPED_TITLE_STRING);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else {
                // The user canceled the operation.
            }
        } else if (requestCode == GET_TITLE) {

            mSelectedAddress.setTitle(data.getStringExtra(TypeTitleDialog.TYPED_TITLE_STRING));

            AddressLab.get(getActivity()).addAddress(mSelectedAddress);

            AddressLab.get(getActivity()).saveAddress();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.address_list_item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        AddressAdapter adapter = (AddressAdapter) mListView.getAdapter();
        Address address = adapter.getItem(position);

        switch (item.getItemId()) {
            case R.id.menu_item_delete_item:
                AddressLab.get(getActivity()).deleteAddress(address);
                adapter.notifyDataSetChanged();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        AddressLab.get(getActivity()).saveAddress();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AddressAdapter) mListView.getAdapter()).notifyDataSetChanged();
        mAddresses = AddressLab.get(getActivity()).getmAddressBook();
    }

    public Location getLastKnownLocation() {

        if (ActivityCompat.checkSelfPermission(getActivity()
            , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(
            mGoogleApiClient);

        return location;
    }

    public void getTravelInfoSingleAddress(final Address address) {

        try {
            URL googlePlaces =
                new URL("https://maps.googleapis.com/maps/api/distancematrix/json?" +
                    "origins=" + mSetUpCommuteInfoForAddresses.
                    getAddressOfCurrentLocation(getLastKnownLocation()) +
                    "&destinations=" + URLEncoder.encode(AddressLab.createSingleAddressUrl(address),
                    "UTF-8").replaceAll("\\+", "%20") +
                    "&units=imperial&types=geocode&language=en&sensor=true&key=" +
                    //Api key
                    getString(R.string.api_key_url));

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

                            final List<String> destinationTimes =
                                SetUpCommuteInfoForAddresses.getDurationOfCommutes(jsonData, getActivity());
                            final List<String> destinationDistances =
                                SetUpCommuteInfoForAddresses.getDistanceOfCommutes(jsonData, getActivity());

                            PopUpMapDialog popUpMapDialog = PopUpMapDialog.newInstance(
                                address.getFullAddress(),
                                destinationDistances.get(0),
                                destinationTimes.get(0), address.getTitle(),
                                address.getmId().toString());

                            popUpMapDialog.show(getFragmentManager(), "Pop up map");


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
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
