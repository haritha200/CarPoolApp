package com.bobdylan.haritha.carpoolapp;

import android.support.v4.app.Fragment;

public class AllTripsActivity extends SingleFragmentActivity {

    @Override
    protected Fragment newFragment() {
        return new TripListFragment();
    }
}
