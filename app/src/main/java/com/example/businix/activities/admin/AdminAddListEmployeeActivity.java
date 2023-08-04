package com.example.businix.activities.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.businix.R;
import com.example.businix.adapters.EmployeeAdapter;
import com.example.businix.adapters.EmployeeMemberAdapter;
import com.example.businix.controllers.DepartmentController;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.controllers.PositionController;
import com.example.businix.models.Department;
import com.example.businix.models.Employee;
import com.example.businix.models.Position;
import com.example.businix.models.UserRole;

import java.util.ArrayList;
import java.util.List;

public class AdminAddListEmployeeActivity extends AppCompatActivity {

    private ImageView btnBack;
    private LinearLayout btnAdd, btnAddListEmployee;
    private ArrayList<Employee> employeeList;
    private EmployeeMemberAdapter employeeMemberAdapter;
    private EmployeeController employeeController;
    private ListView listView;
    private TextView tvAddListEmployee;
    private ProgressBar progressBar;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_list_employee);

        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnAdd = (LinearLayout) findViewById(R.id.btn_add);
        btnAddListEmployee = (LinearLayout) findViewById(R.id.btn_add_list_employee);
        tvAddListEmployee = (TextView) findViewById(R.id.tv_btn_add_list_employee);


        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        listView = (ListView) findViewById(R.id.list_view_employee);
        employeeList = new ArrayList<>();
        employeeList.add(new Employee());
        employeeMemberAdapter = new EmployeeMemberAdapter(this, R.layout.list_view_employee_member, employeeList);
        listView.setAdapter(employeeMemberAdapter);

        btnAdd.setOnClickListener(v -> {
            Employee employee = new Employee();
            employeeList.add(employee);
            employeeMemberAdapter.notifyDataSetChanged();
        });

        employeeController = new EmployeeController();
        btnAddListEmployee.setOnClickListener(v -> {
            for (Employee empl : employeeList) {
                employeeController.addEmployee(empl, task -> {
                    if (task.isSuccessful()) {
                        count += 1;
                    } else {
                        normalView();
                        Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
            }
            if (count == employeeList.size()) {
                Toast.makeText(AdminAddListEmployeeActivity.this, "Thêm danh sách  thành công", Toast.LENGTH_SHORT).show();
                normalView();
                finish();
            }
        });

        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    public void processingView() {
        progressBar.setVisibility(View.VISIBLE);
        tvAddListEmployee.setVisibility(View.GONE);
        btnAddListEmployee.setEnabled(false);
        btnAddListEmployee.setBackgroundColor(Color.LTGRAY);
        btnBack.setEnabled(false);
    }

    public void normalView() {
        progressBar.setVisibility(View.GONE);
        tvAddListEmployee.setVisibility(View.VISIBLE);
        btnAddListEmployee.setEnabled(true);
        btnAddListEmployee.setBackgroundColor(getResources().getColor(R.color.light_purple));
        btnBack.setEnabled(true);
    }
}