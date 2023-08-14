package com.example.businix.activities.employee;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.businix.R;
import com.example.businix.adapters.LeaveRequestHistoryAdapter;
import com.example.businix.controllers.LeaveRequestController;
import com.example.businix.models.LeaveRequest;
import com.example.businix.ui.ActionBar;

import java.util.List;

public class LeaveRequestHistoryActivity  extends ActionBar {
    private ListView listViewRequests;
    private LeaveRequestHistoryAdapter adapter;
    private LeaveRequestController leaveRequestController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_request_history);

        toolbar = findViewById(R.id.toolbar);
        setSupportMyActionBar("Các đơn nghỉ đã gửi", true, false);
        listViewRequests = findViewById(R.id.list_view_requests);
        leaveRequestController = new LeaveRequestController();
        leaveRequestController.getLeaveRequestList(task -> {
            if(task.isSuccessful()) {
                List<LeaveRequest> leaveRequestList = task.getResult();
                adapter = new LeaveRequestHistoryAdapter(LeaveRequestHistoryActivity.this, leaveRequestList);
                try {
                    listViewRequests.setAdapter(adapter);
                } catch (Exception e) {
                    Log.e("BUG", "LỖI", e);
                }
            }
        });
    }
}