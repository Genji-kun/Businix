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

import com.example.businix.adapters.PositionAdapter;
import com.example.businix.controllers.PositionController;
import com.example.businix.models.Position;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class AdminPositionManagementActivity extends AppCompatActivity {

    private ImageView btnHome;
    private LinearLayout btnAddPosition;
    private TextInputEditText inputPosition;
    private PositionAdapter positionAdapter;
    private List<Position> positionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_position_management);

        ListView listView = (ListView) findViewById(R.id.list_view_position);
        PositionController positionController = new PositionController();
        positionController.getPositionList(new OnCompleteListener<List<Position>>() {
            @Override
            public void onComplete(@NonNull Task<List<Position>> task) {
                if (task.isSuccessful()) {
                    positionsList = task.getResult();
                    positionAdapter = new PositionAdapter(AdminPositionManagementActivity.this, R.layout.listview_position, positionsList);
                    listView.setAdapter(positionAdapter);
                } else {
                    // xử lý lỗi
                }
            }
        });

        inputPosition = (TextInputEditText) findViewById(R.id.input_position);
        inputPosition.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                if (positionAdapter != null) {
                    positionAdapter.getFilter().filter(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnAddPosition = (LinearLayout) findViewById(R.id.btn_add_position);
        btnAddPosition.setOnClickListener(v -> {
            Intent i = new Intent(AdminPositionManagementActivity.this, AdminAddPositionActivity.class);
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
        PositionController positionController = new PositionController();
        positionController.getPositionList(new OnCompleteListener<List<Position>>() {
            @Override
            public void onComplete(@NonNull Task<List<Position>> task) {
                if (task.isSuccessful()) {
                    positionsList = task.getResult();
                    if (positionAdapter != null) {
                        positionAdapter.clear(); // Xóa dữ liệu cũ trong adapter
                        positionAdapter.addAll(positionsList); // Thêm dữ liệu mới vào adapter
                        positionAdapter.notifyDataSetChanged(); // Cập nhật lại ListView
                    }
                } else {
                    // Xử lý lỗi
                }
            }
        });
    }
}