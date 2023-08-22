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
import com.example.businix.controllers.EmployeeController;
import com.example.businix.controllers.LeaveRequestController;
import com.example.businix.controllers.LeaveRequestDetailController;
import com.example.businix.fragments.admin.TimePickerFragment;
import com.example.businix.models.Attendance;
import com.example.businix.models.Employee;
import com.example.businix.models.LeaveRequest;
import com.example.businix.models.LeaveRequestDetail;
import com.example.businix.models.LeaveRequestStatus;
import com.example.businix.ui.CustomDialog;
import com.example.businix.utils.DateUtils;
import com.example.businix.utils.MyFindListener;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AdminEditAttendanceActivity extends AppCompatActivity {
    private TextInputEditText inputCheckIn, inputCheckOut;
    private ImageView btnBack;
    private LinearLayout btnEdit;
    private TextView tvBtnEdit;
    private Context context;
    private ProgressBar progressBar;
    private AttendanceController attendanceController;
    private LeaveRequestController leaveRequestController;
    private LeaveRequestDetailController detailController;
    private Date checkInTime, checkOutTime;
    private Attendance attendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_attendance);

        inputCheckIn = findViewById(R.id.input_check_in);
        inputCheckOut = findViewById(R.id.input_check_out);
        progressBar = findViewById(R.id.progress_bar);
        tvBtnEdit = findViewById(R.id.tv_btn_edit);
        btnBack = findViewById(R.id.btn_back);
        attendanceController = new AttendanceController();
        leaveRequestController = new LeaveRequestController();
        detailController = new LeaveRequestDetailController();

        attendanceController.getAttendanceById(getIntent().getStringExtra("attendanceId"), new MyFindListener() {
            @Override
            public void onFoundSuccess(Object object) {
                attendance = (Attendance) object;
                checkInTime = attendance.getCheckInTime();
                checkOutTime = attendance.getCheckOutTime();
                inputCheckIn.setText(DateUtils.formatDate(checkInTime, "HH:mm"));
                inputCheckOut.setText(DateUtils.formatDate(checkOutTime, "HH:mm"));
            }

            @Override
            public void onNotFound() {
                finish();
            }

            @Override
            public void onFail() {
                finish();
            }
        });

        inputCheckIn.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                String checkIn = inputCheckIn.getText().toString();
                String[] CheckInTimeParts = checkIn.split(":");
                int checkInHour = Integer.parseInt(CheckInTimeParts[0]);
                int checkInMinute = Integer.parseInt(CheckInTimeParts[1]);
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
                String checkout = inputCheckOut.getText().toString();
                String[] checkOutTimeParts = checkout.split(":");
                int checkOutHour = Integer.parseInt(checkOutTimeParts[0]);
                int checkOutMinute = Integer.parseInt(checkOutTimeParts[1]);
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
            attend.setEmployee(attend.getEmployee());

            calendar.setTime(checkOutTime);
            calendar.set(Calendar.HOUR_OF_DAY, checkOutH);
            calendar.set(Calendar.MINUTE, checkOutM);
            attend.setCheckOutTime(calendar.getTime());
            checkTime(attend);
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

    private void checkTime(Attendance att) {
        Calendar calCheck = Calendar.getInstance();
        calCheck.setTime(att.getCheckInTime());
        calCheck.set(Calendar.MINUTE, 0);
        calCheck.set(Calendar.SECOND, 0);
        calCheck.set(Calendar.MILLISECOND, 0);
        att.setLate(0.0);
        att.setOvertime(0.0);
        CustomDialog dialog = new CustomDialog(this, R.layout.custom_dialog_2);
        dialog.show();
        dialog.setQuestion("Xác nhận thay đổi");
        dialog.setMessage("Bạn có chắc chắn muốn thay đổi không?");
        dialog.setOnContinueClickListener((dialog1, which) -> {
            leaveRequestController.getLeaveRequestOfEmployeeOverlapping(calCheck.getTime(), calCheck.getTime(), LeaveRequestStatus.ACCEPT, att.getEmployee(), task -> {
                if (task.isSuccessful()) {
                    List<LeaveRequest> requests = task.getResult();
                    if (requests.size() > 0) {
                        detailController.getDetailsByTime(calCheck.getTime(), calCheck.getTime(), requests, task1 -> {
                            if (task1.isSuccessful()) {
                                if (task1.getResult().size() > 0) {
                                    LeaveRequestDetail detail = task1.getResult().get(0);
                                    if (detail.getShift().equals("Cả ngày")) {
                                        CustomDialog errorDialog = new CustomDialog(this, R.layout.custom_dialog_2);
                                        errorDialog.show();
                                        errorDialog.setQuestion("Không thể thay đổi giờ chấm công");
                                        errorDialog.setMessage("Nhân viên có lịch nghỉ cả ngày");
                                        return;
                                    } else if (detail.getShift().equals("Ca sáng")) {
                                        calCheck.set(Calendar.HOUR_OF_DAY, 15);
                                        if (att.getCheckInTime().after(calCheck.getTime())) {
                                            CustomDialog errorDialog = new CustomDialog(this, R.layout.custom_dialog_2);
                                            errorDialog.show();
                                            errorDialog.setQuestion("Không thể thay đổi giờ chấm công");
                                            errorDialog.setMessage("Nhân viên không được trễ hơn 2 tiếng");
                                            return;
                                        }
                                        calCheck.set(Calendar.HOUR_OF_DAY, 12);
                                        if (att.getCheckInTime().before(calCheck.getTime())) {
                                            CustomDialog errorDialog = new CustomDialog(this, R.layout.custom_dialog_2);
                                            errorDialog.show();
                                            errorDialog.setQuestion("Không thể thay đổi giờ chấm công");
                                            errorDialog.setMessage("Hãy thay đổi giờ check in ca chiều sau 12:00 pm");
                                            return;
                                        }
                                        calCheck.set(Calendar.HOUR_OF_DAY, 13);
                                        if (att.getCheckInTime().before(calCheck.getTime())) {
                                            att.setCheckInTime(calCheck.getTime());
                                        } else {
                                            Double lateHours = DateUtils.getDiffHours(att.getCheckInTime(), calCheck.getTime());
                                            if (lateHours * 60 > 15) {
                                                att.setLate(lateHours);
                                            }
                                        }
                                    } else {
                                        calCheck.set(Calendar.HOUR_OF_DAY, 8);
                                        if (att.getCheckInTime().before(calCheck.getTime())) {
                                            att.setCheckInTime(calCheck.getTime());
                                        } else {
                                            Double lateHours = DateUtils.getDiffHours(att.getCheckInTime(), calCheck.getTime());
                                            if (lateHours * 60 > 15) {
                                                att.setLate(lateHours);
                                            }
                                        }
                                        calCheck.set(Calendar.HOUR_OF_DAY, 13);
                                        if (att.getCheckOutTime().after(calCheck.getTime())) {
                                            CustomDialog errorDialog = new CustomDialog(this, R.layout.custom_dialog_2);
                                            errorDialog.show();
                                            errorDialog.setQuestion("Không thể thay đổi giờ chấm công");
                                            errorDialog.setMessage("Nhân viên không được muộn giờ check out cho ca sáng, hãy phải thay đổi giờ check out trước 13:00 pm");
                                            return;
                                        }
                                    }
                                    calCheck.set(Calendar.HOUR_OF_DAY, 17);
                                    if (calCheck.getTime().after(calCheck.getTime())) {
                                        calCheck.set(Calendar.HOUR_OF_DAY, 21);
                                        if (att.getCheckOutTime().after(calCheck.getTime()))
                                            att.setCheckOutTime(calCheck.getTime());
                                        Double overHours = DateUtils.getDiffHours(att.getCheckOutTime(), calCheck.getTime());
                                        if (overHours*60 > 30) {
                                            att.setOvertime(overHours);
                                        }
                                    }
                                    updateAttendance(att);
                                }
                            }
                        });
                    } else {
                        calCheck.set(Calendar.HOUR_OF_DAY, 8);
                        if (att.getCheckInTime().before(calCheck.getTime())) {
                            att.setCheckInTime(calCheck.getTime());
                        } else {
                            Double lateHours = DateUtils.getDiffHours(att.getCheckInTime(), calCheck.getTime());
                            if (lateHours * 60 > 15) {
                                att.setLate(lateHours);
                            }
                        }
                        calCheck.set(Calendar.HOUR_OF_DAY, 17);
                        if (att.getCheckOutTime().after(calCheck.getTime())) {
                            calCheck.set(Calendar.HOUR_OF_DAY, 21);
                            if (att.getCheckOutTime().after(calCheck.getTime()))
                                att.setCheckOutTime(calCheck.getTime());
                            Double overHours = DateUtils.getDiffHours(att.getCheckOutTime(), calCheck.getTime());
                            if (overHours*60 > 30) {
                                att.setOvertime(overHours);
                            }
                        }
                        updateAttendance(att);
                    }

                }
            });
        });
    }

    private void updateAttendance(Attendance attend) {
        attendanceController.updateAttendance(attendance.getId(), attend, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AdminEditAttendanceActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}