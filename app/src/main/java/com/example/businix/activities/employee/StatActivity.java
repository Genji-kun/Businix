package com.example.businix.activities.employee;

import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.example.businix.ui.MonthYearPickerDialog;
import com.example.businix.utils.DateUtils;
import com.example.businix.utils.LoginManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class StatActivity extends ActionBar {
    private LineChart chart;
    private TextView tvWorkHours, tvOvertimeHours, tvLateHours, tvLeaveRequests, tvMonth;
    private DocumentReference employeeRef;
    private AttendanceController attendanceController;
    private LeaveRequestController requestController;
    private LeaveRequestDetailController detailController;
    private LinearLayout btnChangeTime, layoutData;
    private ProgressBar progressBar;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        toolbar = findViewById(R.id.toolbar);
        setSupportMyActionBar("Thống kê", true, false);

        chart = findViewById(R.id.chart);
        chart.getDescription().setEnabled(false);
        layoutData = findViewById(R.id.layout_data);
        progressBar = findViewById(R.id.progress_bar);
        btnChangeTime = findViewById(R.id.btn_select_time);
        tvWorkHours = findViewById(R.id.tv_work_hours);
        tvOvertimeHours = findViewById(R.id.tv_overtime_hours);
        tvLateHours = findViewById(R.id.tv_late_hours);
        tvLeaveRequests = findViewById(R.id.tv_leave_requests);
        tvMonth = findViewById(R.id.tv_month);

        LoginManager loginManager = new LoginManager(this);
        employeeRef = (new EmployeeController()).getEmployeeRef(loginManager.getLoggedInUserId());

        attendanceController = new AttendanceController();
        requestController = new LeaveRequestController();
        detailController = new LeaveRequestDetailController();
        calendar = Calendar.getInstance();


        btnChangeTime.setOnClickListener(v -> {
            btnChangeTime.setEnabled(false);
            int yearSelected = calendar.get(Calendar.YEAR);
            int monthSelected = calendar.get(Calendar.MONTH);

            MonthYearPickerDialog monthDialog = new MonthYearPickerDialog(new MonthYearPickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(year, month, 15);
                    tvMonth.setText(DateUtils.formatDate(calendar.getTime(), "MM/yyyy"));
                    setLoading();
                    loadChart();
                    loadData(calendar.getTime());
                }

                @Override
                public void onCancel() {
                    btnChangeTime.setEnabled(true);
                }
            }, yearSelected, monthSelected);
            monthDialog.setCancelable(false);
            monthDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");
        });
        loadChart();
        loadData(calendar.getTime());
        tvMonth.setText(DateUtils.formatDate(calendar.getTime(), "MM/yyyy"));

    }

    private void loadData(Date dateStat) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateStat);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date start = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date end = cal.getTime();
        requestController.getLeaveRequestOfEmployeeOverlapping(start, end, LeaveRequestStatus.ACCEPT ,employeeRef, task -> {
            if (task.isSuccessful()) {
                List<LeaveRequest> requests = task.getResult();
                requests = requests.stream().filter(request -> request.getStatus() == LeaveRequestStatus.ACCEPT)
                        .collect(Collectors.toList());
                if (requests.size() <= 0)
                    tvLeaveRequests.setText("0");
                else {
                    detailController.getDetailsByTime(start, end, requests, task1 -> {
                        if (task1.isSuccessful()) {
                            int count = 0;
                            for (LeaveRequestDetail detail : task1.getResult()) {
                                if (detail.getShift().equals("Cả ngày"))
                                    count += 2;
                                else
                                    count++;
                            }
                            tvLeaveRequests.setText(count + "");
                        }
                    });
                }
            }
        });
    }

    private void loadChart() {
        attendanceController.getAttendancesOfEmployeeByMonth(calendar.getTime(), employeeRef, task2 -> {
            if (task2.isSuccessful()) {
                if (task2.getResult().size() > 0) {
                    List<Entry> workEntries = new ArrayList<>();
                    List<Entry> overTimeEntries = new ArrayList<>();
                    List<Entry> lateEntries = new ArrayList<>();
                    double totalWorkHours = 0;
                    double totalOvertimeHours = 0;
                    double totalLateHours = 0;
                    for (Attendance data : task2.getResult()) {
                        Calendar temp = Calendar.getInstance();
                        temp.setTime(data.getCheckInTime());
                        double overHours = data.getOvertime();
                        double lateHours = data.getLate();
                        double workHours = DateUtils.getDiffHours(data.getCheckInTime(), data.getCheckOutTime()) - overHours;
                        totalWorkHours += workHours;
                        totalOvertimeHours += overHours;
                        totalLateHours += lateHours;
                        workEntries.add(new Entry(temp.get(Calendar.DAY_OF_MONTH), (float) (workHours)));
                        overTimeEntries.add(new Entry(temp.get(Calendar.DAY_OF_MONTH), (float) (overHours)));
                        lateEntries.add(new Entry(temp.get(Calendar.DAY_OF_MONTH), (float) (lateHours)));
                    }
                    tvLateHours.setText(String.format("%.2f giờ", totalLateHours));
                    tvOvertimeHours.setText(String.format("%.2f giờ", totalOvertimeHours));
                    tvWorkHours.setText(String.format("%.2f giờ", totalWorkHours));


                    LineDataSet workSet = new LineDataSet(workEntries, "Giờ làm");
                    workSet.setColors(getColor(R.color.light_purple));
                    workSet.setValueTextColor(getColor(R.color.black));
                    workSet.setValueTextSize(8);
                    workSet.setCircleColor(R.color.light_purple);
                    workSet.setLineWidth(3);
                    workSet.setDrawValues(true);

                    LineDataSet overtimeSet = new LineDataSet(overTimeEntries, "Giờ tăng ca");
                    overtimeSet.setColors(getColor(R.color.accept_line));
                    overtimeSet.setValueTextColor(getColor(R.color.black));
                    overtimeSet.setValueTextSize(8);
                    overtimeSet.setCircleColor(R.color.accept_line);
                    overtimeSet.setLineWidth(3);
                    overtimeSet.setDrawValues(true);

                    LineDataSet lateSet = new LineDataSet(lateEntries, "Giờ đi trễ");
                    lateSet.setColors(getColor(R.color.reject_line));
                    lateSet.setValueTextColor(getColor(R.color.black));
                    lateSet.setValueTextSize(8);
                    lateSet.setCircleColor(R.color.reject_line);
                    lateSet.setLineWidth(3);
                    lateSet.setDrawValues(true);

                    List<ILineDataSet> setList = new ArrayList<>();
                    setList.add(workSet);
                    setList.add(overtimeSet);
                    setList.add(lateSet);
                    LineData lineData = new LineData(setList);
                    chart.setData(lineData);
                    btnChangeTime.setEnabled(true);
                    setStopLoading();
                }
                else {
                    chart.setData(null);
                    tvLateHours.setText("0.00 giờ");
                    tvOvertimeHours.setText("0.00 giờ");
                    tvWorkHours.setText("0.00 giờ");
                    btnChangeTime.setEnabled(true);
                    setStopLoading();
                }
            }
        });
    }

    private void setLoading() {
        progressBar.setVisibility(View.VISIBLE);
        layoutData.setVisibility(View.INVISIBLE);
    }

    private void setStopLoading() {
        progressBar.setVisibility(View.INVISIBLE);
        layoutData.setVisibility(View.VISIBLE);
    }
}