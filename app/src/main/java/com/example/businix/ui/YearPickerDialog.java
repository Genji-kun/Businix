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

public class YearPickerDialog extends DialogFragment {

    private NumberPicker yearPicker;

    private OnDateSetListener listener;
    private int yearSelected;

    public YearPickerDialog(OnDateSetListener listener, int year) {
        this.listener = listener;
        this.yearSelected = year;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_year_picker, null);

        yearPicker = view.findViewById(R.id.picker_year);

        int year = Calendar.getInstance().get(Calendar.YEAR);
        yearPicker.setMinValue(year - 100);
        yearPicker.setMaxValue(year + 100);
        yearPicker.setValue(yearSelected);

        builder.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onDateSet(null, yearPicker.getValue());
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onCancel();
                        YearPickerDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    public interface OnDateSetListener {
        void onDateSet(DatePicker view, int year);
        void onCancel();
    }
}
