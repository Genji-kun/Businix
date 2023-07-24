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
import com.example.businix.utils.FindListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class AdminAddPositionActivity extends AppCompatActivity {

    private LinearLayout btnAddPosition;
    private TextInputEditText inputPosName, inputSalary;
    private PositionController positionController;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_position);


        inputPosName = (TextInputEditText) findViewById(R.id.input_position_name);
        inputSalary = (TextInputEditText) findViewById(R.id.input_salary);
        btnAddPosition = (LinearLayout) findViewById(R.id.btn_add_position);
        btnBack = (ImageView) findViewById(R.id.btn_back);

        positionController = new PositionController();

        btnAddPosition.setOnClickListener(v -> {
            String positionName = inputPosName.getText().toString().strip();
            String salaryStr = inputSalary.getText().toString().strip();

            if(positionName.isBlank() && salaryStr.isBlank()){
                Toast.makeText(AdminAddPositionActivity.this, "Không được để trống nhập liệu", Toast.LENGTH_SHORT).show();
                return;
            }
            positionController.isExistedPosition(positionName, new FindListener() {
                @Override
                public void onFoundSuccess() {
                    Toast.makeText(AdminAddPositionActivity.this, "Tên chức vụ đã tồn tại", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNotFound() {
                    Double salary = Double.parseDouble(salaryStr);
                    Position newPosition = new Position();
                    newPosition.setName(positionName);
                    newPosition.setSalary(salary);

                    positionController.addPosition(newPosition, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AdminAddPositionActivity.this, "Thêm chức vụ thành công", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AdminAddPositionActivity.this, "Lỗi khi thêm chức vụ", Toast.LENGTH_SHORT).show();
                            }
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