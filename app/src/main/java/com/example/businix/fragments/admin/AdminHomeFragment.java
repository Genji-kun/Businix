package com.example.businix.fragments.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.businix.R;
import com.example.businix.activities.admin.AdminApproveActivity;
import com.example.businix.activities.admin.AdminDepartmentManagementActivity;
import com.example.businix.activities.admin.AdminEmployeeManagementActivity;
import com.example.businix.activities.admin.AdminLeaveRequestActivity;
import com.example.businix.activities.admin.AdminPositionManagementActivity;
import com.example.businix.activities.admin.AdminViewChartActivity;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.controllers.LeaveRequestController;
import com.example.businix.models.LeaveRequest;
import com.example.businix.models.LeaveRequestStatus;
import com.example.businix.models.Status;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminHomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminHomeFragment() {
        // Required empty public constructor
    }

    public static AdminHomeFragment newInstance(String param1, String param2) {
        AdminHomeFragment fragment = new AdminHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private LinearLayout btnEmployee, btnPosition, btnDepartment, btnApprove, btnLeaveRequest, btnViewChart;
    private TextView tvCountApprove, tvCountLeaveRequest;
    private EmployeeController employeeController;
    private LeaveRequestController leaveRequestController;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);
        btnEmployee = (LinearLayout) view.findViewById(R.id.btn_employee);
        btnPosition = (LinearLayout) view.findViewById(R.id.btn_position);
        btnDepartment = (LinearLayout) view.findViewById(R.id.btn_department);
        btnApprove = (LinearLayout) view.findViewById(R.id.btn_approve);
        btnLeaveRequest = (LinearLayout) view.findViewById(R.id.btn_leave_request);
        btnViewChart = (LinearLayout) view.findViewById(R.id.btn_view_chart);

        tvCountApprove = (TextView) view.findViewById(R.id.tv_count_approve);
        tvCountLeaveRequest = (TextView) view.findViewById(R.id.tv_count_leave_request);

        employeeController = new EmployeeController();
        leaveRequestController = new LeaveRequestController();

        employeeController.getEmployeeListByStatus(Status.PENDING, task -> {
            if (task.isSuccessful()) {
                tvCountApprove.setText(String.valueOf(task.getResult().size()));
            }
        });

        leaveRequestController.getLeaveRequestListByStatus(LeaveRequestStatus.PENDING, task -> {
            if (task.isSuccessful()) {
                tvCountLeaveRequest.setText(String.valueOf(task.getResult().size()));
            }
        });

        btnEmployee.setOnClickListener(v -> {
            Intent empl = new Intent(getActivity(), AdminEmployeeManagementActivity.class);
            startActivity(empl);
        });

        btnPosition.setOnClickListener(v -> {
            Intent pos = new Intent(getActivity(), AdminPositionManagementActivity.class);
            startActivity(pos);
        });

        btnDepartment.setOnClickListener(v -> {
            Intent department = new Intent(getActivity(), AdminDepartmentManagementActivity.class);
            startActivity(department);
        });

        btnViewChart.setOnClickListener(v -> {
            Intent chart = new Intent(getActivity(), AdminViewChartActivity.class);
            startActivity(chart);
        });

        btnApprove.setOnClickListener(v -> {
            Intent approve = new Intent(getActivity(), AdminApproveActivity.class);
            startActivity(approve);
        });

        btnLeaveRequest.setOnClickListener(v -> {
            Intent approve = new Intent(getActivity(), AdminLeaveRequestActivity.class);
            startActivity(approve);
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        employeeController.getEmployeeListByStatus(Status.PENDING, task -> {
            if (task.isSuccessful()) {
                tvCountApprove.setText(String.valueOf(task.getResult().size()));
            }
        });

        leaveRequestController.getLeaveRequestListByStatus(LeaveRequestStatus.PENDING, task -> {
            if (task.isSuccessful()) {
                tvCountLeaveRequest.setText(String.valueOf(task.getResult().size()));
            }
        });
    }
}