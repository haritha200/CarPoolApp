package com.bobdylan.haritha.criminalintentapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * A simple fragment subclass.
 */
public class DatePickerFragment extends DialogFragment {


    public static DatePickerFragment newInstance(long date) {
        Bundle args= new Bundle();
        args.putSerializable("display date", date); //date to be displayed by the datepicker (which was set originally in crimefragment)
        DatePickerFragment frag= new DatePickerFragment();
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v =  View.inflate(getActivity(), R.layout.fragment_date_picker, null);  //'inflate' a view to set it


        final DatePicker datePicker= (DatePicker) v.findViewById(R.id.dialog_date_picker); // then findviewbyid for each subview

        final long date= (long) getArguments().getSerializable("display date");   //get date from frag's args
        Calendar calendar=new GregorianCalendar();          //split date into year,month,day
        calendar.setTime(new Date(date));
        int year= calendar.get(Calendar.YEAR);
        int month= calendar.get(Calendar.MONTH);
        int day= calendar.get(Calendar.DAY_OF_MONTH);

        datePicker.init(year,month,day,null);


        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Choose date: ")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year=datePicker.getYear();
                        int month= datePicker.getMonth();
                        int day=datePicker.getDayOfMonth(); //get date picked
                        Date date= new GregorianCalendar(year,month,day).getTime();
                        sendResult(Activity.RESULT_OK, date.getTime());   //send date back to targetfragment (ie CrimeFragment)
                    }
                })
                .create();

    }

    private void sendResult(int resultOk, long date) {
        Intent intent= new Intent();
        intent.putExtra("date picked", date); //call onactivityresult of the target fragment and pass this intent with date picked extra
        getTargetFragment().onActivityResult(CrimeFragment.REQUEST_CODE_DATE, resultOk, intent);
        //ie, call onactivityresult of the targetfragment (ie CrimeFragment) and then this fragment dies
    }


}
