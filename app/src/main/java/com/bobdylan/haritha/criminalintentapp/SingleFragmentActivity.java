package com.bobdylan.haritha.criminalintentapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


//we dont need to add abstract activity classes to the manifest, duh.
public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract  Fragment newFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fc=fm.findFragmentById(R.id.fragment_container);       //the framelayout is inflated into a Fragment
        if(fc==null){                   //fc can be not-null if onCreate is called due to orientation change-
            fc = newFragment();   //previous fragments are saved on rotation by fragmentmanager, so fc can !=null too
            fm.beginTransaction().add(R.id.fragment_container,fc).commit(); //add this fragment 'fc' to this framelayout

        }
    }
}
