package com.example.businix.activities.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.businix.R;
import com.example.businix.adapters.EmployeeAdapter;
import com.example.businix.adapters.EmployeeMemberAdapter;
import com.example.businix.controllers.DepartmentController;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.controllers.PositionController;
import com.example.businix.models.Department;
import com.example.businix.models.Employee;
import com.example.businix.models.Position;
import com.example.businix.models.UserRole;

import java.util.ArrayList;
import java.util.List;

public class AdminAddListEmployeeActivity extends AppCompatActivity {

    private ImageView btnBack;
    private LinearLayout btnAdd, btnAddListEmployee;
    private ArrayList<Employee> employeeList;
    private EmployeeMemberAdapter employeeMemberAdapter;
    private EmployeeController employeeController;
    private ListView listView;
    private TextView tvAddListEmployee;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_list_employee);

        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnAdd = (LinearLayout) findViewById(R.id.btn_add);
        btnAddListEmployee = (LinearLayout) findViewById(R.id.btn_add_list_employee);
        tvAddListEmployee = (TextView) findViewById(R.id.tv_btn_add_list_employee);


        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        listView = (ListView) findViewById(R.id.list_view_employee);
        employeeList = new ArrayList<>();
        employeeList.add(new Employee());
        employeeMemberAdapter = new EmployeeMemberAdapter(this, R.layout.list_view_employee_member, employeeList);
        listView.setAdapter(employeeMemberAdapter);

        btnAdd.setOnClickListener(v -> {
            Employee employee = new Employee();
            employeeList.add(employee);
            employeeMemberAdapter.notifyDataSetChanged();
        });

        employeeController = new EmployeeController();
        btnAddListEmployee.setOnClickListener(v -> {
            processingView();
            EmployeeMemberAdapter adapter = (EmployeeMemberAdapter) listView.getAdapter();
            for (int i = 0; i < employeeList.size(); i++) {
                Employee empl = employeeList.get(i);
                if (empl.getFullName() == null) {
                    showNotification("Bạn chưa điền họ tên nhân viên");
                    normalView();
                    return;
                }
                if (empl.getUsername() == null) {
                    showNotification("Bạn chưa điền username");
                    normalView();
                    return;
                }
                if (adapter.getLayoutUsername(i).getError() != null && !adapter.getLayoutUsername(i).getError().toString().isEmpty()) {
                    showNotification("Username không hợp lệ");
                    normalView();
                    return;
                }
                if (empl.getPassword() == null) {
                    showNotification("Bạn chưa điền username");
                    normalView();
                    return;
                }
                if (empl.getDepartment() == null) {
                    showNotification("Bạn chưa chọn phòng ban");
                    normalView();
                    return;
                }
                if (empl.getPosition() == null) {
                    showNotification("Bạn chưa chọn vị trí");
                    normalView();
                    return;
                }
                if (empl.getStatus() == null) {
                    showNotification("Bạn chưa chọn trạng thái");
                    normalView();
                    return;
                }
            }
            if (!checkDuplicateUsername()) {
                showNotification("Có username bị trùng trong danh sách");
                normalView();
                return;
            }
            employeeController.addEmployeeList(employeeList, task -> {
                if (task.isSuccessful()) {
                    showNotification("Thêm danh sách thành công");
                    normalView();
                    finish();
                } else {
                    Toast.makeText(AdminAddListEmployeeActivity.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                    normalView();
                }
            });
        });

        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    public void processingView() {
        progressBar.setVisibility(View.VISIBLE);
        tvAddListEmployee.setVisibility(View.GONE);
        btnAddListEmployee.setEnabled(false);
        btnAddListEmployee.setBackgroundColor(Color.LTGRAY);
        btnBack.setEnabled(false);
    }

    public void normalView() {
        progressBar.setVisibility(View.GONE);
        tvAddListEmployee.setVisibility(View.VISIBLE);
        btnAddListEmployee.setEnabled(true);
        btnAddListEmployee.setBackgroundColor(getResources().getColor(R.color.light_purple));
        btnBack.setEnabled(true);
    }

    public boolean checkDuplicateUsername() {
        for (int i = 0; i < employeeList.size(); i++) {
            Employee e = employeeList.get(i);
            String un = e.getUsername().strip().toLowerCase();
            for (int j = i + 1; j < employeeList.size(); j++) {
                Employee e2 = employeeList.get(j);
                String un2 = e2.getUsername().strip().toLowerCase();
                if (un.equals(un2)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void showNotification(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminAddListEmployeeActivity.this);
        View dialogView = LayoutInflater.from(AdminAddListEmployeeActivity.this).inflate(R.layout.custom_dialog_2, null);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        TextView tvQuestion = (TextView) dialogView.findViewById(R.id.question);
        tvQuestion.setText("Sai thông tin");
        TextView tvMsg = (TextView) dialogView.findViewById(R.id.msg);
        tvMsg.setText(msg);
        TextView btnCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        btnCancel.setVisibility(View.GONE);
        alertDialog.show();

        TextView btnConfirm = (TextView) dialogView.findViewById(R.id.btn_continue);
        btnConfirm.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }
}