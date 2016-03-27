package com.example.sammengistu.nearest.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.example.sammengistu.nearest.AddressFragment;
import com.example.sammengistu.nearest.R;

import java.util.UUID;


public class AddressActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle onSavedInstanceState){
        super.onCreate(onSavedInstanceState);
        setContentView(R.layout.activity_fragment);

        UUID id = (UUID)getIntent()
                .getSerializableExtra(AddressFragment.ADDRESS_ID);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null){
            fragment = AddressFragment.newInstance(id);
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
