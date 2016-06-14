package com.example.sammengistu.nearest.adapters;

import com.example.sammengistu.nearest.R;
import com.example.sammengistu.nearest.models.Address;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CardViewMapInfoAdapter extends RecyclerView.Adapter
    <CardViewMapInfoAdapter.CommuteInfoViewHolder>
{

    private final String TAG = "cardview55";
    private List<Address> mAddressList;
    private Activity mActivity;

    public CardViewMapInfoAdapter(List<Address> addressList, Activity activity) {
        mAddressList = addressList;
        mActivity = activity;
        Log.i(TAG, "Size = " + mAddressList.size());
    }

    @Override
    public CommuteInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v = inflater.inflate(R.layout.location_info_cardview, parent, false);
        return new CommuteInfoViewHolder(v);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(CommuteInfoViewHolder holder, int position) {

        Address currentAddress = mAddressList.get(position);

        Log.i("cardview55", currentAddress.getFullAddress());

        if (position == 0){
            holder.mBackground.setBackgroundColor(mActivity.getResources()
                .getColor(R.color.highlight_color));
        }

        holder.mCommuteAddressTextView.setText(currentAddress.getFullAddress());
        holder.mCommuteDistanceTextView.setText(currentAddress.getDistance());
        holder.mCommuteETATextView.setText(currentAddress.getCommuteTime());
    }

    @Override
    public int getItemCount() {
        return mAddressList.size();
    }

    public static class CommuteInfoViewHolder extends RecyclerView.ViewHolder {

        public TextView mCommuteAddressTextView;
        public TextView mCommuteDistanceTextView;
        public TextView mCommuteETATextView;
        public TextView mBackground;

        public CommuteInfoViewHolder(View itemView) {
            super(itemView);

            mCommuteAddressTextView = (TextView) itemView.findViewById(R.id.cardview_address);
            mCommuteDistanceTextView = (TextView) itemView.findViewById(R.id.pop_up_distance_view_);
            mCommuteETATextView = (TextView) itemView.findViewById(R.id.pop_up_eta_view);
            mBackground = (TextView) itemView.findViewById(R.id.text_view_background_id);

        }
    }
}