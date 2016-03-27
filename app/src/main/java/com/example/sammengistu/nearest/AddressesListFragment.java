package com.example.sammengistu.nearest;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sammengistu.nearest.activities.AddressActivity;
import com.example.sammengistu.nearest.activities.MapsActivity;

import java.util.ArrayList;

public class AddressesListFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final String TAG = "AddressListFragment";

    private Button mMapButton;
    private ArrayList<Address> mAddresses;

    private AbsListView mListView;
    private ListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mAddresses = AddressLab.get(getActivity()).getmAddressBook();

        mAdapter = new AddressAdapter(mAddresses);

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
                if (networkConnection()) {
                    SetUpCommuteInfoForAddresses setUpCommuteInfoForAddresses = new SetUpCommuteInfoForAddresses(getActivity());
                    setUpCommuteInfoForAddresses.setUpTravelInfo();
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

    private boolean networkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Address address = ((AddressAdapter) mListView.getAdapter()).getItem(position);

        Log.i(TAG, address.isShowOnMap() + "");

        Intent i = new Intent(getActivity(), AddressActivity.class);
        i.putExtra(AddressFragment.ADDRESS_ID, address.getmId());
        i.putExtra(AddressFragment.ADDRESS_SHOW_ON_MAP, address.isShowOnMap());
        startActivity(i);
    }

    private class AddressAdapter extends ArrayAdapter<Address> {

        public AddressAdapter(ArrayList<Address> addresses) {
            super(getActivity(), 0, addresses);
        }

        @Override
        public View getView(int postion, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_address, null);
            }

            final Address a = getItem(postion);

            TextView addressTitleTextView = (TextView)
                    convertView.findViewById(R.id.list_item_title_of_address);
            TextView streetTextView = (TextView)
                    convertView.findViewById(R.id.list_item_streetTextView);
            CheckBox showOnMap = (CheckBox)
                    convertView.findViewById(R.id.list_item_show_on_map);

            showOnMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    a.setShowOnMap(isChecked);
                }
            });

            addressTitleTextView.setText(a.getTitle());
            streetTextView.setText(a.getStreet());
            showOnMap.setChecked(a.isShowOnMap());

            return convertView;
        }
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
                Address address = new Address();

                AddressLab.get(getActivity()).addAddress(address);
                Intent i = new Intent(getActivity(), AddressActivity.class);
                i.putExtra(AddressFragment.ADDRESS_ID, address.getmId());
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
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
