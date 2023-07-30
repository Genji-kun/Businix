package com.example.businix.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.businix.R;
import com.example.businix.utils.OnSpinnerChangeListener;

import java.util.List;

public class DateLeaveAdapter extends ArrayAdapter<String> {

    private List<String> dateList;
    private OnSpinnerChangeListener spinnerChangeListener;

    public void setSpinnerChangeListener(OnSpinnerChangeListener listener) {
        this.spinnerChangeListener = listener;
    }

    public DateLeaveAdapter(Context context, List<String> dateList) {
        super(context, R.layout.list_item_leave_dates, dateList);
        this.dateList = dateList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.list_item_leave_dates, parent, false);
        }

        String date = dateList.get(position);

        TextView textViewDate = view.findViewById(R.id.text_date);
        Spinner spinnerShift = view.findViewById(R.id.spinner_shift);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner, getContext().getResources().getStringArray(R.array.shift_options));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShift.setAdapter(adapter);

        spinnerShift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (spinnerChangeListener != null) {
                    spinnerChangeListener.onSpinnerChanged(position, selectedItem);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // ...
            }
        });

        textViewDate.setText(date);
        return view;
    }
}
