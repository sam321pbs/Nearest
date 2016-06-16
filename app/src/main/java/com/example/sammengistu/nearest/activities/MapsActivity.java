package com.example.sammengistu.nearest.activities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.example.sammengistu.nearest.AddressLab;
import com.example.sammengistu.nearest.R;
import com.example.sammengistu.nearest.SetUpCommuteInfoForAddresses;
import com.example.sammengistu.nearest.SortAddress;
import com.example.sammengistu.nearest.adapters.CardViewMapInfoAdapter;
import com.example.sammengistu.nearest.dialogs.SortDialog;
import com.example.sammengistu.nearest.models.Address;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    SortDialog.SortListener {

    private static final String TAG = "MapActivity";
    private static final int GET_SORT_TYPE = 5;
    private static final int GET_TITLE = 2;

    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private final String ANIMATION_TYPE = "translationY";
    private final int ANIMATION_SHOW_LIST = 0;

    private ObjectAnimator mObjectAnimatorEntireListSection;
    private GoogleMap mMap;
    private Address mSelectedAddress;
    private List<android.location.Address> mGeocodeMatches;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerViewCommuteInfo;
    private FloatingActionButton mFloatingActionButtonAddAddress;
    private TextView mShowHideTextView;
    private FrameLayout mLocationsDetailListView;
    private int mViewListHeight;
    public static int sFirstCardViewHeight;
    private FrameLayout mMiniToolbarSize;
    private int mMiniToolbarHight;
    private ObjectAnimator mObjectAnimatorFAB;
    private Toolbar mMyToolbar;
    private ImageView mRefreshImageView;
    private ObjectAnimator mObjectAnimatorRefresh;
    private boolean mShowEntireList;

    private View.OnClickListener mFABOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {

                AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build();

                Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                        .setFilter(typeFilter)
                        .build(MapsActivity.this);

                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

            } catch (GooglePlayServicesRepairableException e) {
                // TODO: Handle the error.
                Log.i(TAG, e.getMessage());
            } catch (GooglePlayServicesNotAvailableException e) {
                // TODO: Handle the error.
                Log.i(TAG, e.getMessage());
            }
        }
    };

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        initializeViews();

        mGeocodeMatches = null;
        mShowEntireList = false;
        mMap.setMyLocationEnabled(true);

        mShowHideTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowEntireList = !mShowEntireList;
                animateMovedList(mShowEntireList);
            }
        });

        mObjectAnimatorRefresh = ObjectAnimator.ofFloat(mRefreshImageView, "rotation",
            720);
        mObjectAnimatorRefresh.setDuration(1000);
        mFloatingActionButtonAddAddress.setOnClickListener(mFABOnClickListener);
        mMyToolbar.setBackgroundColor(getResources().getColor(R.color.theme_primary));
        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpCommuteDetails();
                mRecyclerViewCommuteInfo.getAdapter().notifyDataSetChanged();

                mObjectAnimatorRefresh.start();
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (mGoogleApiClient == null) {
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        }

        mLayoutManager = new LinearLayoutManager(this);

        if (mRecyclerViewCommuteInfo != null) {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerViewCommuteInfo.setHasFixedSize(true);
            mRecyclerViewCommuteInfo.setLayoutManager(mLayoutManager);
        }

        mRecyclerViewCommuteInfo.setAdapter(new CardViewMapInfoAdapter(this, mMap));

        setUpToolbar();
        setUpLayoutHeights();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_sort_by:
                Log.i(TAG, "sort");
                SortDialog sortDialog = new SortDialog();
                sortDialog.show(getSupportFragmentManager(), "Sort dialog");
                break;

            case R.id.action_help:
                //TODO: create help dialog
                break;

            default:
                break;
        }
        return true;
    }

    private void initializeViews() {
        mMyToolbar = (Toolbar) findViewById(R.id.toolbar_maps_activity);
        mRefreshImageView = (ImageView) findViewById(R.id.refresh_icon);
        mLocationsDetailListView = (FrameLayout) findViewById(R.id.locations_commute_info_view);
        mMiniToolbarSize = (FrameLayout) findViewById(R.id.mini_tool_bar_size);

        mShowHideTextView = (TextView) findViewById(R.id.show_hide_list);
        mFloatingActionButtonAddAddress = (FloatingActionButton) findViewById(R.id.fab_add_maps_activity);
        mRecyclerViewCommuteInfo = (RecyclerView) findViewById(R.id.map_info_recycler_view);
    }

    private void setUpLayoutHeights() {

        ViewTreeObserver viewTreeObserver = mLocationsDetailListView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mLocationsDetailListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mViewListHeight = mLocationsDetailListView.getHeight();
                        animateMovedList(mShowEntireList);

                        Log.i(TAG, "View hight = " + mViewListHeight);
                    }
                });
        }
    }

    private void animateMovedList(boolean showList) {
        Log.i(TAG, "Cardview size = " + sFirstCardViewHeight);
        if (showList) {
            mObjectAnimatorEntireListSection = ObjectAnimator.ofFloat(mLocationsDetailListView,
                ANIMATION_TYPE, ANIMATION_SHOW_LIST);

            mObjectAnimatorFAB = ObjectAnimator.ofFloat(mFloatingActionButtonAddAddress,
                ANIMATION_TYPE, ANIMATION_SHOW_LIST);

        } else {

            mObjectAnimatorEntireListSection = ObjectAnimator.ofFloat(mLocationsDetailListView,
                ANIMATION_TYPE, mViewListHeight - (sFirstCardViewHeight + 110));

            mObjectAnimatorFAB = ObjectAnimator.ofFloat(mFloatingActionButtonAddAddress,
                ANIMATION_TYPE, mViewListHeight - (sFirstCardViewHeight + 165));
        }
        setUpLayoutParamsForListView(showList);

        mObjectAnimatorEntireListSection.start();
        mObjectAnimatorFAB.start();
    }

    private void setUpLayoutParamsForListView(boolean showList) {
        FrameLayout.LayoutParams params;
        if (showList) {
            params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            );
        } else {
            params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                sFirstCardViewHeight == 0 ?
                    300 : sFirstCardViewHeight + 100
            );
        }

        mLocationsDetailListView.setLayoutParams(params);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only
     * ever
     * call {@link #addMarkerToMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    @SuppressWarnings("deprecation")
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.location_map))
                .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                addMarkerToMap();
            }
        }
    }

    private void addMarkerToMap() {

        for (Address addressToDisplay : AddressLab.get(getApplicationContext()).getmAddressBook()) {
            Log.i(TAG, "Add to map" + addressToDisplay.getFullAddress());

            double latitude = 0;
            double longitude = 0;
            try {
                mGeocodeMatches =
                    new Geocoder(this).getFromLocationName(
                        addressToDisplay.getFullAddress(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (!mGeocodeMatches.isEmpty()) {
                    latitude = mGeocodeMatches.get(0).getLatitude();
                    longitude = mGeocodeMatches.get(0).getLongitude();
                    addressToDisplay.setLatitude(latitude);
                    addressToDisplay.setLongitude(longitude);
                }
                mMap.addMarker(
                    new MarkerOptions().position(new LatLng(latitude, longitude))
                        .visible(true)
                        .title(addressToDisplay.getTitle())
                );
            } catch (NullPointerException e) {

            }

        }

    }

    private void zoomOnMyLocation() {

        double longitude = mLastLocation.getLongitude();
        double latitude = mLastLocation.getLatitude();
        LatLng currentItemOnList = new LatLng(latitude, longitude);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentItemOnList, 15));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {

            if (resultCode == this.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                Log.i(TAG, "Place: " + place.getAddress());

                mSelectedAddress = null;

                mSelectedAddress = new Address(place.getAddress().toString());

                AddressLab.get(this).addAddress(mSelectedAddress);

                mRecyclerViewCommuteInfo.setAdapter(new CardViewMapInfoAdapter(
                    this, mMap));

                setUpCommuteDetails();

                animateMovedList(true);

                addMarkerToMap();
//
//                TypeTitleDialog typeTitleDialog = new TypeTitleDialog();
//                typeTitleDialog.setDialogResult(new TypeTitleDialog.OnMyDialogResult() {
//                    @Override
//                    public void finish(String result) {
//                        mSelectedAddress.setTitle(result);
//
//                        AddressLab.get(MapsActivity.this).addAddress(mSelectedAddress);
//
//                        AddressLab.get(MapsActivity.this).saveAddress();
//                    }
//                });

//                typeTitleDialog.show(this.getSupportFragmentManager(),
//                    TypeTitleDialog.TYPED_TITLE_STRING);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else {
                // The user canceled the operation.
            }
        }
    }

    private void setUpCommuteDetails() {
        SetUpCommuteInfoForAddresses setUpCommuteInfoForAddresses =
            new SetUpCommuteInfoForAddresses(this, getLastKnownLocation(),
                mRecyclerViewCommuteInfo);

        setUpCommuteInfoForAddresses.setUpTravelInfo(AddressLab.get(this).getmAddressBook(),
            mMap);
    }

    public Location getLastKnownLocation() {

        if (ActivityCompat.checkSelfPermission(this
            , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
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

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AddressLab.get(this).saveAddress();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();

        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connecting to api");

        if (ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
            mGoogleApiClient);

        zoomOnMyLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMyToolbar.inflateMenu(R.menu.menu_map);
        mMyToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.i(TAG, "Item click menu");
                return onOptionsItemSelected(item);

            }
        });
        return true;
    }

    private void setUpToolbar() {

        setSupportActionBar(mMyToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, boolean distance) {

        AddressLab.sAddressBook =
            SortAddress.sortAddresses(AddressLab.get(this).getmAddressBook(), distance);

        Log.i(TAG, "distance = " + distance);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
