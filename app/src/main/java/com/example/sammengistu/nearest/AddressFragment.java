package com.example.sammengistu.nearest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.util.UUID;


/**
 * A placeholder fragment containing a simple view.
 */
public class AddressFragment extends Fragment {

    private static final String TAG = "AddressFragment";
    public static final String ADDRESS_ID = "Address ID";
    public static final String ADDRESS_SHOW_ON_MAP = "Address Show On Map";

    private Address mAddress;

    private TextView mTitleTextView;
    private TextView mFullAddress;
    private EditText mTitleEditText;
    private EditText mStreet;
    private EditText mCity;
    private EditText mState;
    private EditText mZipCode;
    private CheckBox mShowOnMap;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

            UUID addressId = (UUID)getArguments().getSerializable(ADDRESS_ID);
            mAddress = AddressLab.get(getActivity()).getAddress(addressId);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_address, container, false);

        mTitleTextView = (TextView)v.findViewById(R.id.address_title_text_view);
        mTitleTextView.setText(mAddress.getTitle() == null ? "New Address" : mAddress.getTitle());

        mTitleEditText = (EditText)v.findViewById(R.id.address_title_edit_text);
        if (mAddress.getTitle() == null){
            mTitleEditText.setHint("Enter title here");
        } else {
            mTitleEditText.setText(mAddress.getTitle());
        }

        mStreet = (EditText) v.findViewById(R.id.street_address_edit_text);
        if (mAddress.getStreet() == null){
            mStreet.setHint("Enter street here");
        } else {
            mStreet.setText(mAddress.getStreet());
        }

        mCity = (EditText) v.findViewById(R.id.city_name_edit_text);
        if (mAddress.getCity() == null){
            mCity.setHint("Enter city here");
        } else {
            mCity.setText(mAddress.getCity());
        }
        mState = (EditText) v.findViewById(R.id.state_edit_text);
        if (mAddress.getState() == null){
            mState.setHint("Enter state here");
        } else {
            mState.setText(mAddress.getState());
        }
        mZipCode = (EditText) v.findViewById(R.id.zip_code_edit_text);
        if (mAddress.getZipCode() == 0){
            mZipCode.setHint("Enter zip code here");
        } else {
            mZipCode.setText("" + mAddress.getZipCode());
        }
        mShowOnMap = (CheckBox) v.findViewById(R.id.show_on_map_check_box);
        mShowOnMap.setChecked(mAddress.isShowOnMap());

        mFullAddress = (TextView) v.findViewById(R.id.full_address_text_view);
        mFullAddress.setText(mAddress.getFullAddress());

        mTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mAddress.setTitle(s.toString());
                mTitleTextView.setText(s.toString());
            }
        });

        mStreet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mAddress.setStreet(s.toString());
            }
        });

        mCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mAddress.setCity(s.toString());
            }
        });
        mState.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mAddress.setState(s.toString());
            }
        });
        mZipCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            int zipCode;
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    zipCode = Integer.parseInt(s.toString());
                    mAddress.setZipCode(zipCode);
                } catch (Exception e){
                   // Toast.makeText(getActivity(),
                    //        "Zip Code needs to be a five digit number", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e + "");
                }

            }
        });

        mShowOnMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAddress.setShowOnMap(isChecked);
            }
        });
        return v;
    }

    public static AddressFragment newInstance (UUID id){
        Bundle args = new Bundle();
        args.putSerializable(ADDRESS_ID, id);

        AddressFragment fragment = new AddressFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        AddressLab.get(getActivity()).saveAddress();
    }
}
