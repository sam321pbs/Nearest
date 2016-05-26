package com.example.sammengistu.nearest.activities;

import com.example.sammengistu.nearest.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * This class inflates the fragment container with the use of fragmentTransaction
 */
public abstract class SingleFragmentActivity extends FragmentActivity {

    public abstract Fragment createFragment();

    //By default call this but gets override to use the appropriate view
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    private Fragment mFragment;

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        setContentView(getLayoutResId());

        FragmentManager fm = getSupportFragmentManager();

        mFragment = fm.findFragmentById(R.id.fragment_container);


        if (onSavedInstanceState != null) {
            //Restore the fragment's instance
//            mFragment = getSupportFragmentManager().getFragment(onSavedInstanceState,
//                MovieAppConstants.FRAGMENT_STATE);

        } else {
            mFragment = createFragment();
            fm.beginTransaction()
                .add(R.id.fragment_container, mFragment)
                .commit();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
//        getSupportFragmentManager().putFragment(outState,
//            MovieAppConstants.FRAGMENT_STATE, mFragment);
    }
}