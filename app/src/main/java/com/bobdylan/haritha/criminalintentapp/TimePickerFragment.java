package com.bobdylan.haritha.criminalintentapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.security.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by haritha on 2/9/17.
 */

public class TimePickerFragment extends DialogFragment{

    public static TimePickerFragment newInstance(long date) {
        Bundle args= new Bundle();
        args.putSerializable("display time", date); //date to be displayed by the datepicker (which was set originally in crimefragment)
        TimePickerFragment frag= new TimePickerFragment();
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v =  View.inflate(getActivity(), R.layout.fragment_time_picker, null);  //'inflate' a view to set it
        final TimePicker timePicker= (TimePicker) v.findViewById(R.id.dialog_time_picker); // then findviewbyid for each subview
        final long date= (long) getArguments().getSerializable("display time");   //get date from frag's args
        final Calendar calendar= GregorianCalendar.getInstance();          //split date into year,month,day
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
        calendar.setTime(new Date(date));
        int hour= calendar.get(Calendar.HOUR_OF_DAY);
        int min= calendar.get(Calendar.MINUTE);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(min);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Choose time: ")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int hour, min;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            hour =timePicker.getHour();
                            min = timePicker.getMinute();
                        }
                        else {
                            hour = timePicker.getCurrentHour();
                            min = timePicker.getCurrentMinute();
                        }
                        GregorianCalendar g = new GregorianCalendar();
                        g.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
                        g.set(0, 0, 0, hour, min);
                        long time = g.getTimeInMillis();
                        Log.i("TIMEPICKER: ", "hour= " + hour + ", min= " + min + ", long mills= " + time);
                        sendResult(Activity.RESULT_OK, time);   //send date back to targetfragment (ie CrimeFragment)

                    }
                })
                .create();

    }

    private void sendResult(int resultOk, long date) {
        Intent intent= new Intent();
        intent.putExtra("time picked", date); //call onactivityresult of the target fragment and pass this intent with date picked extra
        getTargetFragment().onActivityResult(CrimeFragment.REQUEST_CODE_TIME, resultOk, intent);
        //ie, call onactivityresult of the targetfragment (ie CrimeFragment) and then this fragment dies
    }

}
