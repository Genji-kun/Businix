package com.example.businix.activities.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import com.example.businix.R;
import com.google.android.material.textfield.TextInputEditText;

public class AdminDepartmentManagementActivity extends AppCompatActivity {

    private ImageView btnHome;
    private LinearLayout btnAddDepartment;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private String[] departmentNames = {"IT","Marketing","Sale","Design"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_department_management);


        listView = (ListView) findViewById(R.id.list_view_department);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.listview_department, R.id.tv_department_name, departmentNames);
        listView.setAdapter(arrayAdapter);

        btnAddDepartment = (LinearLayout) findViewById(R.id.btn_add_department);
        btnAddDepartment.setOnClickListener(v -> {

        });

        btnHome = (ImageView) findViewById(R.id.btn_home);
        btnHome.setOnClickListener(v -> {
            finish();
        });


    }
}