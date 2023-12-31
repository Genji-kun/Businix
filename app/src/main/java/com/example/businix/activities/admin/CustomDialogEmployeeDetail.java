package com.example.businix.activities.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.businix.R;
import com.example.businix.adapters.ApproveRequestAdapter;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.models.Employee;
import com.example.businix.models.Status;
import com.example.businix.utils.DateUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class CustomDialogEmployeeDetail extends Dialog {
    private LinearLayout btnAccept;
    private LinearLayout btnDeny;
    private TextView tvEmplName, tvPhone, tvDOB, tvIdentityNum, tvJoinDate, tvGender, tvEmail, tvUsername;
    private Employee employee;
    private Context context;
    private ImageView imgAvatar;
    private ApproveRequestAdapter approveRequestAdapter;
    private List<Employee> emplPendingList;
    private int position;
    private ListView listView;

    public CustomDialogEmployeeDetail(Context context, Employee employee) {
        super(context);
        this.context = context;
        this.employee = employee;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Loại bỏ tiêu đề của dialog
        setContentView(R.layout.custom_dialog_employee_detail);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        imgAvatar = (ImageView) findViewById(R.id.img_avatar);
        tvEmplName = (TextView) findViewById(R.id.tv_empl_name);
        tvJoinDate = (TextView) findViewById(R.id.tv_join_date);
        tvPhone = (TextView) findViewById(R.id.tv_phone_number);
        tvDOB = (TextView) findViewById(R.id.tv_dob);
        tvGender = (TextView) findViewById(R.id.tv_gender);
        tvIdentityNum = (TextView) findViewById(R.id.tv_identity_number);
        tvEmail = (TextView) findViewById(R.id.tv_email);
        tvUsername = (TextView) findViewById(R.id.tv_username);

        btnAccept = (LinearLayout) findViewById(R.id.btn_accept_request);
        btnDeny = (LinearLayout) findViewById(R.id.btn_deny_request);


        if (employee.getAvatar() != null) {
            Glide.with(getContext()).load(employee.getAvatar()).into(imgAvatar);
        }
        if (employee.getFullName() != null) {
            tvEmplName.setText(employee.getFullName());
        }
        if (employee.getCreateAt() != null) {
            tvJoinDate.setText(DateUtils.formatDate(employee.getCreateAt()));
        }
        if (employee.getPhone() != null) {
            tvPhone.setText(employee.getPhone());
        }
        if (employee.getDob() != null) {
            tvDOB.setText(DateUtils.formatDate(employee.getDob()));
        }
        if (employee.getGender() != null) {
            tvGender.setText(employee.getGender().toString());
        }
        if (employee.getIdentityNum() != null) {
            tvIdentityNum.setText(employee.getIdentityNum());
        }
        if (employee.getEmail() != null) {
            tvEmail.setText(employee.getEmail());
        }
        if (employee.getUsername() != null) {
            tvUsername.setText(employee.getUsername());
        }


        EmployeeController employeeController = new EmployeeController();
        employeeController.getEmployeeList(new OnCompleteListener<List<Employee>>() {
            @Override
            public void onComplete(@NonNull Task<List<Employee>> task) {
                if (task.isSuccessful()) {
                    position = 0;
                    List<Employee> employeeList = task.getResult();
                    emplPendingList = new ArrayList<>();
                    for (Employee empl : employeeList) {
                        if (empl.getStatus() == Status.PENDING)
                            emplPendingList.add(empl);
                        if (empl.getId().equals(employee.getId())) {
                            position = emplPendingList.indexOf(empl);
                        }
                    }
                    approveRequestAdapter = new ApproveRequestAdapter(context, R.layout.list_view_approve_request, emplPendingList);
                    listView = AdminApproveActivity.getListView();
                    listView.setAdapter(approveRequestAdapter);
                } else {
                    // xử lý lỗi
                }
            }
        });

        btnAccept.setOnClickListener(v -> {
            employee.setStatus(Status.ACTIVE);
            employeeController.updateEmployee(employee.getId(), employee, task -> {
                if (task.isSuccessful()) {
                    emplPendingList.remove(position);
                    Toast.makeText(context, "Duyệt thành công!", Toast.LENGTH_SHORT).show();
                    dismiss();
                    approveRequestAdapter.notifyDataSetChanged();
                } else {
                    Log.e("AdminEmployeeManagement", "Error update employee");
                    Toast.makeText(context, "Lỗi khi thực hiện!", Toast.LENGTH_SHORT).show();
                }
            });

        });
        btnDeny.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_2, null);
            builder.setView(dialogView);

            AlertDialog alertDialog = builder.create();
            TextView tvQuestion = (TextView) dialogView.findViewById(R.id.question);
            tvQuestion.setText("Từ chối nhân viên?");
            TextView tvMsg = (TextView) dialogView.findViewById(R.id.msg);
            tvMsg.setText("Việc từ chối tài khoản sẽ thực hiện thao tác xóa giải phóng bộ nhớ, hãy cân nhắc trước khi xóa");
            alertDialog.show();

            TextView btnConfirmDelete = (TextView) dialogView.findViewById(R.id.btn_continue);
            btnConfirmDelete.setOnClickListener(v1 -> {
                // Xác nhận xóa, gọi phương thức deletePosition
                employeeController.deleteEmployee(employee.getId(), task -> {
                    if (task.isSuccessful()) {
                        emplPendingList.remove(position);
                        approveRequestAdapter.notifyDataSetChanged();
                        Toast.makeText(context, "Từ chối thành công!", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    } else {
                        Log.e("AdminEmployeeManagement", "Error deleting employee");
                        Toast.makeText(context, "Lỗi khi thực hiện!", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            TextView btnCancelDelete = (TextView) dialogView.findViewById(R.id.btn_cancel);
            btnCancelDelete.setOnClickListener(v1 -> {
                alertDialog.dismiss();
            });

        });

        setCanceledOnTouchOutside(true);
    }

}
