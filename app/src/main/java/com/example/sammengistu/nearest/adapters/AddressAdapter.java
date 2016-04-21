package com.example.sammengistu.nearest.adapters;

import com.example.sammengistu.nearest.models.Address;
import com.example.sammengistu.nearest.R;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

public class AddressAdapter extends ArrayAdapter<Address> {

    private Activity mActivity;

    public AddressAdapter(Activity appContext, List<Address> addresses) {
        super(appContext, 0, addresses);
        mActivity = appContext;
    }

    @Override
    public View getView(int postion, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mActivity.getLayoutInflater()
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
        streetTextView.setText(a.getFullAddress());
        showOnMap.setChecked(a.isShowOnMap());

        return convertView;
    }
}
