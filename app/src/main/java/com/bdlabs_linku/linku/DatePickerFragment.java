package com.bdlabs_linku.linku;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    static DatePickerFragment newInstance(int year, int month, int day) {
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", day);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = args.getInt("year", c.get(Calendar.YEAR));
        int month = args.getInt("month", c.get(Calendar.MONTH));
        int day = args.getInt("day", c.get(Calendar.DAY_OF_MONTH));

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), R.style.AppTheme_Dialog, this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        if(this.getActivity() instanceof CreateNewEventActivity) {
            ((CreateNewEventActivity) getActivity()).setDate(day, month, year);
        }
        else if(this.getActivity() instanceof EditEventActivity){
            ((EditEventActivity) getActivity()).setDate(day, month, year);
        }
    }
}