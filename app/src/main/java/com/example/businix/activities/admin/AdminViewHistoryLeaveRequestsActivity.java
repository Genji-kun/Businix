package com.example.businix.activities.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.businix.R;
import com.example.businix.adapters.LeaveRequestHistoryAdapter;
import com.example.businix.controllers.LeaveRequestController;
import com.example.businix.models.LeaveRequest;
import com.example.businix.models.LeaveRequestStatus;

import java.util.List;

public class AdminViewHistoryLeaveRequestsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private ListView listView;
    private ProgressBar progressBar;
    private LeaveRequestController leaveRequestController;
    private LeaveRequestHistoryAdapter leaveRequestHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_history_leave_requests);

        btnBack = findViewById(R.id.btn_back);
        listView = findViewById(R.id.list_view_leave_request);
        progressBar = findViewById(R.id.progress_bar);
        listView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        leaveRequestController = new LeaveRequestController();
        leaveRequestController.getLeaveRequestList(task -> {
            if (task.isSuccessful()) {
                List<LeaveRequest> leaveRequestList = task.getResult();
                leaveRequestHistoryAdapter = new LeaveRequestHistoryAdapter(AdminViewHistoryLeaveRequestsActivity.this, leaveRequestList);
                listView.setAdapter(leaveRequestHistoryAdapter);
                leaveRequestHistoryAdapter.setIsVisibleForHistory(true);
                progressBar.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}