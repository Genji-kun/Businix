package com.example.businix.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.businix.R;
import com.example.businix.activities.LoginActivity;
import com.example.businix.activities.SendOtpActivity;
import com.example.businix.activities.VerifyOtpActivity;
import com.example.businix.activities.employee.EmployeeActivity;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.models.Employee;
import com.example.businix.ui.ActionBar;
import com.example.businix.ui.CustomDialog;
import com.example.businix.utils.LoginManager;
import com.example.businix.utils.PasswordHash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordActivity extends ActionBar {
    private LoginManager loginManager;
    private TextInputEditText currentPassword, newPassword;
    private Button btn;
    private EmployeeController employeeController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        loginManager = new LoginManager(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportMyActionBar("Đổi mật khẩu", true, false);
        btn = findViewById(R.id.btn_confirm);
        currentPassword = findViewById(R.id.input_current_password);
        newPassword = findViewById(R.id.input_new_password);
        employeeController = new EmployeeController();
        btn.setOnClickListener(view -> {
            employeeController.getEmployeeById(loginManager.getLoggedInUserId(), task -> {
                Employee employee = task.getResult();
                String currentPass = currentPassword.getText().toString();
                if (!PasswordHash.checkPassword(currentPass, employee.getPassword())) {
                    CustomDialog confirmDialog = new CustomDialog(this, R.layout.custom_dialog_2);
                    confirmDialog.show();
                    confirmDialog.setQuestion("Không thể đổi mật khẩu");
                    confirmDialog.setMessage("Mật khẩu hiện tại bạn nhập không chính xác");
                    return;

                }
                String newPass = newPassword.getText().toString();
                Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*_\\-+=])[A-Za-z\\d!@#$%^&*_\\-+=]+$");
                Matcher matcher = pattern.matcher(newPass);
                if (matcher.find()) {
                    Employee emp = new Employee();
                    emp.setPassword(PasswordHash.hashPassword(newPass));
                    employeeController.updateEmployee(employee.getId(), emp, task1 -> {
                        if (task1.isSuccessful()) {
                            CustomDialog confirmDialog = new CustomDialog(this, R.layout.custom_dialog_2);
                            confirmDialog.show();
                            confirmDialog.setQuestion("Thành công");
                            confirmDialog.setMessage("Đổi mật khẩu thành công");
                            confirmDialog.setOnContinueClickListener((dialogInterface, i) -> {
                                finish();
                            });
                            confirmDialog.setOnCancelClickListener((dialogInterface, i) -> {
                                finish();
                            });
                        }
                    });
                }
                else {
                    CustomDialog confirmDialog = new CustomDialog(this, R.layout.custom_dialog_2);
                    confirmDialog.show();
                    confirmDialog.setQuestion("Không thể đổi mật khẩu");
                    confirmDialog.setMessage("Mật khẩu mới không đúng định dạng, phải có ít nhất một  chữ thường, chữ hoa, số và kí tự");
                }

            });
        });
    }
}