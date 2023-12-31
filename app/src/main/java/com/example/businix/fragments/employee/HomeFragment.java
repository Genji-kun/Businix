package com.example.businix.fragments.employee;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.businix.activities.employee.LeaveActivity;
import com.example.businix.R;
import com.example.businix.activities.employee.SalaryActivity;
import com.example.businix.activities.employee.StatActivity;
import com.example.businix.activities.employee.TimeAttendanceActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LinearLayout btnToLeaveRequest, btnToAttendance, btnToSalary, btnToStat;

    private Boolean isProcessing = false;
    private TextView tvName;
    private String employeeName;

    private static final String TAG = "FragmentHome";


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tvName = view.findViewById(R.id.tv_name);

        btnToLeaveRequest = view.findViewById(R.id.btn_to_leave_request);
        btnToLeaveRequest.setOnClickListener(v -> {
            if (!isProcessing) {
                isProcessing = true;
                Intent intent = new Intent(getActivity(), LeaveActivity.class);
                startActivity(intent);
            }

        });

        btnToAttendance = view.findViewById(R.id.btn_to_attendance);
        btnToAttendance.setOnClickListener(v -> {
            if (!isProcessing) {
                isProcessing = true;
                Intent intent = new Intent(getActivity(), TimeAttendanceActivity.class);
                startActivity(intent);
            }
        });

        btnToSalary = view.findViewById(R.id.btn_to_salary);
        btnToSalary.setOnClickListener(v -> {
            if (!isProcessing) {
                isProcessing = true;
                Intent intent = new Intent(getActivity(), SalaryActivity.class);
                startActivity(intent);
            }
        });

        btnToStat = view.findViewById(R.id.btn_to_stats);
        btnToStat.setOnClickListener(v -> {
            if (!isProcessing) {
                isProcessing = true;
                Intent intent = new Intent(getActivity(), StatActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        isProcessing = false;
    }

    public void setEmployeeName(String employeeName) {

        this.employeeName = employeeName;
        if (tvName != null)
            tvName.setText(employeeName);

    }
}