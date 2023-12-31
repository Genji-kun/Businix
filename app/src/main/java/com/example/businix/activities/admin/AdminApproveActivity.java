package com.example.businix.activities.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.businix.R;
import com.example.businix.adapters.ApproveRequestAdapter;
import com.example.businix.adapters.DepartmentAdapter;
import com.example.businix.controllers.DepartmentController;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.models.Department;
import com.example.businix.models.Employee;
import com.example.businix.models.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AdminApproveActivity extends AppCompatActivity {
    private ImageView btnBack;
    private ApproveRequestAdapter approveRequestAdapter;
    private List<Employee> emplPendingList;
    private ProgressBar progressBar;
    private static ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approve);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        listView = (ListView) findViewById(R.id.list_view_approve);
        listView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        EmployeeController employeeController = new EmployeeController();
        employeeController.getEmployeeList(new OnCompleteListener<List<Employee>>() {
            @Override
            public void onComplete(@NonNull Task<List<Employee>> task) {
                if (task.isSuccessful()) {
                    List<Employee> employeeList = task.getResult();
                    emplPendingList = new ArrayList<>();
                    for (Employee employee : employeeList) {
                        if (employee.getStatus() == Status.PENDING)
                            emplPendingList.add(employee);
                    }
                    progressBar.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    approveRequestAdapter = new ApproveRequestAdapter(AdminApproveActivity.this, R.layout.list_view_approve_request, emplPendingList);
                    listView.setAdapter(approveRequestAdapter);
                } else {
                    // xử lý lỗi
                }
            }
        });


        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            finish();
        });

    }

    public static ListView getListView(){
        return listView;
    }
}