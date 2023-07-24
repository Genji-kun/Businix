package com.example.businix.activities.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.businix.R;
import com.example.businix.controllers.PositionController;
import com.example.businix.models.Position;
import com.example.businix.ui.CustomDialog;
import com.example.businix.utils.FindListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

public class AdminEditPositionActivity extends AppCompatActivity {
    private LinearLayout btnEditPosition;
    private TextInputEditText inputPosName, inputSalary;
    private PositionController positionController;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_position);

        inputPosName = (TextInputEditText) findViewById(R.id.input_position_name);
        inputSalary = (TextInputEditText) findViewById(R.id.input_salary);

        inputPosName.setText(getIntent().getStringExtra("positionName"));
        inputSalary.setText(getIntent().getStringExtra("salary"));

        btnEditPosition = (LinearLayout) findViewById(R.id.btn_edit_position);
        btnBack = (ImageView) findViewById(R.id.btn_back);

        positionController = new PositionController();

        btnEditPosition.setOnClickListener(v -> {
            Position positionUpdates = new Position();
            String posName = inputPosName.getText().toString();
            String posSalary = inputSalary.getText().toString();
            String oldName = getIntent().getStringExtra("positionName");
            String oldSalary = getIntent().getStringExtra("salary");
            String posId = getIntent().getStringExtra("positionId");

            CustomDialog customDialog = new CustomDialog(this, R.layout.custom_dialog_2);
            Boolean isChange = false;
            if (!oldName.equals(posName.strip())) {
                positionUpdates.setName(posName);
                isChange = true;
            }
            if (!oldSalary.equals(posSalary.strip())) {
                positionUpdates.setSalary(Double.parseDouble(posSalary.strip()));
                isChange = true;
            }
            if (isChange) {
                customDialog.show();
                customDialog.setQuestion("Xác nhận cập nhật");
                customDialog.setMessage("Bạn có chắc chắn muốn cập nhật");
                customDialog.setOnContinueClickListener((dialog, which) -> {
                    positionController.updatePosition(posId, positionUpdates, task -> {
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

        btnBack.setOnClickListener(v ->

        {
            finish();
        });

    }
}