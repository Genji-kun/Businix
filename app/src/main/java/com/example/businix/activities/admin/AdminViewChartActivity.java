package com.example.businix.activities.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.businix.R;
import com.example.businix.controllers.DepartmentController;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.models.Department;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AdminViewChartActivity extends AppCompatActivity {
    private PieChart chartEmployee;
    private EmployeeController employeeController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_chart);
        employeeController = new EmployeeController();
        chartEmployee = findViewById(R.id.employee_chart);
        loadEmployeeChart();
    }

    private void loadEmployeeChart() {
        Map<Department, Integer> countMap = new HashMap<>();

        DepartmentController departmentController = new DepartmentController();
        departmentController.getDepartmentList(task -> {
            if (task.isSuccessful()) {
                List<Department> departmentList = task.getResult();
                AtomicInteger processedDepartments = new AtomicInteger(0);
                for(Department department : departmentList){
                    employeeController.getEmployeeListByDepartment(department.getId(), task1 -> {
                        if(task1.isSuccessful()){
                            int count = task1.getResult().size();
                            if ( count > 0) {
                                countMap.put(department,count);
                            }
                            processedDepartments.incrementAndGet();
                            if(processedDepartments.get() == departmentList.size()) {
                                List<PieEntry> entries = new ArrayList<>();
                                for (Department dpm : countMap.keySet()) {
                                    entries.add(new PieEntry(countMap.get(dpm), dpm.getName()));
                                }
                                PieDataSet dataSet = new PieDataSet(entries, "");
                                dataSet.setUsingSliceColorAsValueLineColor(true);
                                dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                                dataSet.getValueTextColor(R.color.black);
                                dataSet.setColors(R.color.light_purple, R.color.accept_line, R.color.waiting_line);

                                PieData data = new PieData(dataSet);

                                chartEmployee.setData(data);
                                chartEmployee.setEntryLabelColor(R.color.black);
                                chartEmployee.invalidate();
                            }
                        }
                    });
                }
            }
        });



    }
}