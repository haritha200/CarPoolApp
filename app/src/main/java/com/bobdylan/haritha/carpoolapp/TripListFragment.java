package com.bobdylan.haritha.carpoolapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class TripListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private int changedCrimePosition;
    private boolean msubtitleVisible=false;
    private Button mFirstCrimeButton;
    private CrimeAdapter mCrimeAdapter;     //need this to call setCrimes, since we've extended the adapter implementation

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);    //tells android to call the menu lifecycle methods..it wont call them otherwise

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem= menu.findItem(R.id.show_subtitle);
        if(msubtitleVisible)
            subtitleItem.setTitle("Hide Subtitle"); //since if it is visible, we want the option to "hide subtitle"
        else
            subtitleItem.setTitle("SHow Subtitle");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       CrimeLab crimes= CrimeLab.get(getActivity());
       switch (item.getItemId()) {      //identify which menu item was selected
           case R.id.add_crime: {
               Crime c = new Crime();
               crimes.addCrime(c);  //add to the model layer
               Intent intent = CrimePagerActivity.newIntent(getActivity(), c.getId());
               startActivity(intent);   //edit details of the new crime in the crimepageractivity
               return true;
           }
           case R.id.show_subtitle: {
               msubtitleVisible= !msubtitleVisible;
               getActivity().invalidateOptionsMenu();   //this redraws the menu by calling onCreateOptionsMenu
               updateSubtitle();
               return true;
           }
           default:
               return super.onOptionsItemSelected(item);

       }
    }

    private void updateSubtitle(){
        int noOfCrimes = CrimeLab.get(getActivity()).getCrimes().size();
        String subtitle = getString(R.string.no_of_crimes, noOfCrimes);
        if(!msubtitleVisible)
            subtitle=null;

        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(subtitle);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mRecyclerView=(RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); //boiler plate. need it to for recycleview to work.
        //..infinite scroll.. mRecyclerView.getLayoutManager().scrollToPosition(1000000);
        mFirstCrimeButton=(Button) view.findViewById(R.id.create_crime_button);


        if(savedInstanceState!=null)
            msubtitleVisible= savedInstanceState.getBoolean("saved subtitle state");

        updateUI();     //connect (and refetch data from) this mRecyclerview to crimeadapter.
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("saved subtitle state", msubtitleVisible);
    }

    private void updateUI() {
        //List<Crime> crimes= CrimeLab.get(getActivity()).getCrimes();
       /* if (mRecyclerView.getAdapter()!= null) {
            //  mRecyclerView.getAdapter().notifyItemChanged(changedCrimePosition); //refetch single data from model layer

            mRecyclerView.getAdapter().notifyDataSetChanged();  //or refetch all data from model layer for viewpager
        }
        else mRecyclerView.setAdapter(new CrimeAdapter());*/
        List<Crime> crimes= CrimeLab.get(getActivity()).getCrimes();

        if(mCrimeAdapter==null){
            mCrimeAdapter=new CrimeAdapter();
            mRecyclerView.setAdapter(mCrimeAdapter);
        }else{
            mCrimeAdapter.setCrimes(crimes);    //update the dataset managed by the adapter to this snapshot's list of crimes
            mCrimeAdapter.notifyDataSetChanged();
        }

        mFirstCrimeButton.setVisibility(View.VISIBLE);

        if(crimes.size()<=0){
        mFirstCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crime c = new Crime();
                CrimeLab crimes = CrimeLab.get(getActivity());
                crimes.addCrime(c);

                Intent i = CrimePagerActivity.newIntent(getActivity(), c.getId());
                startActivity(i);
            }
        });
        }
        else {                mFirstCrimeButton.setVisibility(View.GONE);
             }

        updateSubtitle();   //update subtitle when you resume after editing crime in pageviewer

    }



    //since each viewholder manages one view, we can make it implement an onclicklistener as proxy too
    public class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private static final String EXTRA_CRIME_ID = "crime id extra";
        private Crime mCrime;
        private TextView mTitle, mDate;
        private CheckBox mCheckBox;

        public CrimeHolder(View itemView) {
            super(itemView);

            mTitle= (TextView) itemView.findViewById(R.id.title_list);
            mDate=(TextView)itemView.findViewById(R.id.date_list);
            mCheckBox=(CheckBox) itemView.findViewById(R.id.solved_list);

            itemView.setOnClickListener(this); //if itemview is clicked, Crimeholder's onClick(view) method is called
                                                //since this class itself implements onClickListener

        }

        public void bindCrimeToHolder(Crime crime) {
            mCrime=crime;

           mCheckBox.setChecked(crime.isSolved());
            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mCrime.setSolved(isChecked);
                }
            });
            mDate.setText(crime.getDate().toString());
           mTitle.setText(crime.getTitle());
        }


        @Override
        public void onClick(View v) {
            changedCrimePosition= getAdapterPosition();


                    Toast.makeText(getActivity(),
                   mCrime.getTitle() + " clicked! Position: "+ changedCrimePosition, Toast.LENGTH_SHORT)
                   .show();

           //Intent intent= new Intent(getActivity(), TripSheetPagerActivity.class);
           //intent.putExtra(EXTRA_CRIME_ID, mCrime.getId());

            //instead of creating the intent in this fragment like^^, we'll create it in the activity..
            //because we want to keep intent-info within activities extras and fragment-info within fragment bundles

            Intent intent= CrimePagerActivity.newIntent(getActivity(), mCrime.getId());  //1. store id of clicked crime
            startActivity(intent);  //2.
        }
    }



    public class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{

        private List<Crime> mCrimes= CrimeLab.get(getActivity()).getCrimes();

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

        public void setCrimes(List<Crime>crimes){
            mCrimes=crimes;
        }


    }


}
