package com.bobdylan.haritha.criminalintentapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.UUID;

import database.AppDbHelper;
import database.AppDbSchema;

/**
 * Created by haritha on 17/9/17.
 */

public class UserDefaults {
    private String name, flat, phoneno;
    public static SQLiteDatabase mSQLiteDatabase;

    public String getFlat() {
        return flat;
    }

    public void setFlat(String f){
        this.flat=f;
    }


    public static UserDefaults getUserDefaults(Context context) {
        mSQLiteDatabase= new AppDbHelper(context).getWritableDatabase();
        UserDefaults u=null;
      //  Cursor cursor = mSQLiteDatabase.rawQuery("select * from " + AppDbSchema.UserTable.NAME, null);
        Cursor cursor= mSQLiteDatabase.query(AppDbSchema.UserTable.NAME,
                null,       //null here means select all columns
                AppDbSchema.UserTable.Cols.USERID+"=?",
                new String[] {CrimeLab.userID.toString()} ,
                null,
                null,
                null
        );

        try {
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                String ret_name = cursor.getString(cursor.getColumnIndex(AppDbSchema.UserTable.Cols.USERNAME));
                String ret_flat = cursor.getString(cursor.getColumnIndex(AppDbSchema.UserTable.Cols.USERFLAT));
                String ret_phoneno = cursor.getString(cursor.getColumnIndex(AppDbSchema.UserTable.Cols.USERPHONE));
                Log.i("RETREIVED", "username, flat, phone");

                u = new UserDefaults();
                u.setPhoneno(ret_phoneno);
                u.setName(ret_name);
                u.setFlat(ret_flat);

            }

        } finally {
            cursor.close();
        }
        return u;
    }

    public void updateUser(){
        ContentValues values = getContentValues(this); //ie in onPause of CrimeFragment
        mSQLiteDatabase.update(AppDbSchema.UserTable.NAME, values,
                AppDbSchema.UserTable.Cols.USERID+ "= ?",
                new String[]
                        {CrimeLab.userID.toString()}
        );
    }

    private ContentValues getContentValues(UserDefaults user) {
        ContentValues values =  new ContentValues();
        values.put(AppDbSchema.UserTable.Cols.USERFLAT, user.getFlat());
        values.put(AppDbSchema.UserTable.Cols.USERNAME, user.getName());
        values.put(AppDbSchema.UserTable.Cols.USERPHONE, user.getPhoneno());
        values.put(AppDbSchema.UserTable.Cols.USERID, CrimeLab.userID.toString());
        return values;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }
}
