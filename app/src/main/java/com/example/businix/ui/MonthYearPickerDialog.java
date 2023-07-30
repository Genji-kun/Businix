package com.example.businix.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.businix.R;

import java.util.Calendar;

public class MonthYearPickerDialog extends DialogFragment {

    private static final String[] MONTHS = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};

    private NumberPicker monthPicker;
    private NumberPicker yearPicker;

    private OnDateSetListener listener;
    private int yearSelected, monthSelected;

    public MonthYearPickerDialog(OnDateSetListener listener,int year,int month) {
        this.listener = listener;
        this.yearSelected = year;
        this.monthSelected = month;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_month_year_picker, null);

        monthPicker = view.findViewById(R.id.picker_month);
        yearPicker = view.findViewById(R.id.picker_year);

        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(MONTHS.length - 1);
        monthPicker.setDisplayedValues(MONTHS);
        monthPicker.setValue(monthSelected);

        int year = Calendar.getInstance().get(Calendar.YEAR);
        yearPicker.setMinValue(year - 100);
        yearPicker.setMaxValue(year + 100);
        yearPicker.setValue(yearSelected);

        builder.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onCancel();
                        MonthYearPickerDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    public interface OnDateSetListener {
        void onDateSet(DatePicker view, int year, int month, int dayOfMonth);
        void onCancel();
    }
}
