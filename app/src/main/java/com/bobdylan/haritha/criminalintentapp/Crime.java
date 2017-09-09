package com.bobdylan.haritha.criminalintentapp;

import java.util.Date;
import java.util.UUID;

/**
 * Created by haritha on 31/12/16.
 */

public class Crime {
    private String mTitle;
    private String mPickUp;
    private String mFlat;
    private String mPhone;
    private UUID mId;
    private long mDate;
    private long mTime;

    public void setId(UUID id) {
        mId = id;
    }

    private boolean mIsSolved;

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getFlat() {
        return mFlat;
    }

    public void setFlat(String flat) {
        mFlat = flat;
    }

    public String getPickUp() {
        return mPickUp;
    }

    public void setPickUp(String pickUp) {
        mPickUp = pickUp;
    }



    public Crime() {
        mId= UUID.randomUUID(); //generate new id for every new object
        Date date=new Date();       //set as default date (will implement datepicker later to set date)
        //mTime=new Date();
        mDate=date.getTime();
        mTime=date.getTime();
        mPickUp=new String("St. John's Wood Apartments");       //DEFAULTS
        mTitle=  new String ("Ravisankar");
        mFlat = new String("D-602");
        mPhone= new String ("98441025554");

    }
    public long getTime() {return mTime;}

    public void setTime(long time) {mTime = time;}

    public long getDate() {
        return mDate;
    }

    public boolean isSolved() {
        return mIsSolved;
    }

    public void setDate(long date) {
        mDate = date;
    }

    public void setSolved(boolean solved) {
        mIsSolved = solved;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public UUID getId() {
        return mId;
    }

}
