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
import com.example.businix.models.LeaveRequestDetail;
import com.example.businix.utils.DateUtils;
import com.example.businix.utils.OnSpinnerChangeListener;

import java.util.List;

public class LeaveDetailHistoryAdapter extends ArrayAdapter<LeaveRequestDetail> {

    private List<LeaveRequestDetail> details;

    public LeaveDetailHistoryAdapter(Context context, List<LeaveRequestDetail> details) {
        super(context, R.layout.list_item_leave_history_detail, details);
        this.details = details;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.list_item_leave_history_detail, parent, false);
        }

        LeaveRequestDetail detail = details.get(position);

        TextView textDate = view.findViewById(R.id.text_date);
        TextView textShift = view.findViewById(R.id.text_shift);

        textDate.setText(DateUtils.formatDate(detail.getDate()));
        textShift.setText(detail.getShift());

        return view;
    }
}
