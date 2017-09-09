package com.bobdylan.haritha.criminalintentapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by haritha on 2/9/17.
 */

public class TimePickerFragment extends DialogFragment {

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
        Calendar calendar=new GregorianCalendar();          //split date into year,month,day
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
                        int hour=timePicker.getCurrentHour();
                        int min= timePicker.getCurrentMinute();
                        Date date= new GregorianCalendar(0,0,0,hour,min).getTime();
                        sendResult(Activity.RESULT_OK, date.getTime());   //send date back to targetfragment (ie CrimeFragment)
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
