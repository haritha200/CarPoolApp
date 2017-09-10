package com.bobdylan.haritha.criminalintentapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment; //add support.v4 library dependency through the project structure as well!!!
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by haritha on 31/12/16.
 */

public class CrimeFragment extends Fragment {
   // DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private static final String ARG_CRIME_ID =  "crime id arg";
    public static final int REQUEST_CODE_DATE = 0, REQUEST_CODE_TIME=1 ;

    private Crime mCrime;       //since this fragment needs to present a crime
    private EditText mTitle, mFlat, mPhone;
    private RadioGroup mPickUp;
    private RadioButton mSJW, mAirport;
    private CheckBox mSolved;
    private Button mDateButton, mTimeButton;
    private boolean isDelete = false;

    //TO CREATE THE FRAGMENT AND INSERT FRAG-ARGS. SO THAT THE CRIMEACTIVITY CAN CALL IT TO PLACE THIS FRAGMENT IN IT'S VIEW
    //5. bind args to this fragment
    public static CrimeFragment newInstance(UUID crimeid) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeid);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!isDelete)
            CrimeLab.get(getActivity()).updateCrime(mCrime);
        //  CrimeLab crimelab = CrimeLab.get(getContext());
        /// mDatabase.child("users").child(crimelab.getUserID().toString()).child(mCrime.getId().toString()).setValue(mCrime);
        //  Log.i("USERID ", "VALUE PUSHED TO FIREBASE");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.delete_crime:{
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                isDelete=true;
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

        View v=inflater.inflate(R.layout.fragment_crime, container,false);        //container is this layout's parent
        mTitle=(EditText) v.findViewById(R.id.crime_title);
        mTitle.setText(mCrime.getTitle());
        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mFlat=(EditText)v.findViewById(R.id.flat_number);
        mFlat.setText(mCrime.getFlat());
        mFlat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setFlat(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mPhone=(EditText)v.findViewById(R.id.phone_no);
        mPhone.setText(mCrime.getPhone());
        mPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setPhone(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mSolved=(CheckBox) v.findViewById(R.id.solved);
        mSolved.setChecked(mCrime.isSolved());

        mSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mPickUp= (RadioGroup)v.findViewById(R.id.pickup_radiogroup);
        mAirport=(RadioButton)v.findViewById(R.id.airport_radio);
        mSJW = (RadioButton)v.findViewById(R.id.sjw_radio);

        if(mCrime.getPickUp()!=null && mCrime.getPickUp().equals("Airport")){
            mAirport.setChecked(true);
            mCrime.setPickUp("Airport");
        }else{
            mSJW.setChecked(true);
            mCrime.setPickUp("St. John's Wood Apartments");
        }

        mPickUp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.sjw_radio){
                    Toast.makeText(getContext(), "Have a happy journey!", Toast.LENGTH_SHORT).show();
                    mCrime.setPickUp("St. John's Wood Apartments");
                } else if (checkedId==R.id.airport_radio){
                    Toast.makeText(getContext(), "Welcome back home!", Toast.LENGTH_SHORT).show();
                    mCrime.setPickUp("Airport");

                }
            }
        });

        mDateButton=(Button) v.findViewById(R.id.date);
        mDateButton.setText(DateFormat.format("EEEE, dd MMMM yyyy", mCrime.getDate()));
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm= getFragmentManager();
                DatePickerFragment dialog= DatePickerFragment.newInstance((mCrime.getDate())); //pass this date into fragment bundle
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_CODE_DATE);    //11. set DatePickerfragmenet to report back to CrimeFragment after dying
                dialog.show(fm, "datepicker");
            }
        });
        mTimeButton=(Button) v.findViewById(R.id.time);

        SimpleDateFormat dateFormat1 = new SimpleDateFormat("hh:mm a");
        dateFormat1.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
        mTimeButton.setText(dateFormat1.format( mCrime.getTime()));
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm= getFragmentManager();
                TimePickerFragment dialog= TimePickerFragment.newInstance((mCrime.getTime())); //pass this date into fragment bundle
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_CODE_TIME);    //11. set DatePickerfragmenet to report back to CrimeFragment after dying
                dialog.show(fm, "timepicker");
            }
        });
        return v;
    }

    @Override
    //this method is called from DatePickerFragment upon clicking the positive button
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!= Activity.RESULT_OK)
            return;
        if(requestCode==REQUEST_CODE_DATE){
            long date= (long) data.getSerializableExtra("date picked");
            mCrime.setDate(date);
            mDateButton.setText(DateFormat.format("EEEE, dd MMMM yyyy", mCrime.getDate()));
        } else if(requestCode==REQUEST_CODE_TIME){
            long date= (long) data.getSerializableExtra("time picked");
            mCrime.setTime(date);
            Log.i("CRIMEFRAG: ", ""+date);
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("hh:mm a");
            dateFormat1.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
            mTimeButton.setText(dateFormat1.format(new java.util.Date(date)));
        }
    }
}
