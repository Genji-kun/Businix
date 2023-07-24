package com.example.businix.activities.employee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.businix.R;
import com.example.businix.controllers.AttendanceController;
import com.example.businix.models.Attendance;
import com.example.businix.ui.CustomDialog;
import com.example.businix.utils.GPSManager;

import android.Manifest;

import java.util.Calendar;

public class TimeAttendanceActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1234;
    private LinearLayout btnCheckIn;
    private AttendanceController attendanceController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_attendance);
        //10.8170599,106.6768298
        GPSManager gpsManager = new GPSManager(this);
        attendanceController = new AttendanceController();

        btnCheckIn = findViewById(R.id.btn_check_in);
        btnCheckIn.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            } else {
                gpsManager.setOnLocationCheckListener(isAtCompany -> {
                    if (isAtCompany) {
                        saveAttandanceAtCheckIn();
                    } else {
                        CustomDialog dialog = new CustomDialog(this, R.layout.custom_dialog_2);
                        dialog.show();
                        dialog.setQuestion("Thông báo");
                        dialog.setMessage("GPS của bạn hiện không phải ở công ty, không thể chấm công!");
                    }
                });

                gpsManager.checkInAtCompany();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GPSManager gpsManager = new GPSManager(this);
                gpsManager.setOnLocationCheckListener(isAtCompany -> {
                    if (isAtCompany) {
                        saveAttandanceAtCheckIn();
                    } else {
                        CustomDialog dialog = new CustomDialog(this, R.layout.custom_dialog_2);
                        dialog.show();
                        dialog.setQuestion("Thông báo");
                        dialog.setMessage("GPS của bạn hiện không phải ở công ty, không thể chấm công!");
                    }
                });
                gpsManager.checkInAtCompany();
            } else {
                CustomDialog dialog = new CustomDialog(this, R.layout.custom_dialog_2);
                dialog.show();
                dialog.setQuestion("Không thể chấm công");
                dialog.setMessage("Chúng tôi không thể chấm công khi bạn không cung cấp GPS, vui lòng thử lại");
            }
        }
    }

    private void saveAttandanceAtCheckIn() {
        Attendance attendance = new Attendance();
        try {
            Calendar cal = Calendar.getInstance();
            attendance.setCheckInTime(cal.getTime());
        } catch (Exception e) {
            Log.e("Lỗi", "lỗi", e);
            return;
        }

        CustomDialog dialog = new CustomDialog(this, R.layout.custom_dialog_2);
        dialog.show();
        dialog.setQuestion("Xác nhận chấm công");
        dialog.setMessage("Bạn có chắc chắn muốn chấm công không?");
        dialog.setOnContinueClickListener((dialog1, which) -> {
            attendanceController.addAttendance(attendance, task1 -> {
                if (task1.isSuccessful()) {
                    CustomDialog notificationDialog = new CustomDialog(this, R.layout.custom_dialog_2);
                    notificationDialog.show();
                    notificationDialog.setQuestion("Thông báo");
                    notificationDialog.setMessage("Chấm công thành công");
                } else {
                    CustomDialog notificationDialog = new CustomDialog(this, R.layout.custom_dialog_2);
                    notificationDialog.show();
                    notificationDialog.setQuestion("Thông báo");
                    notificationDialog.setMessage("Chấm công không thành công");
                }
            });
        });
    }

}