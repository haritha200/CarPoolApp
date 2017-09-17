package com.bobdylan.haritha.criminalintentapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by haritha on 17/9/17.
 */

public class UserDefaultsActivity extends SingleFragmentActivity {
    @Override
    protected Fragment newFragment() {
        return new UserDefaultsFragment();
    }

    public static Intent newIntent(Context activity){
        return new Intent (activity, UserDefaultsActivity.class);
    }

}
