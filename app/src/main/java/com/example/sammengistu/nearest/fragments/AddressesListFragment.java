package com.example.sammengistu.nearest.fragments;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import com.example.sammengistu.nearest.AddressLab;
import com.example.sammengistu.nearest.CheckNetwork;
import com.example.sammengistu.nearest.R;
import com.example.sammengistu.nearest.activities.MapsActivity;
import com.example.sammengistu.nearest.adapters.AddressAdapter;
import com.example.sammengistu.nearest.dialogs.PopUpMapDialog;
import com.example.sammengistu.nearest.dialogs.TypeTitleDialog;
import com.example.sammengistu.nearest.models.Address;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.util.List;

public class AddressesListFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final String TAG = "AddressListFragment";
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int GET_TITLE = 2;

    private Button mMapButton;
    private List<Address> mAddresses;

    private AbsListView mListView;
    private ListAdapter mAdapter;
    private Address mSelectedAddress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mAddresses = AddressLab.get(getActivity()).getmAddressBook();

        mAdapter = new AddressAdapter(getActivity(), mAddresses);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_addresses, container, false);

        // Set the adapter
        mListView = (AbsListView) v.findViewById(android.R.id.list);

        mListView.setAdapter(mAdapter);
        registerForContextMenu(mListView);

        mListView.setOnItemClickListener(this);

        mMapButton = (Button) v.findViewById(R.id.show_map_button);

        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckNetwork.networkConnection(getActivity())) {

                    Intent i = new Intent(getActivity(), MapsActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getActivity(), "Please Check Network Connection",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Address address = ((AddressAdapter) mListView.getAdapter()).getItem(position);

        Log.i(TAG, address.isShowOnMap() + "");

//        Intent i = new Intent(getActivity(), AddressActivity.class);
//        i.putExtra(AddressFragment.ADDRESS_ID, address.getmId());
//        i.putExtra(AddressFragment.ADDRESS_SHOW_ON_MAP, address.isShowOnMap());
//        startActivity(i);

        PopUpMapDialog popUpMapDialog = PopUpMapDialog.newInstance(address.getFullAddress());
        popUpMapDialog.show(getFragmentManager(), "Pop up map");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_addresses, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_Address:

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
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
}
