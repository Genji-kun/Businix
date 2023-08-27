package com.example.businix.activities.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.businix.R;
import com.example.businix.activities.employee.LeaveRequestHistoryActivity;
import com.example.businix.adapters.LeaveRequestHistoryAdapter;
import com.example.businix.controllers.LeaveRequestController;
import com.example.businix.models.Employee;
import com.example.businix.models.LeaveRequest;
import com.example.businix.models.LeaveRequestStatus;

import java.util.List;

public class AdminLeaveRequestActivity extends AppCompatActivity {

    private ImageView btnBack, btnHistory;
    private ListView listView;
    private ProgressBar progressBar;
    private LeaveRequestController leaveRequestController;
    private LeaveRequestHistoryAdapter leaveRequestHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_leave_request);

        btnBack = findViewById(R.id.btn_back);
        btnHistory = findViewById(R.id.btn_history);
        listView = findViewById(R.id.list_view_leave_request);
        progressBar = findViewById(R.id.progress_bar);
        listView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        leaveRequestController = new LeaveRequestController();
        leaveRequestController.getLeaveRequestListByStatus(LeaveRequestStatus.PENDING, task -> {
            if (task.isSuccessful()) {
                List<LeaveRequest> leaveRequestList = task.getResult();
                leaveRequestHistoryAdapter = new LeaveRequestHistoryAdapter(AdminLeaveRequestActivity.this, leaveRequestList);
                listView.setAdapter(leaveRequestHistoryAdapter);
                leaveRequestHistoryAdapter.setIsVisibleForHistory(true);
                leaveRequestHistoryAdapter.setIsVisible(true);
                progressBar.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnHistory.setOnClickListener(v -> {
            Intent i = new Intent(AdminLeaveRequestActivity.this, AdminViewHistoryLeaveRequestsActivity.class);
            startActivity(i);
        });
    }
}