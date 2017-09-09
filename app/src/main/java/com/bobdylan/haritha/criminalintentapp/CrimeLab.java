package com.bobdylan.haritha.criminalintentapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import database.AppDbHelper;

public class CrimeLab {
    private static CrimeLab sCrimeLab;   //cant say sCrimelab = new CrimeLab() since we dont have a context instance
    private ArrayList<Crime> mCrimes;
    private UUID userID;
    private SQLiteDatabase mSQLiteDatabase;
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
        return values;
    }

    public ArrayList<Crime> getCrimes() {  //return as a List, so we can change or datastructure to Linkedlist or something if we need to in the future
      //  mCrimes = new ArrayList<Crime>();
     /*   mDatabase.child("users").child(userID.toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                JSONtoCrimes((Map<String, Object>)snap.getValue());
             //   Log.i("GET CRIMES", ""+ mCrimes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }); */

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
        Log.i("GETCRIMES: ", ""+ crimes.size());
        return crimes;
    }

    public void JSONtoCrimes(Map<String, Object> json){
        mCrimes.clear();
        if(json!=null){
            for(Map.Entry<String, Object> entry: json.entrySet()){
                Map crimedata = (Map) entry.getValue();
                Log.i("CRIMEDATA", ""+crimedata);
                Crime c = new Crime();
                c.setPickUp(crimedata.get("pickUp").toString());
                c.setTitle(crimedata.get("title").toString());
                c.setSolved(false);
                c.setDate(Long.parseLong(crimedata.get("date").toString()));
                c.setTime(Long.parseLong(crimedata.get("time").toString()));
                c.setFlat(crimedata.get("flat").toString());
                c.setPhone(crimedata.get("phone").toString());
                Log.i("Pickup; ",crimedata.get("pickUp").toString() );
                Log.i("Date; ",crimedata.get("date").toString() );
                mCrimes.add(c);
                //  c.setTitle(crimedata.get(""));
            }
        }

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


        //pack into Crime object
        Crime c= new Crime();  //create crime obj with details from the table's cursor

        c.setTime(time);
        c.setTitle(title);
        c.setDate(date);
        c.setFlat(flat);
        c.setPhone(phone);
        c.setPickUp(pickup);
        c.setId(UUID.fromString(crimeid));

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
      /*  ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("FB: ", "onChildAdded:" + dataSnapshot.getKey());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("FB: ", "onChildChanged:" + dataSnapshot.getKey());

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("FB: ", "onChildRemoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("FB: ", "onChildMoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FB: ", "onChildCancelled:" );

            }
        };

        mDatabase.child("users").addChildEventListener(childEventListener);
 */
        ValueEventListener valueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("FB: ", "onDataChange "+ dataSnapshot.getValue());
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
