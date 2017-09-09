package com.bobdylan.haritha.carpoolapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment; //add support.v4 library dependency through the project structure as well!!!
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
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

import java.util.Date;
import java.util.UUID;

import static java.lang.Boolean.TRUE;

/**
 * Created by haritha on 31/12/16.
 */

public class TripSheetFragment extends Fragment {
    private static final String ARG_CRIME_ID =  "crime id arg";
    public static final int REQUEST_CODE_DATE = 0 ;
    private static final int REQUEST_CODE_CONTACT =1 ;
    private static final int REQUEST_CODE_DIAL = 2 ;
    private Crime mCrime;       //since this fragment needs to present a crime
    private EditText mTitle;
    private CheckBox mSolved;
    private Button mDateButton, mReportButton, mSuspect, mCallSuspect;
    private String suspectPhoneno;

    //TO CREATE THE FRAGMENT AND INSERT FRAG-ARGS. SO THAT THE CRIMEACTIVITY CAN CALL IT TO PLACE THIS FRAGMENT IN IT'S VIEW
    //5. bind args to this fragment
    public static TripSheetFragment newInstance(UUID crimeid) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeid);
        TripSheetFragment fragment = new TripSheetFragment();
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

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
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
        mSolved=(CheckBox) v.findViewById(R.id.solved);
        mSolved.setChecked(mCrime.isSolved());

        mSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mDateButton=(Button) v.findViewById(R.id.date);
        mDateButton.setText(DateFormat.format("EEEE, dd MMMM yyyy", mCrime.getDate()));
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm= getFragmentManager();
                DatePickerFragment dialog= DatePickerFragment.newInstance(mCrime.getDate()); //pass this date into fragment bundle
                dialog.setTargetFragment(com.bobdylan.haritha.criminalintentapp.TripSheetFragment.this, REQUEST_CODE_DATE);    //11. set DatePickerfragmenet to report back to TripSheetFragment after dying
                dialog.show(fm, "datepicker");
            }
        });

        mReportButton= (Button) v.findViewById(R.id.send_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getString(R.string.crime_report, mCrime.getTitle(), mCrime.getDate().toString(), mCrime.isSolved()== TRUE? "Solved" : "Unsolved"));
                i= Intent.createChooser(i, "How are we sharing the crime today?");  //to always display the chooser for the intent
                startActivity(i);
            }
        });

        mSuspect = (Button) v.findViewById(R.id.contact_name);
        final Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        //boilerplate packagemanager code to check if an app exists which can service such an intent
        PackageManager packageManager = getActivity().getPackageManager();  //packagemanager has details of all activities of all apps in mobile
        if (packageManager.resolveActivity(i,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {       //match to default category of intent => launcher type activity must match
            mSuspect.setEnabled(false);
        }
        mSuspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(i, REQUEST_CODE_CONTACT);
            }
        });

        //TODO: FIX THE PHONE DIAL BUTTON
        mCallSuspect = (Button) v.findViewById(R.id.dial_suspect);
        mCallSuspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL);//, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(i, REQUEST_CODE_DIAL);
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
            Date date= (Date) data.getSerializableExtra("date picked");
            mCrime.setDate(date);
            mDateButton.setText(DateFormat.format("EEEE, dd MMMM yyyy", mCrime.getDate()));
        }
        if(requestCode==REQUEST_CODE_CONTACT){
            Uri uri= data.getData();

            Cursor c = getActivity().getContentResolver().query(uri,
                    new String[] {ContactsContract.Contacts.DISPLAY_NAME},      //query the uri dataobj to get the displayname
                    null,
                    null,
                    null);

            if (c.getCount()==0) return; //important to check !!!

            c.moveToFirst();    //first row
            String name = c.getString(0); //column index= 0 means 1st column of the result (here, our only column)
            mCrime.setSuspect(name);
            mSuspect.setText(name);
            c.close();
        }
    }
}
