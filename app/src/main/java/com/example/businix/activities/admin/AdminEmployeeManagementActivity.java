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
import com.example.businix.models.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AdminEmployeeManagementActivity extends AppCompatActivity {
    private ImageView btnHome;
    private TextInputEditText inputName;
    private AutoCompleteTextView dropdownPosition;
    private LinearLayout btnAddEmployee;
    private ListView listView;
    private RadioGroup radioGroupStatus;
    private RadioButton radioActive, radioInActive;
    private EmployeeAdapter employeeAdapter;
    private List<Employee> employeeList;
    private List<Position> posList;
    private List<String> posItems;
    private ArrayAdapter<String> posNameAdapter;

    private EmployeeController employeeController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_employee_management);

        radioGroupStatus = (RadioGroup) findViewById(R.id.radio_group_status);
        radioActive = (RadioButton) findViewById(R.id.radio_active);
        radioInActive = (RadioButton) findViewById(R.id.radio_inactive);
        listView = (ListView) findViewById(R.id.list_view_employee);

        employeeController = new EmployeeController();
        if(radioActive.isChecked()){
            radioCheck(Status.ACTIVE);
        }
        radioGroupStatus.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_active) {
                inputName.setText("");
               radioCheck(Status.ACTIVE);
            } else if (checkedId == R.id.radio_inactive) {
                inputName.setText("");
                radioCheck(Status.INACTIVE);
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

        dropdownPosition.setOnItemClickListener((parent, view, position, id) -> {
            
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
        if(radioActive.isChecked()){
            inputName.setText("");
            radioCheck(Status.ACTIVE);
        }
        if(radioInActive.isChecked()){
            inputName.setText("");
            radioCheck(Status.INACTIVE);
        }
    }


    public void radioCheck(Status status)
    {
        employeeController.getEmployeeListByStatus(status, new OnCompleteListener<List<Employee>>() {
            @Override
            public void onComplete(@NonNull Task<List<Employee>> task) {
                if (task.isSuccessful()) {
                    employeeList = task.getResult();
                    employeeAdapter = new EmployeeAdapter(AdminEmployeeManagementActivity.this, R.layout.list_view_employee, employeeList);
                    listView.setAdapter(employeeAdapter);
                }
            }
        });
    }
}