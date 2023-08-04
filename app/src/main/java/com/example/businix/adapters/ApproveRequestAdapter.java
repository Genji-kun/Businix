package com.example.businix.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.businix.R;
import com.example.businix.activities.admin.AdminEditEmployeeActivity;
import com.example.businix.activities.admin.AdminEmployeeManagementActivity;
import com.example.businix.activities.admin.CustomDialogAddEmployee;
import com.example.businix.activities.admin.CustomDialogEmployeeDetail;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.models.Employee;
import com.example.businix.models.Position;
import com.example.businix.models.Status;
import com.example.businix.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class ApproveRequestAdapter extends ArrayAdapter<Employee> {
    private List<Employee> employeeList;
    private Context context;
    EmployeeController employeeController;

    public ApproveRequestAdapter(Context context, int resource, List<Employee> employeeList) {
        super(context, resource, employeeList);
        this.employeeList = employeeList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.list_view_approve_request, null);
        }
        employeeController = new EmployeeController();
        Employee employee = employeeList.get(position);
        if (employee != null) {
            TextView tvEmplName = (TextView) view.findViewById(R.id.tv_empl_name);
            if (tvEmplName != null) {
                tvEmplName.setText(employee.getFullName());
            }
            TextView tvApplyDate = (TextView) view.findViewById(R.id.tv_join_date);
            if (tvApplyDate != null) {
                tvApplyDate.setText(tvApplyDate.getText().toString().replace("...", DateUtils.formatDate(employee.getCreateAt())));
            }
            TextView btnShowDetail = (TextView) view.findViewById(R.id.btn_show_detail);
        }

        TextView btnShowDetail = (TextView) view.findViewById(R.id.btn_show_detail);
        btnShowDetail.setOnClickListener(v -> {
            CustomDialogEmployeeDetail customDialogEmployeeDetail = new CustomDialogEmployeeDetail(getContext(), employee);
            customDialogEmployeeDetail.show();

        });

        return view;
    }
}
