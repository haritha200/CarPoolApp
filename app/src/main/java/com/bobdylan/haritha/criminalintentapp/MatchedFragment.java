package com.bobdylan.haritha.criminalintentapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by haritha on 10/9/17.
 */

public class MatchedFragment extends Fragment {
    private static final String ARG_CRIME_ID = "matched crime id" ;
    private Crime mCrime;
    private TextView p_name, p_flat, p_phone, p_date, p_time, p_pickUp;
    public static Fragment newInstance(UUID crimeid) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeid);
        MatchedFragment fragment = new MatchedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.delete_crime:{
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                getActivity().finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate( R.menu.fragment_crime,menu);
    }

    //initalise non-view member variables in onCreate()
    //6. extract args from frag bundle
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID cid=(UUID)getArguments().getSerializable(ARG_CRIME_ID);
        mCrime=CrimeLab.get(getActivity()).getCrime(cid);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v=inflater.inflate(R.layout.fragment_matched_details, container,false);        //container is this layout's parent
        p_name= (TextView)v.findViewById(R.id.p_name);
        p_flat= (TextView)v.findViewById(R.id.p_flat);
        p_date= (TextView)v.findViewById(R.id.p_date);
        p_phone= (TextView)v.findViewById(R.id.p_phone);
        p_pickUp= (TextView)v.findViewById(R.id.p_pickup);
        p_time= (TextView)v.findViewById(R.id.p_time);

        Crime matchedTrip = CrimeLab.get(getContext()).getMatchedTrip(mCrime);
        p_name.setText(matchedTrip.getTitle());
        p_flat.setText(matchedTrip.getFlat());
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("hh:mm a");
        dateFormat1.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
        p_time.setText(dateFormat1.format(matchedTrip.getTime()));
        dateFormat1 = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        p_date.setText(dateFormat1.format(matchedTrip.getDate()));
        p_phone.setText(matchedTrip.getPhone());
        p_pickUp.setText(matchedTrip.getPickUp());
        return v;
    }
}
