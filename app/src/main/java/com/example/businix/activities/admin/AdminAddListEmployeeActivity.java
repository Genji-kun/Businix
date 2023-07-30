package com.example.businix.activities.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.businix.R;
import com.example.businix.controllers.DepartmentController;
import com.example.businix.controllers.PositionController;
import com.example.businix.models.Department;
import com.example.businix.models.Position;
import com.example.businix.models.UserRole;

import java.util.ArrayList;
import java.util.List;

public class AdminAddListEmployeeActivity extends AppCompatActivity {

    private ImageView btnBack;
    private LinearLayout btnAdd, layoutContainer, btnAddListEmployee;
    private AutoCompleteTextView dropdownPosition, dropdownDepartment, dropdownRole;

    private ArrayAdapter<String> posNameAdapter, departmentNameAdapter, roleAdapter;
    private List<String> posItems, departmentItems, roleItems;
    private List<Position> posList;
    private List<Department> departmentList;
    private String positionId, departmentId;
    private PositionController positionController;
    private DepartmentController departmentController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_list_employee);

        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnAdd = (LinearLayout) findViewById(R.id.btn_add);
        btnAddListEmployee = (LinearLayout) findViewById(R.id.btn_add_list_employee);
        layoutContainer = (LinearLayout) findViewById(R.id.layout_container);
        dropdownRole = (AutoCompleteTextView) findViewById(R.id.dropdown_role);
        dropdownPosition = (AutoCompleteTextView) findViewById(R.id.dropdown_position);
        dropdownDepartment = (AutoCompleteTextView) findViewById(R.id.dropdown_department);

        posItems = new ArrayList<String>();
        positionController = new PositionController();
        positionController.getPositionList(task -> {
            if (task.isSuccessful()) {
                posList = task.getResult();
                for (Position pos : posList) {
                    posItems.add(pos.getName());
                }
                posNameAdapter = new ArrayAdapter<String>(AdminAddListEmployeeActivity.this, R.layout.dropdown_menu, posItems);
                dropdownPosition.setAdapter(posNameAdapter);
            } else {
                // Xử lý lỗi
            }
        });
        dropdownPosition.setOnItemClickListener((parent, view, position, id) -> {
            for (Position pos : posList) {
                if (pos.getName().equals(posItems.get(position))) {
                    positionId = pos.getId();
                }
            }
        });

        departmentItems = new ArrayList<String>();
        departmentController = new DepartmentController();
        departmentController.getDepartmentList(task -> {
            if (task.isSuccessful()) {
                departmentList = task.getResult();
                for (Department department : departmentList) {
                    departmentItems.add(department.getName());
                }
                departmentNameAdapter = new ArrayAdapter<String>(AdminAddListEmployeeActivity.this, R.layout.dropdown_menu, departmentItems);
                dropdownDepartment.setAdapter(departmentNameAdapter);
            } else {
                //Xử lý lỗi
            }
        });
        dropdownDepartment.setOnItemClickListener((parent, view, position, id) -> {
            for (Department department : departmentList) {
                if (department.getName().equals(departmentItems.get(position))) {
                    departmentId = department.getId();
                }
            }
        });

        roleItems = new ArrayList<>();
        UserRole[] roles = UserRole.values();
        for (UserRole role : roles) {
            roleItems.add(role.toString());
        }
        roleAdapter = new ArrayAdapter<>(AdminAddListEmployeeActivity.this, R.layout.dropdown_menu, roleItems);
        dropdownRole.setAdapter(roleAdapter);


        btnAdd.setOnClickListener(v -> {

        });
    }
}