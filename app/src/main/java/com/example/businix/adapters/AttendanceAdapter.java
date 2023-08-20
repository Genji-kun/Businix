package com.example.businix.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.businix.R;
import com.example.businix.activities.admin.AdminEditAttendanceActivity;
import com.example.businix.controllers.AttendanceController;
import com.example.businix.models.Attendance;
import com.example.businix.models.Employee;
import com.example.businix.models.Position;
import com.example.businix.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AttendanceAdapter extends ArrayAdapter<Attendance> {
    private List<Attendance> attendanceList;
    private List<Attendance> filteredList;
    private Context context;

    public AttendanceAdapter(Context context, int resource, List<Attendance> attendanceList) {
        super(context, resource, attendanceList);

        this.attendanceList = attendanceList;
        this.filteredList = attendanceList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.list_view_attendance, null);
        }
        AttendanceController attendanceController = new AttendanceController();
        Attendance attendance = filteredList.get(position);

        ImageView imgAvatar = (ImageView) view.findViewById(R.id.img_avatar);
        TextView tvEmplName = (TextView) view.findViewById(R.id.tv_empl_name);
        TextView tvPosName = (TextView) view.findViewById(R.id.tv_position_name);
        LinearLayout btnEdit = view.findViewById(R.id.btn_edit_attendance);


        if (attendance != null) {
            TextView tvIn = (TextView) view.findViewById(R.id.tv_in);
            Date checkInTime = attendance.getCheckInTime();
            tvIn.setText("in: " + DateUtils.formatDate(checkInTime,"HH:mm"));
            TextView tvOut = (TextView) view.findViewById(R.id.tv_out);
            Date checkOutTime = attendance.getCheckOutTime();
            tvOut.setText("out: " + DateUtils.formatDate(checkOutTime,"HH:mm"));
            attendance.getEmployee().get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Employee employee = documentSnapshot.toObject(Employee.class);
                    if (imgAvatar != null)
                        Glide.with(context).load(employee.getAvatar()).into(imgAvatar);
                    if (tvEmplName != null) {
                        tvEmplName.setText(employee.getFullName());
                    }
                    employee.getPosition().get().addOnSuccessListener(documentSnapshot1 -> {
                        if (documentSnapshot1.exists()) {
                            Position pos = documentSnapshot1.toObject(Position.class);
                            String positionName = pos.getName();
                            if (employee.getUserRole().toString().equals("ADMIN")) {
                                tvPosName.setText("Admin");
                            } else
                                tvPosName.setText(positionName);
                        } else {
                            tvPosName.setText("Chưa có chức vụ");
                        }
                    });
                }
            });

            btnEdit.setOnClickListener(v -> {
                Attendance currentAttendance = filteredList.get(position);

                // Gửi thông tin của vị trí hiện tại qua Activity mới
                Intent intent = new Intent(context, AdminEditAttendanceActivity.class);
                intent.putExtra("attendanceId", currentAttendance.getId());
                context.startActivity(intent);
            });
        }
        return view;
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = attendanceList;
                    results.count = attendanceList.size();
                } else {
                    List<Attendance> filteredAttendances = new ArrayList<>();
                    for (Attendance attendance : attendanceList) {
                        attendance.getEmployee().get().addOnSuccessListener(documentSnapshot -> {
                            if(documentSnapshot.exists()){
                                Employee employee = documentSnapshot.toObject(Employee.class);
                                boolean matchName = employee.getFullName().toLowerCase().contains(constraint.toString().toLowerCase());
                                if (matchName) {
                                    filteredAttendances.add(attendance);
                                }
                            }
                        });
                    }

                    results.values = filteredAttendances;
                    results.count = filteredAttendances.size();

                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<Attendance>) results.values;
                notifyDataSetChanged();
            }

        };
    }
}
