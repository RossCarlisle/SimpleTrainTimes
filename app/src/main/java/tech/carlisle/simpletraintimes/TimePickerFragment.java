package tech.carlisle.simpletraintimes;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Ross on 09/03/2018.
 */

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        TextView timeTextView = getActivity().findViewById(R.id.timeTextView);
        String setHour = Integer.toString(hourOfDay);
        String setMinute = Integer.toString(minute);
        if (hourOfDay < 10) {
            setHour = "0" + hourOfDay;
        }
        if (minute < 10) {
            setMinute = "0" + minute;
        }
        String finalTime = setHour + ":" + setMinute;
        timeTextView.setText(finalTime);

    }
}
