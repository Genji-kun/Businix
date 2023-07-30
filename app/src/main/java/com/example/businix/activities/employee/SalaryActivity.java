package com.example.businix.activities.employee;

import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.businix.R;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.controllers.PositionController;
import com.example.businix.controllers.StatController;
import com.example.businix.models.Employee;
import com.example.businix.ui.ActionBar;
import com.example.businix.ui.MonthYearPickerDialog;
import com.example.businix.utils.DateUtils;
import com.example.businix.utils.LoginManager;
import com.example.businix.utils.StatData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.firestore.DocumentReference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SalaryActivity extends ActionBar {
    private BarChart barChart;
    private TextView tvMonth;
    private StatController statController;
    private DocumentReference employeeRef;
    private double salaryCoefficient;
    private Calendar calendar;
    private LinearLayout btnSelectDate;
    private ProgressBar processBar;
    private TextView tvSalaryTotal, tvSalaryPrimary, tvSalaryOvertime, tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary);

        toolbar = findViewById(R.id.toolbar);
        setSupportMyActionBar("Xem lương", true, false);

        barChart = findViewById(R.id.chart);
        barChart.getDescription().setEnabled(false);
        statController = new StatController();
        calendar = Calendar.getInstance();
        tvMonth = findViewById(R.id.month);
        tvMonth.setText(DateUtils.formatDate(calendar.getTime(), "MM/yyyy"));

        tvSalaryTotal = findViewById(R.id.salary_total);
        tvSalaryPrimary = findViewById(R.id.salary_primary);
        tvSalaryOvertime = findViewById(R.id.salary_overtime);
        tvDescription = findViewById(R.id.tv_description);

        LoginManager loginManager = new LoginManager(this);
        EmployeeController employeeController = new EmployeeController();
        employeeRef = employeeController.getEmployeeRef(loginManager.getLoggedInUserId());

        processBar = findViewById(R.id.progress_bar);
        btnSelectDate = findViewById(R.id.btn_select_date);
        btnSelectDate.setEnabled(false);
        btnSelectDate.setOnClickListener(v -> {
            btnSelectDate.setEnabled(false);
            selectMonth();
        });
        setLoading();
        employeeController.getEmployeeById(loginManager.getLoggedInUserId(), task -> {
            if (task.isSuccessful()) {
                Employee employee = task.getResult();
                (new PositionController()).getPositionById(employee.getPosition().getId(), task1 -> {
                    if (task1.isSuccessful()) {
                        salaryCoefficient = task1.getResult().getSalary();
                        loadChart(calendar.getTime());
                    }
                });

            }
        });


    }

    private void loadChart(Date dateSelected) {
        List<BarEntry> entries = new ArrayList<>();
        statController.getAttendancesMonth(dateSelected, employeeRef, task2 -> {
            if (task2.isSuccessful()) {
                double totalPrimary = 0;
                double totalOvertime = 0;
                for (StatData.AttendanceData data : task2.getResult()) {
                    calendar.setTime(data.getDate());
                    double salaryPrimary = data.getWorkHours() * salaryCoefficient;
                    double salaryOvertime = data.getOverTimeHours() * salaryCoefficient * (150 / 100);
                    totalPrimary += salaryPrimary;
                    totalOvertime += salaryOvertime;
                    entries.add(new BarEntry(calendar.get(Calendar.DAY_OF_MONTH), new float[]{(float) (salaryPrimary), (float) (salaryOvertime)}));
                }
                long total = (long) ((totalPrimary + totalOvertime) * 1000);
                tvSalaryTotal.setText( String.format("%,d", total) + " VNĐ");

                long totalPrimaryValue = (long) (totalPrimary * 1000);
                tvSalaryPrimary.setText(String.format("%,d", totalPrimaryValue));

                long totalOvertimeValue = (long) (totalOvertime * 1000);
                tvSalaryOvertime.setText(String.format("%,d", totalOvertimeValue));

                BarDataSet dataSet = new BarDataSet(entries, "");
                dataSet.setColors(getColor(R.color.sub_purple), getColor(R.color.light_purple));
                dataSet.setValueTextColor(getColor(R.color.black));
                dataSet.setHighLightColor(getColor(R.color.waiting));
                dataSet.setHighLightAlpha(255);
                dataSet.setValueTextSize(8);
                dataSet.setDrawValues(true);
                dataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        if (value == 0.0)
                            return "";
                        DecimalFormat df = new DecimalFormat("#.###");
                        return df.format(value) + "k";
                    }
                });
                dataSet.setStackLabels(new String[]{"Lương chính", "Lương tăng ca"});
                BarData barDate = new BarData(dataSet);

                barChart.getAxisRight().setEnabled(false);
                barChart.setData(barDate);
                barChart.setDrawValueAboveBar(false);
                barChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        DecimalFormat df = new DecimalFormat("#.###");
                        return df.format(value) + "k";
                    }
                });
                XAxis xAxis = barChart.getXAxis();
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
                barChart.invalidate();
            }
            btnSelectDate.setEnabled(true);
            setStopLoading();
        });
    }

    private void selectMonth() {

        int yearSelected = calendar.get(Calendar.YEAR);
        int monthSelected = calendar.get(Calendar.MONTH);

        MonthYearPickerDialog monthDialog = new MonthYearPickerDialog(new MonthYearPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, 15);
                tvMonth.setText(DateUtils.formatDate(calendar.getTime(), "MM/yyyy"));
                setLoading();
                Calendar nowCalendar = Calendar.getInstance();
                if (nowCalendar.get(Calendar.YEAR) == year && nowCalendar.get(Calendar.MONTH) == month) {
                    tvDescription.setText(getString(R.string.currentSalary));
                }
                else {
                    tvDescription.setText(getString(R.string.oldSalary));
                }
                loadChart(calendar.getTime());
            }

            @Override
            public void onCancel() {
                btnSelectDate.setEnabled(true);
            }
        }, yearSelected, monthSelected);
        monthDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");
    }

    private void setLoading() {
        processBar.setVisibility(View.VISIBLE);
        barChart.setVisibility(View.INVISIBLE);
    }

    private void setStopLoading() {
        processBar.setVisibility(View.INVISIBLE);
        barChart.setVisibility(View.VISIBLE);
    }
}