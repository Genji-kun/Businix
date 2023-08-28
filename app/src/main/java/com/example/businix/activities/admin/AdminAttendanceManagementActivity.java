package com.example.businix.activities.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.businix.R;
import com.example.businix.adapters.AttendanceAdapter;
import com.example.businix.adapters.DepartmentAdapter;
import com.example.businix.controllers.AttendanceController;
import com.example.businix.controllers.DepartmentController;
import com.example.businix.models.Attendance;
import com.example.businix.models.Department;
import com.example.businix.utils.DateUtils;
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
    private TextView tvDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_attendance_management);

        tvDate = findViewById(R.id.tv_date);
        ListView listView = (ListView) findViewById(R.id.list_view_attendance);
        AttendanceController attendanceController = new AttendanceController();
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        tvDate.setText("Ngày " + DateUtils.formatDate(date));
        attendanceController.getAttendancesByDate(date, new OnCompleteListener<List<Attendance>>() {
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

        tvDate.setOnClickListener(view -> {
            showDatePickerDialog();
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

    @Override
    protected void onResume() {
        super.onResume();
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        AttendanceController attendanceController = new AttendanceController();
        attendanceController.getAttendancesByDate(date, new OnCompleteListener<List<Attendance>>() {
            @Override
            public void onComplete(@NonNull Task<List<Attendance>> task) {
                if (task.isSuccessful()) {
                    attendanceList = task.getResult();
                    if (attendanceAdapter != null) {
                        attendanceAdapter.clear(); // Xóa dữ liệu cũ trong adapter
                        attendanceAdapter.addAll(attendanceList); // Thêm dữ liệu mới vào adapter
                        attendanceAdapter.notifyDataSetChanged(); // Cập nhật lại ListView
                    }
                } else {
                    // Xử lý lỗi
                }
            }
        });
    }


    private void showDatePickerDialog() {
        // Lấy ngày hiện tại để đặt làm giá trị mặc ring cho DatePickerDialog
        int year;
        int month;
        int dayOfMonth;
        String dateString = tvDate.getText().toString().replace("Ngày ", "");

        String[] endDateArr = dateString.split("/");
        year = Integer.parseInt(endDateArr[2]);
        month = Integer.parseInt(endDateArr[1]) - 1;
        dayOfMonth = Integer.parseInt(endDateArr[0]);
        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, yearSelected, monthOfYear, dayOfMonthSelected) -> {
                    // Lắng nghe sự kiện chọn ngày và cập nhật giá trị cho EditText tương ứng
                    Calendar cal = Calendar.getInstance();
                    String calDate = DateUtils.formatDate(cal.getTime(), "dd/MM/yyyy");
                    cal.set(yearSelected, monthOfYear, dayOfMonthSelected);
                    String calSelected = DateUtils.formatDate(cal.getTime(), "dd/MM/yyyy");

                    String selectedDate = "Ngày " + calSelected;
                    tvDate.setText(selectedDate);
                    AttendanceController attendanceController = new AttendanceController();

                    attendanceController.getAttendancesByDate(cal.getTime(), new OnCompleteListener<List<Attendance>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<Attendance>> task) {
                            if (task.isSuccessful()) {
                                attendanceList = task.getResult();
                                if (attendanceAdapter != null) {
                                    attendanceAdapter.clear(); // Xóa dữ liệu cũ trong adapter
                                    attendanceAdapter.addAll(attendanceList); // Thêm dữ liệu mới vào adapter
                                    attendanceAdapter.notifyDataSetChanged(); // Cập nhật lại ListView
                                }
                            } else {
                                // Xử lý lỗi
                            }
                        }
                    });

                },
                year,
                month,
                dayOfMonth
        );

        // Hiển thị DatePickerDialog
        datePickerDialog.show();
    }
}