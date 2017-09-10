package com.bobdylan.haritha.criminalintentapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {
    private static final String EXTRA_CRIME_ID = "extra crime id" ;
    private List<Crime> mCrimes;
    private ViewPager viewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        mCrimes= CrimeLab.get(this).getCrimes();
        viewPager= (ViewPager) findViewById(R.id.crime_pager);
        UUID crimeId= (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);  //retrieve id of clicked crime from extra


        FragmentManager fm= getSupportFragmentManager();

        viewPager.setAdapter(new FragmentStatePagerAdapter(fm) {    //viewpager needs a pageradapter to manage views
            @Override
            public Fragment getItem(int position) {
                Crime crime= mCrimes.get(position);//crime in array position 'position'
                Log.i("CRIMEPAGERACT: ", ""+crime.isMatched());
                if(!crime.isMatched())
                    return CrimeFragment.newInstance(crime.getId());    //crime with UUID 'getid'
                else
                    return MatchedFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }

        });


        for (int i=0;i<mCrimes.size();i++){
            if(mCrimes.get(i).getId().equals(crimeId)){
                viewPager.setCurrentItem(i); break;     //find clicked crime's index in the arraylist
                //set this index as the current item to view in viewpager
            }
        }
    }


    //1. create intent in this activity.
    public static Intent newIntent(Context activity, UUID id) {

        Intent intent= new Intent(activity, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, id);
        return intent;
    }

}
