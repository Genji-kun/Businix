package com.example.businix.activities.employee;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.businix.R;
import com.example.businix.controllers.AttendanceController;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.controllers.LeaveRequestController;
import com.example.businix.controllers.LeaveRequestDetailController;
import com.example.businix.models.Attendance;
import com.example.businix.models.LeaveRequest;
import com.example.businix.models.LeaveRequestDetail;
import com.example.businix.models.LeaveRequestStatus;
import com.example.businix.ui.ActionBar;
import com.example.businix.ui.CustomDialog;
import com.example.businix.utils.DateUtils;
import com.example.businix.utils.GPSManager;
import com.example.businix.utils.LoginManager;
import com.example.businix.utils.MyFindListener;
import com.google.firebase.firestore.DocumentReference;

import android.Manifest;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TimeAttendanceActivity extends ActionBar {
    private static final int REQUEST_LOCATION_PERMISSION = 1234;
    private LinearLayout btnCheckIn, btnCheckOut, btnReload, btnChangeDate;

    private AttendanceController attendanceController;

    private Attendance availableAttend;
    private MyFindListener attendanceTodayLister, attendanceAnotherLister;
    private TextView textDate;

    private DocumentReference employeeRef;
    private LeaveRequestController leaveRequestController;
    private LeaveRequestDetailController leaveRequestDetailController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_attendance);

        EmployeeController ec = new EmployeeController();
        leaveRequestController = new LeaveRequestController();
        leaveRequestDetailController = new LeaveRequestDetailController();
        LoginManager lm = new LoginManager(this);
        employeeRef = ec.getEmployeeRef(lm.getLoggedInUserId());

        toolbar = findViewById(R.id.toolbar);
        setSupportMyActionBar("Chấm công", true, false);
        //10.8170599,106.6768298
        GPSManager gpsManager = new GPSManager(this);
        gpsManager.setOnLocationCheckListener(isAtCompany -> {
            if (isAtCompany) {
                checkOut();
            } else {
                CustomDialog dialog = new CustomDialog(this, R.layout.custom_dialog_2);
                dialog.show();
                dialog.setQuestion("Thông báo");
                dialog.setMessage("GPS của bạn hiện không phải ở công ty, không thể chấm công!");
            }
        });
        attendanceController = new AttendanceController();
        textDate = findViewById(R.id.text_date);

        btnCheckIn = findViewById(R.id.btn_check_in);
        btnCheckOut = findViewById(R.id.btn_check_out);
        disableMyButton(btnCheckOut);
        disableMyButton(btnCheckIn);
        setAttendanceTodayListener();
        setAttendanceAnotherLister();


        Calendar cal = Calendar.getInstance();
        textDate.setText("Ngày " + DateUtils.formatDate(cal.getTime(), "dd-MM-yyyy"));
        attendanceController.getAttandanceByMonth(cal.getTime(), employeeRef, attendanceTodayLister);

        btnCheckIn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            } else {
                if (isGpsEnabled()) {
                    gpsManager.setOnLocationCheckListener(isAtCompany -> {
                        if (isAtCompany) {
                            checkIn();
                        } else {
                            CustomDialog dialog = new CustomDialog(this, R.layout.custom_dialog_2);
                            dialog.show();
                            dialog.setQuestion("Thông báo");
                            dialog.setMessage("GPS của bạn hiện không phải ở công ty, không thể chấm công!");
                        }
                    });

                    gpsManager.checkInAtCompany();
                } else {
                    showGpsAlertDialog();
                }
            }
        });

        btnCheckOut.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            } else {
                if (isGpsEnabled()) {
                    gpsManager.setOnLocationCheckListener(isAtCompany -> {
                        if (isAtCompany) {
                            checkOut();
                        } else {
                            CustomDialog dialog = new CustomDialog(this, R.layout.custom_dialog_2);
                            dialog.show();
                            dialog.setQuestion("Thông báo");
                            dialog.setMessage("GPS của bạn hiện không phải ở công ty, không thể chấm công!");
                        }
                    });

                    gpsManager.checkInAtCompany();
                }
                showGpsAlertDialog();

            }
        });

        btnReload = findViewById(R.id.btn_reload);
        btnReload.setOnClickListener(v -> {
            btnReload.setEnabled(false);
            btnReload.setBackgroundTintList(AppCompatResources.getColorStateList(this, R.color.dark_gray));
            reload();
        });

        btnChangeDate = findViewById(R.id.btn_change_date);
        btnChangeDate.setOnClickListener(v -> {
            showDatePickerDialog();
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
                        if (availableAttend == null)
                            checkIn();
                        else
                            checkOut();
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

    private void checkIn() {
        Attendance attendance = new Attendance();
        Calendar checkInCal = Calendar.getInstance();
        attendance.setCheckInTime(checkInCal.getTime());
        attendance.setEmployee(employeeRef);
        checkInCal.set(Calendar.MINUTE, 0);
        checkInCal.set(Calendar.SECOND, 0);
        checkInCal.set(Calendar.MILLISECOND, 0);

        CustomDialog dialog = new CustomDialog(this, R.layout.custom_dialog_2);
        dialog.show();
        dialog.setQuestion("Xác nhận chấm công");
        dialog.setMessage("Bạn có chắc chắn muốn chấm công không?");
        dialog.setOnContinueClickListener((dialog1, which) -> {
            Calendar nowCal = Calendar.getInstance();
            leaveRequestController.getLeaveRequestOverlapping(nowCal.getTime(), nowCal.getTime(), employeeRef, task -> {
                if (task.isSuccessful()) {
                    List<LeaveRequest> requests = task.getResult();
                    requests = requests.stream().filter(request -> request.getStatus() == LeaveRequestStatus.ACCEPT)
                            .collect(Collectors.toList());
                    if (requests.size() > 0) {
                        leaveRequestDetailController.getDetailsByTime(nowCal.getTime(), nowCal.getTime(), requests, task1 -> {
                            if (task1.isSuccessful()) {
                                if (task1.getResult().size() > 0) {
                                    LeaveRequestDetail detail = task1.getResult().get(0);
                                    if (detail.getShift().equals("Cả ngày")) {
                                        CustomDialog errorDialog = new CustomDialog(this, R.layout.custom_dialog_2);
                                        errorDialog.show();
                                        errorDialog.setQuestion("Không thể chấm công");
                                        errorDialog.setMessage("Bạn có lịch nghỉ cả ngày hôm nay");
                                        return;
                                    } else if (detail.getShift().equals("Ca sáng")) {
                                        checkInCal.set(Calendar.HOUR_OF_DAY, 13);
                                        if (nowCal.getTime().before(checkInCal.getTime())) {
                                            CustomDialog errorDialog = new CustomDialog(this, R.layout.custom_dialog_2);
                                            errorDialog.show();
                                            errorDialog.setQuestion("Không thể chấm công");
                                            errorDialog.setMessage("Bạn có lịch nghỉ ca sáng hôm nay, hãy check in từ 13 giờ");
                                            return;
                                        }
                                        checkInCal.set(Calendar.HOUR_OF_DAY, 15);
                                        if (nowCal.getTime().after(checkInCal.getTime())) {
                                            CustomDialog errorDialog = new CustomDialog(this, R.layout.custom_dialog_2);
                                            errorDialog.show();
                                            errorDialog.setQuestion("Không thể chấm công");
                                            errorDialog.setMessage("Bạn đã trễ hơn 2 tiếng");
                                            return;
                                        }
                                    }
                                }
                                saveCheckIn(attendance, checkInCal);

                            }
                        });
                    }
                    else {
                        saveCheckIn(attendance, checkInCal);
                    }
                }
            });
        });
    }

    private void saveCheckIn(Attendance attendance, Calendar checkInCal) {
        checkInCal.set(Calendar.HOUR_OF_DAY, 10);
        if (attendance.getCheckInTime().after(checkInCal.getTime())) {
            CustomDialog errorDialog = new CustomDialog(this, R.layout.custom_dialog_2);
            errorDialog.show();
            errorDialog.setQuestion("Không thể chấm công");
            errorDialog.setMessage("Bạn đã trễ hơn 2 tiếng");
            return;
        }
        attendanceController.addAttendance(attendance, task2 -> {
            if (task2.isSuccessful()) {
                CustomDialog notificationDialog = new CustomDialog(this, R.layout.custom_dialog_2);
                notificationDialog.show();
                notificationDialog.setQuestion("Thông báo");
                notificationDialog.setMessage("Chấm công thành công");
                disableMyButton(btnCheckIn);
                enableMyButton(btnCheckOut);
                setStatusTimeLineItem(R.id.part_check_in, DateUtils.formatDate(attendance.getCheckInTime(), "HH:mm"), false);
            } else {
                CustomDialog notificationDialog = new CustomDialog(this, R.layout.custom_dialog_2);
                notificationDialog.show();
                notificationDialog.setQuestion("Thông báo");
                notificationDialog.setMessage("Chấm công không thành công");
            }
        });
    }

    private void saveCheckOut(Attendance attendance, Calendar checkOutCal) {
        checkOutCal.set(Calendar.HOUR_OF_DAY, 21);
        if (attendance.getCheckOutTime().after(checkOutCal.getTime())) {
            attendance.setCheckOutTime(checkOutCal.getTime());
        }
        attendanceController.updateAttendance(availableAttend.getId(), attendance, task2 -> {
            if (task2.isSuccessful()) {
                CustomDialog notificationDialog = new CustomDialog(this, R.layout.custom_dialog_2);
                notificationDialog.show();
                notificationDialog.setQuestion("Thông báo");
                notificationDialog.setMessage("Chấm công thành công");
                disableMyButton(btnCheckOut);
                setStatusTimeLineItem(R.id.part_check_out, DateUtils.formatDate(attendance.getCheckOutTime(), "HH:mm"), false);
                availableAttend.setCheckOutTime(attendance.getCheckOutTime());
                loadBreakOverTime(availableAttend, true);
            } else {
                CustomDialog notificationDialog = new CustomDialog(this, R.layout.custom_dialog_2);
                notificationDialog.show();
                notificationDialog.setQuestion("Thông báo");
                notificationDialog.setMessage("Chấm công không thành công");
            }
        });
    }


    private void checkOut() {
        Calendar checkOutCal = Calendar.getInstance();
        checkOutCal.set(Calendar.MINUTE, 0);
        checkOutCal.set(Calendar.SECOND, 0);
        checkOutCal.set(Calendar.MILLISECOND, 0);

        Attendance attendance = new Attendance();
        attendance.setCheckOutTime(checkOutCal.getTime());

        CustomDialog dialog = new CustomDialog(this, R.layout.custom_dialog_2);
        dialog.show();
        dialog.setQuestion("Xác nhận chấm công");
        dialog.setMessage("Bạn có chắc chắn muốn chấm công không?");
        dialog.setOnContinueClickListener((dialog1, which) -> {
            Calendar nowCal = Calendar.getInstance();
            leaveRequestController.getLeaveRequestOverlapping(nowCal.getTime(), nowCal.getTime(), employeeRef, task -> {
                if (task.isSuccessful()) {
                    List<LeaveRequest> requests = task.getResult();
                    requests = requests.stream().filter(request -> request.getStatus() == LeaveRequestStatus.ACCEPT)
                            .collect(Collectors.toList());
                    if (requests.size() > 0) {
                        leaveRequestDetailController.getDetailsByTime(nowCal.getTime(), nowCal.getTime(), requests, task1 -> {
                            if (task1.isSuccessful()) {

                                if (task1.getResult().size() > 0) {
                                    LeaveRequestDetail detail = task1.getResult().get(0);

                                    if (detail.getShift().equals("Ca chiều")) {
                                        checkOutCal.set(Calendar.HOUR_OF_DAY, 13);
                                        if (nowCal.getTime().after(checkOutCal.getTime())) {
                                            CustomDialog errorDialog = new CustomDialog(this, R.layout.custom_dialog_2);
                                            errorDialog.show();
                                            errorDialog.setQuestion("Không thể chấm công");
                                            errorDialog.setMessage("Bạn đã muộn giờ check out cho ca sáng, bạn phải check out trước 13 giờ");
                                            return;
                                        }
                                    }
                                }
                                saveCheckOut(attendance, checkOutCal);
                            }
                        });
                    }
                    else {
                        saveCheckOut(attendance, checkOutCal);
                    }
                }

            });
        });
    }

    private void enableMyButton(LinearLayout btn) {
        TextView text = (TextView) btn.getChildAt(0);
        ImageView img = (ImageView) btn.getChildAt(1);
        text.setTextColor(getResources().getColor(R.color.white, null));
        img.setImageTintList(AppCompatResources.getColorStateList(this, R.color.white));
        btn.setBackgroundTintList(AppCompatResources.getColorStateList(this, R.color.medium_purple));
        btn.setElevation(changePdToPx(5));
        btn.setEnabled(true);
    }

    private void disableMyButton(LinearLayout btn) {
        TextView text = (TextView) btn.getChildAt(0);
        ImageView img = (ImageView) btn.getChildAt(1);
        text.setTextColor(getResources().getColor(R.color.dark_gray, null));
        img.setImageTintList(AppCompatResources.getColorStateList(this, R.color.dark_gray));
        btn.setBackgroundTintList(AppCompatResources.getColorStateList(this, R.color.gray));
        btn.setElevation(0);
        btn.setEnabled(false);
    }

    private void setAttendanceTodayListener() {
        attendanceTodayLister = new MyFindListener() {
            @Override
            public void onFoundSuccess(Object object) {
                btnReload.setEnabled(true);
                btnReload.setBackgroundTintList(AppCompatResources.getColorStateList(TimeAttendanceActivity.this, R.color.medium_purple));
                disableMyButton(btnCheckIn);
                availableAttend = (Attendance) object;
                setStatusTimeLineItem(R.id.part_check_in, DateUtils.formatDate(availableAttend.getCheckInTime(), "HH:mm"), false);
                if (availableAttend.getCheckOutTime() != null) {
                    disableMyButton(btnCheckOut);
                    setStatusTimeLineItem(R.id.part_check_out, DateUtils.formatDate(availableAttend.getCheckOutTime(), "HH:mm"), false);
                    loadBreakOverTime(availableAttend, true);
                } else {
                    ((TextView) findViewById(R.id.overtime_hours)).setText("0 hours");
                    setStatusTimeLineItem(R.id.part_check_out, "...", true);
                    enableMyButton(btnCheckOut);
                    Calendar cal = Calendar.getInstance();
                    Date now = cal.getTime();
                    cal.set(Calendar.HOUR_OF_DAY, 12);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    if (now.after(cal.getTime())) {
                        setStatusTimeLineItem(R.id.part_break, "12:00", false);
                    } else {
                        setStatusTimeLineItem(R.id.part_break, "12:00", true);
                    }
                    cal.set(Calendar.HOUR_OF_DAY, 13);
                    if (now.after(cal.getTime())) {
                        setStatusTimeLineItem(R.id.part_after_break, "13:00", false);
                    } else {
                        setStatusTimeLineItem(R.id.part_after_break, "13:00", true);
                    }
                    (findViewById(R.id.part_break)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.part_after_break)).setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onNotFound() {
                enableMyButton(btnCheckIn);
                disableMyButton(btnCheckOut);
                setStatusTimeLineItem(R.id.part_check_in, "...", true);
                setStatusTimeLineItem(R.id.part_check_out, "...", true);
                (findViewById(R.id.part_break)).setVisibility(View.VISIBLE);
                (findViewById(R.id.part_after_break)).setVisibility(View.VISIBLE);
                setStatusTimeLineItem(R.id.part_break, "12:00", true);
                setStatusTimeLineItem(R.id.part_after_break, "13:00", true);
                btnReload.setBackgroundTintList(AppCompatResources.getColorStateList(TimeAttendanceActivity.this, R.color.medium_purple));
                btnReload.setEnabled(true);
            }

            @Override
            public void onFail() {
                CustomDialog dialog = new CustomDialog(TimeAttendanceActivity.this, R.layout.custom_dialog_2);
                dialog.show();
                dialog.setQuestion("ERROR");
                dialog.setMessage("Đã có lỗi xảy ra, kiểm tra đường truyền và thử lại sau!");
                dialog.setOnCancelClickListener((dialog1, which) -> {
                    finish();
                });
                dialog.setOnContinueClickListener((dialog1, which) -> {
                    finish();
                });
            }


        };
    }

    private void setAttendanceAnotherLister() {
        attendanceAnotherLister = new MyFindListener() {
            @Override
            public void onFoundSuccess(Object object) {
                disableMyButton(btnCheckOut);
                disableMyButton(btnCheckIn);
                btnReload.setEnabled(true);
                btnReload.setBackgroundTintList(AppCompatResources.getColorStateList(TimeAttendanceActivity.this, R.color.medium_purple));
                Attendance attendance = (Attendance) object;
                setStatusBackTimeItem(R.id.part_check_in, DateUtils.formatDate(attendance.getCheckInTime(), "HH:mm"), true);
                if (attendance.getCheckOutTime() != null) {
                    setStatusBackTimeItem(R.id.part_check_out, DateUtils.formatDate(attendance.getCheckOutTime(), "HH:mm"), true);
                    loadBreakOverTime(attendance, false);
                } else {
                    setStatusBackTimeItem(R.id.part_check_out, "...", false);
                    (findViewById(R.id.part_break)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.part_after_break)).setVisibility(View.VISIBLE);
                    setStatusBackTimeItem(R.id.part_break, "12:00", null);
                    setStatusBackTimeItem(R.id.part_after_break, "13:00", null);
                    ((TextView) findViewById(R.id.overtime_hours)).setText("0 hours");
                }
            }

            @Override
            public void onNotFound() {
                disableMyButton(btnCheckOut);
                disableMyButton(btnCheckIn);
                setStatusBackTimeItem(R.id.part_check_in, "...", false);
                setStatusBackTimeItem(R.id.part_check_out, "...", false);
                setStatusBackTimeItem(R.id.part_break, "12:00", false);
                setStatusBackTimeItem(R.id.part_after_break, "13:00", false);
                (findViewById(R.id.part_break)).setVisibility(View.VISIBLE);
                (findViewById(R.id.part_after_break)).setVisibility(View.VISIBLE);
                btnReload.setBackgroundTintList(AppCompatResources.getColorStateList(TimeAttendanceActivity.this, R.color.medium_purple));
                btnReload.setEnabled(true);
                ((TextView) findViewById(R.id.overtime_hours)).setText("0 hours");
            }

            @Override
            public void onFail() {
                attendanceTodayLister.onFail();
            }
        };
    }

    private void reload() {
        if (availableAttend != null) {
            attendanceController.getAttandanceById(availableAttend.getId(), attendanceTodayLister);
            Calendar cal = Calendar.getInstance();
            textDate.setText("Ngày " + DateUtils.formatDate(cal.getTime(), "dd-MM-yyyy"));
        } else {
            Calendar cal = Calendar.getInstance();
            attendanceController.getAttandanceByMonth(cal.getTime(), employeeRef, attendanceTodayLister);
        }
    }

    private void setStatusTimeLineItem(int idPart, String time, Boolean isEnable) {
        LinearLayout layout = findViewById(idPart);


        int colorBg;
        int colorItem;
        LinearLayout block = (LinearLayout) layout.getChildAt(2);
        ImageView mark;
        if (idPart == R.id.part_check_in || idPart == R.id.part_check_out)
            mark = (ImageView) ((RelativeLayout) layout.getChildAt(0)).getChildAt(3);
        else mark = (ImageView) ((RelativeLayout) layout.getChildAt(0)).getChildAt(2);
        if (!isEnable) {
            colorBg = R.color.light_gray;
            colorItem = R.color.dark_gray;
            block.setElevation(0);
            mark.setImageDrawable(getDrawable(R.drawable.ic_check));
        } else {
            colorBg = R.color.white;
            colorItem = R.color.medium_purple;
            block.setElevation(changePdToPx(3));
            mark.setImageDrawable(null);
        }
        block.setBackgroundTintList(AppCompatResources.getColorStateList(this, colorBg));
        ImageView icon = (ImageView) block.getChildAt(1);
        icon.setImageTintList(AppCompatResources.getColorStateList(this, colorItem));

        TextView text = (TextView) ((LinearLayout) block.getChildAt(0)).getChildAt(1);
        if (text != null) {
            text.setText(time);
        }
    }

    private void setStatusBackTimeItem(int idPart, String time, Boolean isValid) {
        LinearLayout layout = findViewById(idPart);
        int colorBg = R.color.light_gray;
        int colorItem = R.color.dark_gray;
        LinearLayout block = (LinearLayout) layout.getChildAt(2);
        block.setElevation(0);
        ImageView mark;
        if (idPart == R.id.part_check_in || idPart == R.id.part_check_out)
            mark = (ImageView) ((RelativeLayout) layout.getChildAt(0)).getChildAt(3);
        else mark = (ImageView) ((RelativeLayout) layout.getChildAt(0)).getChildAt(2);
        if (isValid == false) {
            mark.setImageDrawable(getDrawable(R.drawable.ic_x));
        } else if (isValid == null) {
            mark.setImageDrawable(null);
        } else {
            mark.setImageDrawable(getDrawable(R.drawable.ic_check));
        }
        block.setBackgroundTintList(AppCompatResources.getColorStateList(this, colorBg));
        ImageView icon = (ImageView) block.getChildAt(1);
        icon.setImageTintList(AppCompatResources.getColorStateList(this, colorItem));

        TextView text = (TextView) ((LinearLayout) block.getChildAt(0)).getChildAt(1);
        if (text != null) {
            text.setText(time);
        }
    }

    private float changePdToPx(int pd) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pd, getResources().getDisplayMetrics());
    }

    private void showDatePickerDialog() {
        // Lấy ngày hiện tại để đặt làm giá trị mặc ring cho DatePickerDialog
        int year;
        int month;
        int dayOfMonth;
        String dateString = textDate.getText().toString().replace("Ngày ", "");

        String[] endDateArr = dateString.split("-");
        year = Integer.parseInt(endDateArr[2]);
        month = Integer.parseInt(endDateArr[1]) - 1;
        dayOfMonth = Integer.parseInt(endDateArr[0]);
        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, yearSelected, monthOfYear, dayOfMonthSelected) -> {
                    // Lắng nghe sự kiện chọn ngày và cập nhật giá trị cho EditText tương ứng
                    Calendar cal = Calendar.getInstance();
                    String calDate = DateUtils.formatDate(cal.getTime(), "dd-MM-yyyy");
                    cal.set(yearSelected, monthOfYear, dayOfMonthSelected);
                    String calSelected = DateUtils.formatDate(cal.getTime(), "dd-MM-yyyy");
                    if (calDate.equals(calSelected)) {
                        reload();
                    } else {
                        String selectedDate = "Ngày " + calSelected;
                        textDate.setText(selectedDate);
                        attendanceController.getAttandanceByMonth(cal.getTime(), employeeRef, attendanceAnotherLister);
                    }

                },
                year,
                month,
                dayOfMonth
        );

        // Hiển thị DatePickerDialog
        datePickerDialog.show();
    }

    private void loadBreakOverTime(Attendance attendance, boolean isToday) {
        Calendar cal = Calendar.getInstance();
        if (!isToday) {
            cal.setTime(attendance.getCheckInTime());
        }
        cal.set(Calendar.HOUR_OF_DAY, 13);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        if (attendance.getCheckInTime().after(cal.getTime())) {
            (findViewById(R.id.part_break)).setVisibility(View.GONE);
            (findViewById(R.id.part_after_break)).setVisibility(View.GONE);
        } else {
            if (attendance.getCheckOutTime().after(cal.getTime())) {
                (findViewById(R.id.part_break)).setVisibility(View.VISIBLE);
                (findViewById(R.id.part_after_break)).setVisibility(View.VISIBLE);
                if (isToday) {
                    setStatusTimeLineItem(R.id.part_break, "12:00", false);
                    setStatusTimeLineItem(R.id.part_after_break, "13:00", false);
                } else {
                    setStatusBackTimeItem(R.id.part_break, "12:00", true);
                    setStatusBackTimeItem(R.id.part_after_break, "13:00", true);
                }

            } else {
                (findViewById(R.id.part_break)).setVisibility(View.GONE);
                (findViewById(R.id.part_after_break)).setVisibility(View.GONE);
            }
        }
        cal.set(Calendar.HOUR_OF_DAY, 17);
        if (attendance.getCheckOutTime().after(cal.getTime())) {
            double hours = DateUtils.getDiffHours(attendance.getCheckOutTime(), cal.getTime());
            if (hours >= 1) {
                ((TextView) findViewById(R.id.overtime_hours)).setText(hours + " hours");
            } else {
                ((TextView) findViewById(R.id.overtime_hours)).setText("0 hours");

            }
        } else {
            ((TextView) findViewById(R.id.overtime_hours)).setText("0 hours");
        }
    }

    private boolean isGpsEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        return false;
    }

    private void showGpsAlertDialog() {
        CustomDialog dialog = new CustomDialog(this, R.layout.custom_dialog_2);
        dialog.show();
        dialog.setTitle("Yêu cầu bật GPS");
        dialog.setMessage("GPS của bạn đã tắt. Vui lòng bật GPS để có thể chấm công.");
        dialog.setOnContinueClickListener((dialog1, which) -> {
            openGpsSettings();
        });
        dialog.setOnCancelClickListener((dialog1, which) -> {
            dialog1.dismiss();
        });
    }

    private void openGpsSettings() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

}