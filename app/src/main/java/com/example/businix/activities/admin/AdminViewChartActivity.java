package com.example.businix.activities.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.businix.R;
import com.example.businix.controllers.AttendanceController;
import com.example.businix.controllers.DepartmentController;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.controllers.LeaveRequestController;
import com.example.businix.controllers.LeaveRequestDetailController;
import com.example.businix.controllers.PositionController;
import com.example.businix.controllers.StatController;
import com.example.businix.models.Attendance;
import com.example.businix.models.Department;
import com.example.businix.models.LeaveRequestDetail;
import com.example.businix.models.LeaveRequestStatus;
import com.example.businix.models.Position;
import com.example.businix.ui.MonthYearPickerDialog;
import com.example.businix.utils.DateUtils;
import com.example.businix.utils.LeaveStat;
import com.example.businix.utils.SalaryData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AdminViewChartActivity extends AppCompatActivity {
    private PieChart chartEmployee;
    private LineChart chartAttendance;
    private BarChart chartLeave, chartSalary;
    private EmployeeController employeeController;
    private DepartmentController departmentController;
    private PositionController positionController;
    private AttendanceController attendanceController;
    private StatController statController;
    private LeaveRequestController leaveRequestController;
    private LeaveRequestDetailController leaveRequestDetailController;
    private LinearLayout statEmployee, statAttendance, statSalary, statLeave;
    private LinearLayout btnEmployeeChart, btnByPosition, btnByDepartment, btnAttendanceChart, btnChangeTime, btnSalaryChart, btnLeaveChart, btnChangeTimeLeave, btnChangeTimeSalary;
    private ImageView imgEmployeeChart, imgAttendanceChart, imgSalaryChart, imgLeaveChart;
    private TextView tvEmplTitle, tvEmplCount, tvCountName, tvCount, tvMonth, tvMonthLeave, tvSalaryTime;
    private ProgressBar pbEmpl, pbAttendance, pbSalary, pbLeave;
    private Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_chart);

        //Controller
        employeeController = new EmployeeController();
        departmentController = new DepartmentController();
        positionController = new PositionController();
        attendanceController = new AttendanceController();
        statController = new StatController();

        //Employee Chart
        statEmployee = findViewById(R.id.stat_employee);
        pbEmpl = findViewById(R.id.progress_bar_empl);
        chartEmployee = findViewById(R.id.employee_chart);

        tvEmplTitle = findViewById(R.id.tv_employee_chart_title);
        tvEmplCount = findViewById(R.id.tv_empl_count);
        tvCountName = findViewById(R.id.tv_count_name);
        tvCount = findViewById(R.id.tv_count);
        imgEmployeeChart = findViewById(R.id.img_show_empl_chart);
        btnByPosition = findViewById(R.id.btn_position);
        btnByDepartment = findViewById(R.id.btn_department);

        //Work Chart
        statAttendance = findViewById(R.id.stat_attendance);
        chartAttendance = findViewById(R.id.attendance_chart);
        imgAttendanceChart = findViewById(R.id.img_show_attendance_chart);

        tvMonth = findViewById(R.id.tv_month);
        btnChangeTime = findViewById(R.id.btn_select_time);
        calendar = Calendar.getInstance();

        //Leave Chart
        statLeave = findViewById(R.id.stat_leave);
        chartLeave = findViewById(R.id.leave_chart);
        imgLeaveChart = findViewById(R.id.img_show_leave_chart);

        tvMonthLeave = findViewById(R.id.tv_month_leave);
        btnChangeTimeLeave = findViewById(R.id.btn_select_time_leave);

        //Button Click Event
        btnEmployeeChart = findViewById(R.id.btn_show_empl_chart);
        btnEmployeeChart.setOnClickListener(v -> {
            if (statEmployee.getVisibility() == View.VISIBLE) {
                imgEmployeeChart.setImageResource(R.drawable.ic_arrow_down);
                statEmployee.setVisibility(View.GONE);
            } else {
                loadEmployeeChartByDepartment();

                btnByPosition.setOnClickListener(v1 -> {
                    pbEmpl.setVisibility(View.VISIBLE);
                    statEmployee.setVisibility(View.INVISIBLE);
                    loadEmployeeChartByPosition();
                });

                btnByDepartment.setOnClickListener(v1 -> {
                    pbEmpl.setVisibility(View.VISIBLE);
                    statEmployee.setVisibility(View.VISIBLE);
                    loadEmployeeChartByDepartment();

                });
            }
        });

        btnAttendanceChart = findViewById(R.id.btn_show_attendance_chart);
        btnAttendanceChart.setOnClickListener(v -> {
            if (statAttendance.getVisibility() == View.VISIBLE) {
                imgAttendanceChart.setImageResource(R.drawable.ic_arrow_down);
                statAttendance.setVisibility(View.GONE);
            } else {
                loadAttendanceChart(calendar.getTime());
                tvMonth.setText(DateUtils.formatDate(calendar.getTime(), "MM/yyyy"));
            }

        });
        btnChangeTime.setOnClickListener(v1 -> {
            btnChangeTime.setEnabled(false);
            int yearSelected = calendar.get(Calendar.YEAR);
            int monthSelected = calendar.get(Calendar.MONTH);

            MonthYearPickerDialog monthDialog = new MonthYearPickerDialog(new MonthYearPickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(year, month, 15);
                    tvMonth.setText(DateUtils.formatDate(calendar.getTime(), "MM/yyyy"));
                    loadAttendanceChart(calendar.getTime());
                }

                @Override
                public void onCancel() {
                    btnChangeTime.setEnabled(true);
                }
            }, yearSelected, monthSelected);
            monthDialog.setCancelable(false);
            monthDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");
        });

        //Salary Chart
        statSalary = findViewById(R.id.stat_salary);
        chartSalary = findViewById(R.id.salary_chart);
        pbSalary = findViewById(R.id.progress_bar_salary);
        tvSalaryTime = findViewById(R.id.tv_salary_time);
        imgSalaryChart = findViewById(R.id.img_show_salary_chart);
        btnSalaryChart = findViewById(R.id.btn_show_salary_chart);
        btnChangeTimeSalary = findViewById(R.id.btn_select_time_salary);
        btnSalaryChart.setOnClickListener(v -> {
            if (statSalary.getVisibility() == View.VISIBLE) {
                imgSalaryChart.setImageResource(R.drawable.ic_arrow_down);
                statSalary.setVisibility(View.GONE);
            } else {
                loadSalaryChart(calendar.getTime());
                statSalary.setVisibility(View.VISIBLE);
                tvSalaryTime.setText(DateUtils.formatDate(calendar.getTime(), "MM/yyyy"));
            }
        });
        btnChangeTimeSalary.setOnClickListener(view -> {
            btnChangeTimeSalary.setEnabled(false);
            int yearSelected = calendar.get(Calendar.YEAR);
            int monthSelected = calendar.get(Calendar.MONTH);

            MonthYearPickerDialog monthDialog = new MonthYearPickerDialog(new MonthYearPickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(year, month, 15);
                    tvSalaryTime.setText(DateUtils.formatDate(calendar.getTime(), "MM/yyyy"));
                    loadSalaryChart(calendar.getTime());
                }

                @Override
                public void onCancel() {
                    btnChangeTimeSalary.setEnabled(true);
                }
            }, yearSelected, monthSelected);
            monthDialog.setCancelable(false);
            monthDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");
        });

        leaveRequestController = new LeaveRequestController();
        leaveRequestDetailController = new LeaveRequestDetailController();
        btnLeaveChart = findViewById(R.id.btn_show_leave_chart);
        btnLeaveChart.setOnClickListener(v -> {
            if (statLeave.getVisibility() == View.VISIBLE) {
                imgLeaveChart.setImageResource(R.drawable.ic_arrow_down);
                statLeave.setVisibility(View.GONE);
            } else {
                imgLeaveChart.setImageResource(R.drawable.ic_arrow_up);
                statLeave.setVisibility(View.VISIBLE);
                loadLeaveRequestChart(calendar.getTime());
                tvMonthLeave.setText(DateUtils.formatDate(calendar.getTime(), "MM/yyyy"));
                btnChangeTimeLeave.setEnabled(true);

            }
        });
        btnChangeTimeLeave.setOnClickListener(v1 -> {
            btnChangeTimeLeave.setEnabled(false);
            int yearSelected = calendar.get(Calendar.YEAR);
            int monthSelected = calendar.get(Calendar.MONTH);

            MonthYearPickerDialog monthDialog = new MonthYearPickerDialog(new MonthYearPickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(year, month, 15);
                    tvMonthLeave.setText(DateUtils.formatDate(calendar.getTime(), "MM/yyyy"));
                    loadLeaveRequestChart(calendar.getTime());

                }

                @Override
                public void onCancel() {
                    btnChangeTimeLeave.setEnabled(true);
                }
            }, yearSelected, monthSelected);
            monthDialog.setCancelable(false);
            monthDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> {
            finish();
        });
    }

    //Employee Chart
    private void loadEmployeeChartByDepartment() {
        Map<Department, Integer> countMap = new HashMap<>();

        departmentController.getDepartmentList(task -> {
            if (task.isSuccessful()) {
                List<Department> departmentList = task.getResult();
                AtomicInteger countEmpl = new AtomicInteger();
                AtomicInteger processedDepartments = new AtomicInteger(0);
                for (Department department : departmentList) {
                    employeeController.getEmployeeListByDepartment(department.getId(), task1 -> {
                        if (task1.isSuccessful()) {
                            int count = task1.getResult().size();
                            if (count > 0) {
                                countMap.put(department, count);
                                countEmpl.addAndGet(count);
                            }
                            processedDepartments.incrementAndGet();
                            if (processedDepartments.get() == departmentList.size()) {
                                List<PieEntry> entries = new ArrayList<>();
                                for (Department dpm : countMap.keySet()) {
                                    entries.add(new PieEntry(countMap.get(dpm), dpm.getName()));
                                }
                                PieDataSet dataSet = new PieDataSet(entries, "");
                                dataSet.setUsingSliceColorAsValueLineColor(true);
                                dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                                dataSet.getValueTextColor(R.color.black);
                                dataSet.setColors(getColor(R.color.light_purple), getColor(R.color.accept_line), getColor(R.color.waiting_line));
                                dataSet.setValueTextSize(12f);

                                PieData data = new PieData(dataSet);

                                chartEmployee.setExtraOffsets(15f, 15f, 15f, 15f);
                                chartEmployee.getDescription().setEnabled(false);
                                chartEmployee.setData(data);
                                chartEmployee.setEntryLabelColor(R.color.black);
                                chartEmployee.invalidate();
                                tvEmplCount.setText(String.valueOf(countEmpl));
                            }
                        }
                        pbEmpl.setVisibility(View.GONE);
                        statEmployee.setVisibility(View.VISIBLE);
                        tvEmplTitle.setText("Theo phòng ban");
                        tvCountName.setText("Tổng phòng ban");
                        tvCount.setText(String.valueOf(departmentList.size()));
                        imgEmployeeChart.setImageResource(R.drawable.ic_arrow_up);
                        statEmployee.setVisibility(View.VISIBLE);
                        btnByPosition.setVisibility(View.VISIBLE);
                        btnByDepartment.setVisibility(View.GONE);
                    });
                }
            }
        });
    }

    private void loadEmployeeChartByPosition() {
        Map<Position, Integer> countMap = new HashMap<>();

        positionController.getPositionList(task -> {
            if (task.isSuccessful()) {
                List<Position> positionList = task.getResult();
                AtomicInteger countEmpl = new AtomicInteger();
                AtomicInteger processedPositions = new AtomicInteger(0);
                for (Position pos : positionList) {
                    employeeController.getEmployeeListByPosition(pos.getId(), task1 -> {
                        if (task1.isSuccessful()) {
                            int count = task1.getResult().size();
                            if (count > 0) {
                                countMap.put(pos, count);
                                countEmpl.addAndGet(count);
                            }
                            processedPositions.incrementAndGet();
                            if (processedPositions.get() == positionList.size()) {
                                List<PieEntry> entries = new ArrayList<>();
                                for (Position position : countMap.keySet()) {
                                    entries.add(new PieEntry(countMap.get(position), position.getName()));
                                }
                                PieDataSet dataSet = new PieDataSet(entries, "");
                                dataSet.setUsingSliceColorAsValueLineColor(true);
                                dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                                dataSet.getValueTextColor(R.color.black);
                                dataSet.setColors(getColor(R.color.light_purple), getColor(R.color.accept_line), getColor(R.color.waiting_line));
                                dataSet.setValueTextSize(12f);

                                PieData data = new PieData(dataSet);

                                chartEmployee.setExtraOffsets(15f, 15f, 15f, 15f);
                                chartEmployee.getDescription().setEnabled(false);
                                chartEmployee.setData(data);
                                chartEmployee.setEntryLabelColor(R.color.black);
                                chartEmployee.invalidate();
                                tvEmplCount.setText(String.valueOf(countEmpl));
                            }
                        }
                        pbEmpl.setVisibility(View.GONE);
                        statEmployee.setVisibility(View.VISIBLE);
                        tvEmplTitle.setText("Theo chức vụ");
                        tvCountName.setText("Tổng chức vụ");
                        tvCount.setText(String.valueOf(positionList.size()));
                        imgEmployeeChart.setImageResource(R.drawable.ic_arrow_up);
                        statEmployee.setVisibility(View.VISIBLE);
                        btnByDepartment.setVisibility(View.VISIBLE);
                        btnByPosition.setVisibility(View.GONE);
                    });
                }
            }
        });
    }

    private void loadAttendanceChart(Date date) {
        attendanceController.getStatAttendanceGroupByDate(date, task -> {
            if (task.isSuccessful()) {
                List<Entry> workEntries = new ArrayList<>();
                List<Entry> overTimeEntries = new ArrayList<>();
                List<Entry> lateEntries = new ArrayList<>();
                Map<Date, List<Attendance>> datas = new TreeMap<>(task.getResult());

                for (Date dateKey : datas.keySet()) {
                    double workHours = 0;
                    double lateHours = 0;
                    double overHours = 0;
                    Calendar temp = Calendar.getInstance();
                    temp.setTime(dateKey);
                    for (Attendance attendance : datas.get(dateKey)) {
                        overHours += attendance.getOvertime();
                        lateHours += attendance.getLate();
                        workHours += DateUtils.getDiffHours(attendance.getCheckInTime(), attendance.getCheckOutTime());
                    }
                    workHours -= overHours;
                    workEntries.add(new Entry(temp.get(Calendar.DAY_OF_MONTH), (float) (workHours)));
                    overTimeEntries.add(new Entry(temp.get(Calendar.DAY_OF_MONTH), (float) (overHours)));
                    lateEntries.add(new Entry(temp.get(Calendar.DAY_OF_MONTH), (float) (lateHours)));
                }
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
                chartAttendance.getDescription().setEnabled(false);
                chartAttendance.setData(lineData);
                chartAttendance.invalidate();
            }
            imgAttendanceChart.setImageResource(R.drawable.ic_arrow_up);
            statAttendance.setVisibility(View.VISIBLE);
            btnChangeTime.setEnabled(true);
        });
    }

    private void loadSalaryChart(Date dateSelected) {
        statController.statSlary(dateSelected, dateSalaryDataMap -> {
            List<BarEntry> entries = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            for (Date date : dateSalaryDataMap.keySet()) {
                cal.setTime(date);
                SalaryData data = dateSalaryDataMap.get(date);
                entries.add(new BarEntry(cal.get(Calendar.DAY_OF_MONTH),
                        new float[]{(float) (data.getNormalSalary()),
                                (float) (data.getOvertimeSalary())}));
            }
            BarDataSet dataSet = new BarDataSet(entries, "");
            dataSet.setColors(getColor(R.color.sub_purple), getColor(R.color.light_purple));
            dataSet.setValueTextColor(getColor(R.color.black));
            dataSet.setHighLightColor(getColor(R.color.waiting));
            dataSet.setHighLightAlpha(255);
            dataSet.setValueTextSize(8);
            dataSet.setDrawValues(true);
            dataSet.setStackLabels(new String[]{"Lương hành chính", "Lương overtime"});
            BarData barDate = new BarData(dataSet);

            chartSalary.getAxisRight().setEnabled(false);
            chartSalary.setData(barDate);
            chartSalary.setDrawValueAboveBar(false);
            chartSalary.getAxisLeft().setValueFormatter(new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    DecimalFormat df = new DecimalFormat("#.###");
                    return df.format(value) + "k";
                }
            });
            XAxis xAxis = chartSalary.getXAxis();
            xAxis.setTextSize(11);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    int dayOfMonth = (int) value;
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    return DateUtils.formatDate(calendar.getTime(), "dd/MM");
                }
            });
            chartSalary.invalidate();
        });
        btnChangeTimeSalary.setEnabled(true);
    }

    private void loadLeaveRequestChart(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date minDate = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date maxDate = cal.getTime();
        leaveRequestController.getLeaveRequestOverlapping(minDate, maxDate, LeaveRequestStatus.ACCEPT, task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    leaveRequestDetailController.getDetailsByTime(minDate, maxDate, task.getResult(), task1 -> {
                        if (task1.isSuccessful()) {
                            if (task1.getResult().size() > 0) {
                                List<LeaveRequestDetail> list = task1.getResult();
                                Map<Date, LeaveStat> detailByDate = new HashMap<>();
                                for (LeaveRequestDetail detail : list) {
                                    cal.setTime(detail.getDate());
                                    cal.set(Calendar.HOUR_OF_DAY, 0);
                                    cal.set(Calendar.MINUTE, 0);
                                    cal.set(Calendar.SECOND, 0);
                                    cal.set(Calendar.MILLISECOND, 0);
                                    Date dateKey = cal.getTime();
                                    if (!detailByDate.containsKey(dateKey)) {
                                        detailByDate.put(dateKey, new LeaveStat());
                                    }
                                    switch (detail.getShift()) {
                                        case "Cả ngày":
                                            int total = detailByDate.get(dateKey).getTotalAllDay();
                                            detailByDate.get(dateKey).setTotalAllDay(total + 1);
                                            break;
                                        case "Ca sáng":
                                            int totalM = detailByDate.get(dateKey).getMorningShift();
                                            detailByDate.get(dateKey).setMorningShift(totalM + 1);
                                            break;
                                        default:
                                            int totalA = detailByDate.get(dateKey).getAfternoonShift();
                                            detailByDate.get(dateKey).setAfternoonShift(totalA + 1);
                                            break;
                                    }
                                }
                                List<BarEntry> entries = new ArrayList<>();
                                Calendar dateTemp = Calendar.getInstance();

                                for (Date dateKey : detailByDate.keySet()) {
                                    dateTemp.setTime(dateKey);
                                    LeaveStat ls = detailByDate.get(dateKey);
                                    entries.add(new BarEntry(dateTemp.get(Calendar.DAY_OF_MONTH),
                                            new float[]{ls.getTotalAllDay(),
                                                    ls.getMorningShift(),
                                                    ls.getAfternoonShift()}));
                                }

                                BarDataSet dataSet = new BarDataSet(entries, "");
                                dataSet.setColors(getColor(R.color.light_purple), getColor(R.color.waiting_line), getColor(R.color.success));
                                dataSet.setValueTextColor(getColor(R.color.black));
                                dataSet.setHighLightColor(getColor(R.color.waiting));
                                dataSet.setHighLightAlpha(255);
                                dataSet.setValueTextSize(8);
                                dataSet.setDrawValues(true);
                                dataSet.setStackLabels(new String[]{"Cả ngày", "Ca sáng", "Ca chiều"});

                                XAxis xAxis = chartLeave.getXAxis();
                                xAxis.setTextSize(12);
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                xAxis.setGranularity(1f);
                                xAxis.setValueFormatter(new ValueFormatter() {
                                    @Override
                                    public String getAxisLabel(float value, AxisBase axis) {
                                        int dayOfMonth = (int) value;
                                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                        return DateUtils.formatDate(calendar.getTime(), "dd/MM");
                                    }
                                });

                                BarData barData = new BarData(dataSet);
                                chartLeave.getAxisRight().setEnabled(false);
                                chartLeave.setDescription(null);
                                chartLeave.setData(barData);
                                chartLeave.invalidate();
                            }
                        }

                    });
                }
            }
            chartLeave.setData(null);
            chartLeave.invalidate();
            btnChangeTimeLeave.setEnabled(true);
        });
    }
}