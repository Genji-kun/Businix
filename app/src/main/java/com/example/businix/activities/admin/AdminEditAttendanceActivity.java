package com.example.businix.activities.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.businix.R;
import com.example.businix.controllers.AttendanceController;
import com.example.businix.fragments.admin.TimePickerFragment;
import com.example.businix.models.Attendance;
import com.example.businix.utils.DateUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class AdminEditAttendanceActivity extends AppCompatActivity {
    private TextInputEditText inputCheckIn, inputCheckOut;
    private ImageView btnBack;
    private LinearLayout btnEdit;
    private TextView tvBtnEdit;
    private Context context;
    private ProgressBar progressBar;
    private AttendanceController attendanceController;
    private Date checkInTime, checkOutTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_attendance);

        inputCheckIn = findViewById(R.id.input_check_in);
        inputCheckOut = findViewById(R.id.input_check_out);
        progressBar = findViewById(R.id.progress_bar);
        tvBtnEdit = findViewById(R.id.tv_btn_edit);
        btnBack = findViewById(R.id.btn_back);


        try {
            checkInTime = DateUtils.changeStringToDate(getIntent().getStringExtra("checkIn"), "dd/MM/yyyy HH:mm:ss");
            checkOutTime = DateUtils.changeStringToDate(getIntent().getStringExtra("checkOut"), "dd/MM/yyyy HH:mm:ss");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        inputCheckIn.setText(DateUtils.formatDate(checkInTime, "HH:mm"));
        inputCheckOut.setText(DateUtils.formatDate(checkOutTime, "HH:mm"));

        String[] checkInBefore = inputCheckIn.getText().toString().split(":");
        int checkInHour = Integer.parseInt(checkInBefore[0]);
        int checkInMinute = Integer.parseInt(checkInBefore[1]);
        String[] checkOutBefore = inputCheckOut.getText().toString().split(":");
        int checkOutHour = Integer.parseInt(checkOutBefore[0]);
        int checkOutMinute = Integer.parseInt(checkOutBefore[1]);

        inputCheckIn.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AdminEditAttendanceActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                inputCheckIn.setText(hourOfDay + ":" + minute);
                            }
                        }, checkInHour, checkInMinute, false);
                timePickerDialog.show();
            }
        });

        inputCheckOut.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AdminEditAttendanceActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                inputCheckOut.setText(hourOfDay + ":" + minute);
                            }
                        }, checkOutHour, checkOutMinute, false);
                timePickerDialog.show();
            }
        });

        attendanceController = new AttendanceController();
        btnEdit = findViewById(R.id.btn_edit);
        btnEdit.setOnClickListener(v -> {
            processingView();
            if (inputCheckIn.getText().toString().isBlank()) {
                normalView();
                return;
            }
            if (inputCheckOut.getText().toString().isBlank()) {
                normalView();
                return;
            }

            String[] checkInAfter = inputCheckIn.getText().toString().split(":");
            int checkInH = Integer.parseInt(checkInAfter[0]);
            int checkInM = Integer.parseInt(checkInAfter[1]);

            String[] checkOutAfter = inputCheckOut.getText().toString().split(":");
            int checkOutH = Integer.parseInt(checkOutAfter[0]);
            int checkOutM = Integer.parseInt(checkOutAfter[1]);

            Attendance attend = new Attendance();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(checkInTime);
            calendar.set(Calendar.HOUR_OF_DAY, checkInH);
            calendar.set(Calendar.MINUTE, checkInM);
            attend.setCheckInTime(calendar.getTime());
            calendar.setTime(checkOutTime);
            calendar.set(Calendar.HOUR_OF_DAY, checkOutH);
            calendar.set(Calendar.MINUTE, checkOutM);
            attend.setCheckOutTime(calendar.getTime());

            attendanceController.updateAttendance(getIntent().getStringExtra("attendanceId"), attend, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(AdminEditAttendanceActivity.this,"Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    public void processingView() {
        progressBar.setVisibility(View.VISIBLE);
        tvBtnEdit.setVisibility(View.GONE);
        btnEdit.setEnabled(false);
        btnEdit.setBackgroundColor(Color.LTGRAY);
    }

    public void normalView() {
        progressBar.setVisibility(View.GONE);
        tvBtnEdit.setVisibility(View.VISIBLE);
        btnEdit.setEnabled(true);
        btnEdit.setBackgroundColor(context.getResources().getColor(R.color.light_purple));
    }

}