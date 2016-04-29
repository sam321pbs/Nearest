package com.example.sammengistu.nearest.activities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import com.example.sammengistu.nearest.CheckNetwork;
import com.example.sammengistu.nearest.R;
import com.example.sammengistu.nearest.SetUpCommuteInfoForAddresses;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


public class AddressesListActivity extends AppCompatActivity implements
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private SetUpCommuteInfoForAddresses mSetUpCommuteInfoForAddresses;
    private GoogleApiClient mGoogleApiClient;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses);

        mGoogleApiClient = new GoogleApiClient.Builder(AddressesListActivity.this)
            .addConnectionCallbacks(AddressesListActivity.this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();

        mGoogleApiClient.connect();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setBackgroundColor(getResources().getColor(R.color.theme_primary));

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageView mapIcon = (ImageView) findViewById(R.id.show_map_icon);
        mapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AsyncTaskLoad().execute();
            }
        });

    }
    public Location getLastKnownLocation() {

        if (ActivityCompat.checkSelfPermission(AddressesListActivity.this
            , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(AddressesListActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    public class AsyncTaskLoad extends android.os.AsyncTask<Void, Void, Void>{
        ProgressDialog progress = new ProgressDialog(AddressesListActivity.this);

        @Override
        protected void onPreExecute() {

            progress.setTitle("Loading");
            progress.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // To dismiss the dialog
                progress.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (CheckNetwork.networkConnection(AddressesListActivity.this)) {

                mSetUpCommuteInfoForAddresses = new SetUpCommuteInfoForAddresses(AddressesListActivity.this,
                    getLastKnownLocation());

                mSetUpCommuteInfoForAddresses.setUpTravelInfo(AddressesListActivity.this, MapsActivity.class);
            } else {
                Toast.makeText(AddressesListActivity.this, "Please Check Network Connection",
                    Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }
}
