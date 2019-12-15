package com.example.birthdays;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddImportantDateDialog extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                EditText dateOfImportantDate = (EditText) getActivity().findViewById(R.id.dateOfImportantDate);
                GregorianCalendar calendar = new GregorianCalendar(year, month, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy");
                dateOfImportantDate.setText(dateFormat.format(calendar.getTime()));
            }
        }, year, month, day);
    }

}
