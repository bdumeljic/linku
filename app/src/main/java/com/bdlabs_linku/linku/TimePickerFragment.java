package com.bdlabs_linku.linku;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    static TimePickerFragment newInstance(int hour, int minute) {
        Bundle args = new Bundle();
        args.putInt("hour", hour);
        args.putInt("minute", minute);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();

        final Calendar c = Calendar.getInstance();

        //The second input is a default value in case hour or minute are empty
        int hour = args.getInt("hour", c.get(Calendar.HOUR_OF_DAY));
        int minute = args.getInt("minute", c.get(Calendar.MINUTE));

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), R.style.AppTheme_Dialog, this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        if(this.getActivity() instanceof CreateNewEventActivity) {
            ((CreateNewEventActivity) getActivity()).setTime(hourOfDay, minute);
        }
        else if(this.getActivity() instanceof EditEventActivity){
            ((EditEventActivity) getActivity()).setTime(hourOfDay, minute);
        }
    }
}