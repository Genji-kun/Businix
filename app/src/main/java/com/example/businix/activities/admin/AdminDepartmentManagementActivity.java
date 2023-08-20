package com.example.businix.activities.admin;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.businix.R;

import com.example.businix.adapters.DepartmentAdapter;
import com.example.businix.controllers.DepartmentController;
import com.example.businix.models.Department;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class AdminDepartmentManagementActivity extends AppCompatActivity {

    private ImageView btnHome;
    private LinearLayout btnAddDepartment;
    private TextInputEditText inputDepartment;
    private DepartmentAdapter departmentAdapter;
    private List<Department> departmentList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_department_management);

        ListView listView = (ListView) findViewById(R.id.list_view_department);
        DepartmentController departmentController = new DepartmentController();
        departmentController.getDepartmentList(new OnCompleteListener<List<Department>>() {
            @Override
            public void onComplete(@NonNull Task<List<Department>> task) {
                if (task.isSuccessful()) {
                    departmentList = task.getResult();
                    departmentAdapter = new DepartmentAdapter(AdminDepartmentManagementActivity.this, R.layout.list_view_department, departmentList);
                    listView.setAdapter(departmentAdapter);
                } else {
                    // xử lý lỗi
                }
            }
        });

        inputDepartment = (TextInputEditText) findViewById(R.id.input_department);
        inputDepartment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                if (departmentAdapter != null) {
                    departmentAdapter.getFilter().filter(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnAddDepartment = (LinearLayout) findViewById(R.id.btn_add_department);
        btnAddDepartment.setOnClickListener(v -> {
            Intent i = new Intent(AdminDepartmentManagementActivity.this, AdminAddDepartmentActivity.class);
            startActivity(i);
        });

        btnHome = (ImageView) findViewById(R.id.btn_home);
        btnHome.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        DepartmentController departmentController = new DepartmentController();
        departmentController.getDepartmentList(new OnCompleteListener<List<Department>>() {
            @Override
            public void onComplete(@NonNull Task<List<Department>> task) {
                if (task.isSuccessful()) {
                    departmentList = task.getResult();
                    if (departmentAdapter != null) {
                        departmentAdapter.clear(); // Xóa dữ liệu cũ trong adapter
                        departmentAdapter.addAll(departmentList); // Thêm dữ liệu mới vào adapter
                        departmentAdapter.notifyDataSetChanged(); // Cập nhật lại ListView
                    }
                } else {
                    // Xử lý lỗi
                }
            }
        });
    }
}