package com.example.businix.activities.admin;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.businix.R;
import com.example.businix.controllers.DepartmentController;
import com.example.businix.controllers.PositionController;
import com.example.businix.models.Department;
import com.example.businix.models.Position;
import com.example.businix.utils.FindListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

public class AdminAddDepartmentActivity extends AppCompatActivity {

    private LinearLayout btnAddDepartment;
    private TextInputEditText inputDepartmentName;
    private DepartmentController departmentController;
    private ImageView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_department);

        inputDepartmentName = (TextInputEditText) findViewById(R.id.input_department_name);
        btnAddDepartment = (LinearLayout) findViewById(R.id.btn_add_department);
        btnBack = (ImageView) findViewById(R.id.btn_back);

        departmentController = new DepartmentController();

        btnAddDepartment.setOnClickListener(v -> {
            String departmentName = inputDepartmentName.getText().toString().strip();
            if (departmentName.isBlank()) {
                Toast.makeText(AdminAddDepartmentActivity.this, "Tên phòng ban không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            departmentController.isExistedDepartment(departmentName, new FindListener() {
                @Override
                public void onFoundSuccess() {
                    Toast.makeText(AdminAddDepartmentActivity.this, "Tên phòng ban đã tồn tại", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNotFound() {
                    Department newDepartment = new Department();
                    newDepartment.setName(departmentName);

                    departmentController.addDepartment(newDepartment, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AdminAddDepartmentActivity.this, "Thêm phòng ban thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AdminAddDepartmentActivity.this, "Lỗi khi thêm phòng ban", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}