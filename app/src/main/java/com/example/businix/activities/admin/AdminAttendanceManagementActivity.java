package com.example.businix.activities.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.businix.R;
import com.example.businix.adapters.AttendanceAdapter;
import com.example.businix.adapters.DepartmentAdapter;
import com.example.businix.controllers.AttendanceController;
import com.example.businix.controllers.DepartmentController;
import com.example.businix.models.Attendance;
import com.example.businix.models.Department;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import org.checkerframework.checker.units.qual.A;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AdminAttendanceManagementActivity extends AppCompatActivity {

    private ImageView btnHome;
    private TextInputEditText inputEmployeeName;
    private AttendanceAdapter attendanceAdapter;
    private List<Attendance> attendanceList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_attendance_management);

        ListView listView = (ListView) findViewById(R.id.list_view_attendance);
        AttendanceController attendanceController = new AttendanceController();
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        attendanceController.getAttendancesByDate(date,new OnCompleteListener<List<Attendance>>() {
            @Override
            public void onComplete(@NonNull Task<List<Attendance>> task) {
                if (task.isSuccessful()) {
                    attendanceList = task.getResult();
                    attendanceAdapter = new AttendanceAdapter(AdminAttendanceManagementActivity.this, R.layout.list_view_attendance, attendanceList);
                    listView.setAdapter(attendanceAdapter);
                } else {
                    // xử lý lỗi
                }
            }
        });

        inputEmployeeName = (TextInputEditText) findViewById(R.id.input_empl_name);
        inputEmployeeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                if (attendanceAdapter != null) {
                    attendanceAdapter.getFilter().filter(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnHome = (ImageView) findViewById(R.id.btn_home);
        btnHome.setOnClickListener(v -> {
            finish();
        });
    }
}