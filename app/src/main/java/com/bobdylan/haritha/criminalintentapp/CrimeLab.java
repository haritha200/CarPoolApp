package com.bobdylan.haritha.criminalintentapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;
import android.util.Log;

import database.AppDbSchema;
import database.AppDbSchema.AppTable;
import database.AppDbSchema.UserTable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import database.AppDbHelper;

public class CrimeLab {
    private static CrimeLab sCrimeLab;   //cant say sCrimelab = new CrimeLab() since we dont have a context instance
    private ArrayList<Crime> mCrimes;
    private UUID userID;
    private SQLiteDatabase mSQLiteDatabase;
    private ValueEventListener valueListener;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public static CrimeLab get(Context context) {
        if(sCrimeLab==null){
            sCrimeLab=new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {     //private constructor => only this class can create the object.
        mSQLiteDatabase = new AppDbHelper(context).getWritableDatabase();

        userID= getUserID();     //generate singleton userID when crimelab is created

        mCrimes = new ArrayList<Crime>();
        Log.i("crimelab: ", "inserted uid");
    }

    public void deleteAllApps(){
        mSQLiteDatabase.execSQL("delete from "+ AppTable.NAME);
    }

    public void deleteCrime(Crime c){
        mSQLiteDatabase.delete(AppTable.NAME,
                AppTable.Cols.CRIMEID+ "= ?",
                new String[]
                        {c.getId().toString()}
        );

        mDatabase.child("users").child(userID.toString()).child(c.getId().toString()).removeValue();
     //   Log.i("deleted crimesize:", ""+getCrimes().size());

    }


    public void updateCrime(Crime c){
        ContentValues values = getContentValues(c); //ie in onPause of CrimeFragment
        mSQLiteDatabase.update(AppTable.NAME, values,
                AppTable.Cols.CRIMEID+ "= ?",
                new String[]
                        {c.getId().toString()}
        );

        Log.i("updating: ", c.getTime() +  "");

        mDatabase.child("users").child(userID.toString()).child(c.getId().toString()).setValue(c);
        registerListener();

    }

    public void addCrime(Crime c){

        if(c!=null && !c.getTitle().isEmpty()){
            ContentValues values= getContentValues(c);
            mSQLiteDatabase.insert(AppTable.NAME,null,values);
        }


     //   mCrimes.add(c);
        //users/userID/crimeID/crime
        Log.i("pushing: ", c.getTime() +  "");
        mDatabase.child("users").child(userID.toString()).child(c.getId().toString()).setValue(c);
        registerListener();

    }

    private ContentValues getContentValues(Crime c) {
        ContentValues values =  new ContentValues();
        values.put(AppTable.Cols.TIME, String.valueOf(c.getTime()));
        values.put(AppTable.Cols.TITLE, c.getTitle());
        values.put(AppTable.Cols.FLAT, c.getFlat());
        values.put(AppTable.Cols.DATE, String.valueOf(c.getDate()));
        values.put(AppTable.Cols.CRIMEID, String.valueOf(c.getId()));
        values.put(AppTable.Cols.PICKUP, c.getPickUp());
        values.put(AppTable.Cols.PHONE, c.getPhone());
        values.put(AppTable.Cols.MATCHED, c.isMatched());
        return values;
    }

    public ArrayList<Crime> getCrimes() {  //return as a List, so we can change or datastructure to Linkedlist or something if we need to in the future

        String query= "SELECT * FROM " + AppTable.NAME + " ORDER BY  "+AppTable.Cols.DATE+" ASC";

        Cursor cursor= mSQLiteDatabase.rawQuery(query,null);
        ArrayList<Crime> crimes= new ArrayList<Crime>();

        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                crimes.add(packCrime(cursor));
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
      //  Log.i("GETCRIMES: ", ""+ crimes.size());
        return crimes;
    }

    public Crime getCrime(UUID id){
        if(id==null || id.toString().isEmpty()) return null;

        Cursor cursor= mSQLiteDatabase.query(AppTable.NAME,     //get cursor
                null,       //null here means select all columns
                AppTable.Cols.CRIMEID+"=?",
                new String[] {id.toString()} ,
                null,
                null,
                null
        );

        //use try block mostly because ummm <not sure> but so that 'finally' has cursor.close() ?
        try{
            if(cursor.getCount() !=0) {

                cursor.moveToFirst();       //IMPORTANT. MOVE TO FIRST ROW OF ALL RETRIEVED ROWS, (here 1 row only)

                //unpack from table and into Crime Object
                Crime c= packCrime(cursor);
                Log.i("retreiving: ", c.getTime() +  "");

                return c;
            }

        } finally {
            cursor.close();     //IMPORTANT TO CLOSE YOUR CURSOR
        }
        return null;
    }

    public Crime packCrime(Cursor cursor){

        Long time = cursor.getLong(cursor.getColumnIndex(AppTable.Cols.TIME));
        String title=cursor.getString(cursor.getColumnIndex(AppTable.Cols.TITLE));
        String flat= cursor.getString(cursor.getColumnIndex(AppTable.Cols.FLAT));
        Long date = cursor.getLong(cursor.getColumnIndex(AppTable.Cols.DATE));
        String pickup= cursor.getString(cursor.getColumnIndex(AppTable.Cols.PICKUP));
        String phone= cursor.getString(cursor.getColumnIndex(AppTable.Cols.PHONE));
        String crimeid=cursor.getString(cursor.getColumnIndex(AppTable.Cols.CRIMEID));
        Boolean ismatched= cursor.getInt(cursor.getColumnIndex(AppTable.Cols.MATCHED)) > 0;


        //pack into Crime object
        Crime c= new Crime();  //create crime obj with details from the table's cursor

        c.setTime(time);
        c.setTitle(title);
        c.setDate(date);
        c.setFlat(flat);
        c.setPhone(phone);
        c.setPickUp(pickup);
        c.setId(UUID.fromString(crimeid));
        c.setMatched(ismatched);

        return c;

    }

    private UUID getUserID() {

       // if(userID==null){
            Cursor  cursor = mSQLiteDatabase.rawQuery("select * from "+ UserTable.NAME,null);
            try {
                if (cursor.getCount() != 0) {
                    Log.i("USERID: ", "RETREIVED");
                    cursor.moveToFirst();       //IMPORTANT. MOVE TO FIRST ROW OF ALL RETRIEVED ROWS, (here 1 row only)
                    userID = UUID.fromString(cursor.getString(cursor.getColumnIndex(UserTable.Cols.USERID)));
                } else {
                    userID=UUID.randomUUID();
                    ContentValues values =  new ContentValues();
                    values.put(UserTable.Cols.USERID, userID.toString());
                    mSQLiteDatabase.insert(UserTable.NAME,null,values);
                    mDatabase.child("users").child(userID.toString()).setValue(userID.toString());
                    Log.i("USERID: ", "ADDED");
                }
        } finally {
                cursor.close();
            }
      //  }
        registerListener();
        return userID;
    }

    public void registerListener(){

        if(valueListener==null){
            valueListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i("FB: ", "onDataChange "+ dataSnapshot.getValue());
                    findMatch((Map<String, Object>) dataSnapshot.getValue());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w("FB: ", "loadPost:onCancelled", databaseError.toException());

                }
            };
            mDatabase.child("users").addValueEventListener(valueListener);

        }


    }

    public void findMatch(Map<String, Object> allUsers){
        if(allUsers==null) return;

        for(Map.Entry<String, Object> singleUserData: allUsers.entrySet()){
            if(!singleUserData.getKey().equals(userID.toString())){
                Map<String, Object> allTripsData= (Map<String, Object>)singleUserData.getValue();
                for(Map.Entry<String, Object> singleTripData: allTripsData.entrySet() ){
                    Map tripDetails =(Map)singleTripData.getValue();
                    for (Crime c: getCrimes()) {
                      //  Boolean ismatched= false;
                        if(!c.isMatched() && c.getPickUp().equals(tripDetails.get("pickUp"))){

                            if(DateFormat.format("EEEE, dd MMMM yyyy",new Date(c.getDate())).equals(DateFormat.format("EEEE, dd MMMM yyyy", new Date((long)tripDetails.get("date"))))){
                                Log.i("MATCHED: ", "DATES MATCHED " + c.getTime() + " and "+ tripDetails.get("time"));

                                SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH");
                                dateFormat1.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));

                                SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH");
                                dateFormat2.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));

                                Log.i("Times: ",dateFormat1.format(new Date(c.getTime())) + " and " +  dateFormat2.format(new Date((long)tripDetails.get("time"))));
                                if((Math.abs(c.getTime()-(long)tripDetails.get("time")))/60000<=60){
                                    Log.i("MATCHED: ", "TIMES MATCHED" );
                                    c.setMatched(true);
                                    updateCrime(c);
                                }
                            }
                        }

                    }
                }

            }
            
        }
    }
}
