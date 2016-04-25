package com.example.sammengistu.nearest.adapters;

import com.example.sammengistu.nearest.models.Address;
import com.example.sammengistu.nearest.R;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MapListAdapter extends ArrayAdapter<Address> {

    private Activity mActivity;

    public MapListAdapter(Activity appContext, List<Address> addresses) {
        super(appContext, 0, addresses);
        mActivity = appContext;
    }

    @Override
    public View getView(int postion, View convertView, ViewGroup parent) {
        //If a view wasent given to us
        if (convertView == null) {
            convertView = mActivity.getLayoutInflater()
                .inflate(R.layout.map_list_item_address, null);
        }

        Address addressToShowOnMap = getItem(postion);

        TextView addressTitleTextView = (TextView)
            convertView.findViewById(R.id.list_item_title_of_address);
        TextView fullAddressTextView = (TextView)
            convertView.findViewById(R.id.map_list_item_full_address);
        TextView commuteTime = (TextView)
            convertView.findViewById(R.id.list_item_duration_details);
        TextView commuteDistance = (TextView)
            convertView.findViewById(R.id.list_item_distance_details);

        if (addressToShowOnMap.getDuration() != null) {
            commuteTime.setText(addressToShowOnMap.getDuration());
        } else {
            commuteTime.setText("0 min");
        }

        if (addressToShowOnMap.getDistance() != null) {
            commuteDistance.setText(addressToShowOnMap.getDistance());
        } else {
            commuteDistance.setText("0 miles");
        }

        if (addressToShowOnMap.getFullAddress() != null){
            fullAddressTextView.setText(addressToShowOnMap.getFullAddress());
        }

        addressTitleTextView.setText(addressToShowOnMap.getTitle());

        return convertView;
    }
}
