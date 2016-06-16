package com.example.sammengistu.nearest.adapters;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import com.example.sammengistu.nearest.AddressLab;
import com.example.sammengistu.nearest.R;
import com.example.sammengistu.nearest.activities.MapsActivity;
import com.example.sammengistu.nearest.models.Address;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.List;

public class CardViewMapInfoAdapter extends RecyclerView.Adapter
    <CardViewMapInfoAdapter.CommuteInfoViewHolder>
{

    private final String TAG = "cardview55";
    private List<Address> mAddressList;
    private Activity mActivity;
    private GoogleMap mGoogleMap;

    public CardViewMapInfoAdapter(Activity activity, GoogleMap map) {
        mAddressList = AddressLab.get(activity).getmAddressBook();
        mActivity = activity;
        mGoogleMap = map;
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
    public void onBindViewHolder(final CommuteInfoViewHolder holder, final int position) {

        Address currentAddress = mAddressList.get(position);

        Log.i("cardview55", currentAddress.getFullAddress());

        if (position == 0){
            holder.mBackground.setBackgroundColor(mActivity.getResources()
                .getColor(R.color.highlight_color));
        } else {
            holder.mBackground.setBackgroundColor(mActivity.getResources()
                .getColor(R.color.white_color));

        }

        holder.mCommuteAddressTextView.setText(currentAddress.getFullAddress());
        holder.mCommuteDistanceTextView.setText(currentAddress.getDistance());
        holder.mCommuteETATextView.setText(currentAddress.getCommuteTime());
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double longitude = mAddressList.get(position).getLongitude();
                double latitude = mAddressList.get(position).getLatitude();
                LatLng currentItemOnList = new LatLng(latitude, longitude);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentItemOnList, 15));
            }
        });

        holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(mActivity)
                    .setTitle(mActivity.getString(R.string.are_you_sure_you_want_to_delete))
                    .setMessage(holder.mCommuteAddressTextView.getText().toString())
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AddressLab.sAddressBook.remove(position);
                            CardViewMapInfoAdapter.this.notifyDataSetChanged();
                        }
                    })
                    .show();
                return false;
            }
        });

        ViewTreeObserver viewTreeObserver = holder.mCardView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        holder.mCardView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        MapsActivity.sFirstCardViewHeight = holder.mCardView.getHeight();

                    }
                });
        }
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
        public CardView mCardView;

        public CommuteInfoViewHolder(View itemView) {
            super(itemView);

            mCommuteAddressTextView = (TextView) itemView.findViewById(R.id.cardview_address);
            mCommuteDistanceTextView = (TextView) itemView.findViewById(R.id.pop_up_distance_view_);
            mCommuteETATextView = (TextView) itemView.findViewById(R.id.pop_up_eta_view);
            mBackground = (TextView) itemView.findViewById(R.id.text_view_background_id);
            mCardView = (CardView) itemView.findViewById(R.id.card_view);

        }
    }
}
