package com.bobdylan.haritha.criminalintentapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private int changedCrimePosition;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);    //tells android to call the menu lifecycle methods..it wont call them otherwise

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       CrimeLab crimes= CrimeLab.get(getActivity());
       switch (item.getItemId()) {      //identify which menu item was selected
           case R.id.add_crime: {
               UserDefaults u = UserDefaults.getUserDefaults(getActivity());
               Crime c = new Crime(u.getName(), u.getFlat(), u.getPhoneno());
              // Crime c = new Crime();
               crimes.addCrime(c);  //add to the model layer
               Intent intent = CrimePagerActivity.newIntent(getActivity(), c.getId());
               startActivity(intent);   //edit details of the new crime in the crimepageractivity
           //    crimes.updateCrime(c);
               return true;
           }
           case R.id.user_defaults:{
                Intent intent = UserDefaultsActivity.newIntent(getActivity());
                startActivity(intent);
                return  true;
           }
           default:
               return super.onOptionsItemSelected(item);

       }
    }


    public View updateView(LayoutInflater inflater, ViewGroup container){

        final CrimeLab crimes= CrimeLab.get(getActivity());
        int noOfCrimes = crimes.getCrimes().size();
        View view;
        if(noOfCrimes==0){
            view=  inflater.inflate(R.layout.fragment_empty_crimelist, container, false);
            Button b= (Button)view.findViewById(R.id.create_crime_button);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserDefaults u = UserDefaults.getUserDefaults(getActivity());
                    Crime c = new Crime(u.getName(), u.getFlat(), u.getPhoneno());
                    // Crime c = new Crime();
                    crimes.addCrime(c);  //add to the model layer
                    Intent intent = CrimePagerActivity.newIntent(getActivity(), c.getId());
                    startActivity(intent);
                }
            });
        }
        else {
            view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        }
        return view;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = updateView(inflater,container);
        mRecyclerView=(RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); //boiler plate. need it to for recycleview to work.
     //   if(savedInstanceState!=null)
       //     msubtitleVisible= savedInstanceState.getBoolean("saved subtitle state");

        updateUI();     //connect (and refetch data from) this mRecyclerview to crimeadapter.
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();

       // Log.i("ONRESUME: ", "INSIDE CRIMELIST FRAG");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // outState.putBoolean("saved subtitle state", msubtitleVisible);
    }

    private void updateUI() {
        //List<Crime> crimes= CrimeLab.get(getActivity()).getCrimes();

        if (mRecyclerView.getAdapter()!= null)
          //  mRecyclerView.getAdapter().notifyItemChanged(changedCrimePosition); //refetch single data from model layer
        {
            mRecyclerView.setAdapter(new CrimeAdapter());
            mRecyclerView.getAdapter().notifyDataSetChanged();  //or refetch all data from model layer for viewpager
        }
        else {
            mRecyclerView.setAdapter(new CrimeAdapter());
        }

    }

    //since each viewholder manages one view, we can make it implement an onclicklistener as proxy too
    public class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private static final String EXTRA_CRIME_ID = "crime id extra";
        private Crime mCrime;
        private TextView mTitle, mDate, mTime, mFlat, mPickup;
        private CheckBox mCheckBox;
        private RelativeLayout tripItem;


        public CrimeHolder(View itemView) {
            super(itemView);

            mTitle= (TextView) itemView.findViewById(R.id.title_list);
            mFlat= (TextView) itemView.findViewById(R.id.flat_list);
            mPickup= (TextView) itemView.findViewById(R.id.pickup_list);

            mDate=(TextView)itemView.findViewById(R.id.date_list);
            tripItem = (RelativeLayout)itemView.findViewById(R.id.trip_element);
            mTime=(TextView)itemView.findViewById(R.id.time_list);
            itemView.setOnClickListener(this); //if itemview is clicked, Crimeholder's onClick(view) method is called
                                                //since this class itself implements onClickListener

        }

        public void bindCrimeToHolder(Crime crime) {
            mCrime=crime;

            mDate.setText(DateFormat.format("EEEE, dd MMMM", mCrime.getDate()));
           // Log.i("listfrag: ",""+ mCrime.getTime());
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("hh:mm a");
            dateFormat1.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
            mTime.setText(dateFormat1.format(new Date(mCrime.getTime())));
            if(!mCrime.isMatched())
              tripItem.setBackgroundColor(getResources().getColor(R.color.grey));
            else
                tripItem.setBackgroundColor(getResources().getColor(R.color.green));

            mTitle.setText(crime.getTitle());
            mFlat.setText(crime.getFlat());
            mPickup.setText(crime.getPickUp());
        }


        @Override
        public void onClick(View v) {
            changedCrimePosition= getAdapterPosition();
            Intent intent;
            intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());  //1. store id of clicked crime
            startActivity(intent);  //2.
        }
    }



    public class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{

        private List<Crime> mCrimes;

        public CrimeAdapter() {
            super();
            mCrimes = CrimeLab.get(getActivity()).getCrimes();
         //   Log.i("IN CRIMEADAPT, SIZE: ", ""+mCrimes.size());

        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater=LayoutInflater.from(getActivity());
            View view= inflater.inflate(R.layout.list_item, parent, false);     //inflate the item's view

            return new CrimeHolder(view);   //wrapping the view in the CrimeHolder and returning the crimeholder object
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime=mCrimes.get(position);
            //..infinite scroll..  Crime crime=mCrimes.get(position%mCrimes.size());

            holder.bindCrimeToHolder(crime);    //so the chosen Crime's reference is kept in the crimeholder now too,
                                                //and crime's data is displayed in the holder's views

        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
            //..infinite scroll..  return Integer.MAX_VALUE;
        }


    }


}
