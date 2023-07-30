package com.example.businix.activities.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.businix.R;
import com.example.businix.controllers.DepartmentController;
import com.example.businix.controllers.PositionController;
import com.example.businix.models.Department;
import com.example.businix.models.Position;
import com.example.businix.ui.CustomDialog;
import com.google.android.material.textfield.TextInputEditText;

public class AdminEditDepartmentActivity extends AppCompatActivity {

    private LinearLayout btnEditDepartment;
    private TextInputEditText inputDepartmentName;
    private DepartmentController departmentController;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_department);


        inputDepartmentName = (TextInputEditText) findViewById(R.id.input_department_name);


        inputDepartmentName.setText(getIntent().getStringExtra("departmentName"));

        btnEditDepartment = (LinearLayout) findViewById(R.id.btn_edit_department);
        btnBack = (ImageView) findViewById(R.id.btn_back);

        departmentController = new DepartmentController();

        btnEditDepartment.setOnClickListener(v -> {
            Department departmentUpdates = new Department();
            String departmentName = inputDepartmentName.getText().toString();
            String oldName = getIntent().getStringExtra("departmentName");
            String departmentId = getIntent().getStringExtra("departmentId");

            CustomDialog customDialog = new CustomDialog(this, R.layout.custom_dialog_2);
            Boolean isChange = false;
            if (!oldName.equals(departmentName.strip())) {
                departmentUpdates.setName(departmentName);
                isChange = true;
            }
            if (isChange) {
                customDialog.show();
                customDialog.setQuestion("Xác nhận cập nhật");
                customDialog.setMessage("Bạn có chắc chắn muốn cập nhật");
                customDialog.setOnContinueClickListener((dialog, which) -> {
                    departmentController.updateDepartment(departmentId, departmentUpdates, task -> {
                        if (task.isSuccessful()) {
                            CustomDialog dialogNoti = new CustomDialog(this, R.layout.custom_dialog_2);
                            dialogNoti.show();
                            dialogNoti.setQuestion("Thông báo");
                            dialogNoti.setMessage("Cập nhật thành công");
                            dialogNoti.setOnContinueClickListener((dialog1, which1) -> {
                                finish();
                            });
                            dialogNoti.setOnCancelClickListener((dialog1, which1) -> {
                                finish();
                            });
                        } else {
                            CustomDialog dialogNoti = new CustomDialog(this, R.layout.custom_dialog_2);
                            dialogNoti.show();
                            dialogNoti.setQuestion("Thông báo");
                            dialogNoti.setMessage("Cập nhật không thành công, vui lòng kiểm tra kết nối hoặc nhập liêu");
                        }
                    });
                });
            } else {
                customDialog.dismiss();
                CustomDialog dialogNoti = new CustomDialog(this, R.layout.custom_dialog_2);
                dialogNoti.show();
                dialogNoti.setQuestion("Thông báo");
                dialogNoti.setMessage("Không có thông tin mới");
            }

        });

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}