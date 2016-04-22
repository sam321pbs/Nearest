package com.example.sammengistu.nearest.dialogs;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.example.sammengistu.nearest.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class PopUpMapDialog extends DialogFragment {

    private static final String ADDRESS_TO_SHOW_ON_MAP = "Address for map";
    private static final String TAG = "Pop Up";
    private static final String ADDRESS_TITLE = "Address title";
    private static final String ADDRESS_DISTANCE = "distance";
    private static final String ADDRESS_ETA = "eta";
    private GoogleMap mGoogleMap;

    MapView mMapView;
    List<android.location.Address> geocodeMatches;

    private TextView mETATextView;
    private TextView mDistanceTextView;
    private String address;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = getActivity().
            getLayoutInflater()
            .inflate(R.layout.fragment_location_info, null);

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        address = getArguments().getString(ADDRESS_TO_SHOW_ON_MAP);

        TextView title = ((TextView) v.findViewById(R.id.fragment_location_pop_title_of_location));
        title.setText(getArguments().get(ADDRESS_TITLE).toString());

        mETATextView = (TextView) v.findViewById(R.id.pop_up_eta_view);
        mDistanceTextView = (TextView) v.findViewById(R.id.pop_up_distance_view_);

        mETATextView.setText(getArguments().getString(ADDRESS_ETA));
        mDistanceTextView.setText(getArguments().getString(ADDRESS_DISTANCE));

        geocodeMatches = null;

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mGoogleMap = mMapView.getMap();

        showAddressOnMap(address);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setPositiveButton("Take me there", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startGoogleMaps(address);

            }
        });
        builder.setNeutralButton("Cancel", null);

        AlertDialog alertDialog = builder.create();

        alertDialog.show();

        return alertDialog;
    }

    private void showAddressOnMap(String address) {
        double latitude;
        double longitude;

        try {
            geocodeMatches =
                new Geocoder(getActivity()).getFromLocationName(
                    address, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!geocodeMatches.isEmpty()) {
            latitude = geocodeMatches.get(0).getLatitude();
            longitude = geocodeMatches.get(0).getLongitude();

            // Changing marker icon
            mGoogleMap.addMarker(
                new MarkerOptions().position(new LatLng(latitude, longitude))
                    .visible(true));

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
        }
    }

    private void startGoogleMaps(String address) {

        Uri gmmIntentUri = Uri.parse("geo:0,0?q= " + address);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(getActivity(), "Please download google maps",
                Toast.LENGTH_SHORT).show();
        }
    }

    public static PopUpMapDialog newInstance(String address, String distance, String eta, String addressTitle) {
        Bundle bundle = new Bundle();
        bundle.putString(ADDRESS_TO_SHOW_ON_MAP, address);
        bundle.putString(ADDRESS_TITLE, addressTitle);
        bundle.putString(ADDRESS_DISTANCE, distance);
        bundle.putString(ADDRESS_ETA, eta);

        PopUpMapDialog popUpMapDialog = new PopUpMapDialog();
        popUpMapDialog.setArguments(bundle);

        return popUpMapDialog;
    }
}
