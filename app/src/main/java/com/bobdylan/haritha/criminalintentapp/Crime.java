package com.bobdylan.haritha.criminalintentapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
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
    private boolean mIsMatched = false;

    public Crime(String name, String flat, String phoneno) {
        mId= UUID.randomUUID(); //generate new id for every new object
        Date date=new Date();
        mDate=date.getTime();

        GregorianCalendar g = new GregorianCalendar();
        g.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
        g.setTime(date);
        int hour= g.get(Calendar.HOUR_OF_DAY);
        int min= g.get(Calendar.MINUTE);
        g.set(0, 0, 0, hour, min);
        mTime=g.getTimeInMillis();

        mPickUp=new String("St. John's Wood Apartments");
        mIsMatched = false;
        mTitle=name;
        mFlat=flat;
        mPhone=phoneno;
    }


    public boolean isMatched() {
        return mIsMatched;
    }

    public void setMatched(boolean matched) {
        mIsMatched = matched;
    }

    public Crime() {
        mId= UUID.randomUUID(); //generate new id for every new object
        Date date=new Date();       //set as default date (will implement datepicker later to set date)
        //mTime=new Date();
        mDate=date.getTime();
        mTime=date.getTime();
        mPickUp=new String();       //DEFAULTS
        mTitle=  new String ();
        mFlat = new String();
        mPhone= new String ();
        mIsMatched = false;

    }

    public void setId(UUID id) {
        mId = id;
    }

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

    public long getTime() {return mTime;}

    public void setTime(long time) {mTime = time;}

    public long getDate() {
        return mDate;
    }

    public void setDate(long date) {
        mDate = date;
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
