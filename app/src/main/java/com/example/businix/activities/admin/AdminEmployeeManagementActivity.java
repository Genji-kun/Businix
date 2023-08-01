package com.example.businix.activities.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.businix.R;
import com.example.businix.adapters.EmployeeAdapter;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.controllers.PositionController;
import com.example.businix.models.Employee;
import com.example.businix.models.Position;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AdminEmployeeManagementActivity extends AppCompatActivity {
    private ImageView btnHome;
    private AutoCompleteTextView dropdownPosition;
    private TextInputEditText inputName;
    private LinearLayout btnAddEmployee;
    private ListView listView;
    private EmployeeAdapter employeeAdapter;
    private PositionController positionController;
    private List<Employee> employeeList;
    private List<String> posItems;
    private List<Position> posList;
    private ArrayAdapter<String> posNameAdapter;
    private RadioGroup radioGroupStatus;
    private RadioButton radioActive, radioInActive;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_employee_management);

        radioGroupStatus = (RadioGroup) findViewById(R.id.radio_group_status);
        radioActive = (RadioButton) findViewById(R.id.radio_active);
        radioInActive = (RadioButton) findViewById(R.id.radio_inactive);

        radioGroupStatus.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.radio_active){
                employeeAdapter.getFilter().filter("");
            }else if(checkedId == R.id.radio_inactive){
                employeeAdapter.getFilter().filter("");
            }
        });

        listView = (ListView) findViewById(R.id.list_view_employee);
        EmployeeController employeeController = new EmployeeController();
        employeeController.getEmployeeList(new OnCompleteListener<List<Employee>>() {
            @Override
            public void onComplete(@NonNull Task<List<Employee>> task) {
                if (task.isSuccessful()) {
                    employeeList = task.getResult();
                    employeeAdapter = new EmployeeAdapter(AdminEmployeeManagementActivity.this, R.layout.list_view_employee, employeeList);
                    listView.setAdapter(employeeAdapter);
                }
            }
        });


        inputName = (TextInputEditText) findViewById(R.id.input_name);
        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                if (employeeAdapter != null) {
                    employeeAdapter.getFilter().filter(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dropdownPosition = (AutoCompleteTextView) findViewById(R.id.dropdown_position);

        posItems = new ArrayList<String>();
        positionController = new PositionController();
        positionController.getPositionList(task -> {
            if (task.isSuccessful()) {
                posList = task.getResult();
                for (Position pos : posList) {
                    posItems.add(pos.getName());
                }
                posNameAdapter = new ArrayAdapter<String>(AdminEmployeeManagementActivity.this, R.layout.dropdown_menu, posItems);
                dropdownPosition.setAdapter(posNameAdapter);
                dropdownPosition.setText("", false);

            } else {
                // Xử lý lỗi
            }
        });
        dropdownPosition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedPosition = posItems.get(position);
                employeeAdapter.getFilter().filter("");
            }
        });

        btnAddEmployee = (LinearLayout) findViewById(R.id.btn_add_employee);
        btnAddEmployee.setOnClickListener(v -> {
            CustomDialogAddEmployee customAddEmployeeDialog = new CustomDialogAddEmployee(AdminEmployeeManagementActivity.this);
            customAddEmployeeDialog.show();
        });

        btnHome = (ImageView) findViewById(R.id.btn_home);
        btnHome.setOnClickListener(v -> {
            finish();
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        EmployeeController employeeController = new EmployeeController();
        employeeController.getEmployeeList(new OnCompleteListener<List<Employee>>() {
            @Override
            public void onComplete(@NonNull Task<List<Employee>> task) {
                if (task.isSuccessful()) {
                    employeeList = task.getResult();
                    if (employeeAdapter != null) {
                        employeeAdapter.clear(); // Xóa dữ liệu cũ trong adapter
                        employeeAdapter.addAll(employeeList); // Thêm dữ liệu mới vào adapter
                        employeeAdapter.notifyDataSetChanged(); // Cập nhật lại ListView
                    }
                } else {
                    // Xử lý lỗi
                }
            }
        });
    }
}