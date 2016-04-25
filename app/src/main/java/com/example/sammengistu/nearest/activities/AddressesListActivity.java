package com.example.sammengistu.nearest.activities;

import com.example.sammengistu.nearest.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


public class AddressesListActivity extends AppCompatActivity {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses);

        CollapsingToolbarLayout toolbarLayout = ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolBar));
        toolbarLayout.setTitle(getResources().getString(R.string.app_name));
//        toolbarLayout.setBackgroundColor(getResources().getColor(R.color.theme_primary));

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setBackgroundColor(getResources().getColor(R.color.theme_primary));
        setSupportActionBar(myToolbar);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
