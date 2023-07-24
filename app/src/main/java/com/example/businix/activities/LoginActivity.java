package com.example.businix.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.businix.R;
import com.example.businix.activities.employee.EmployeeActivity;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.models.Employee;
import com.example.businix.ui.CustomDialog;
import com.example.businix.utils.AuthenticationListener;
import com.example.businix.utils.LoginManager;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    private MotionLayout motionLayout;
    private EditText txtUsername;
    private EditText txtPassword;
    private LinearLayout btnLogin;
    private TextView tvRegister;
    private CustomDialog customDialog;
    private EmployeeController employeeController;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        employeeController = new EmployeeController();

        motionLayout = (MotionLayout) findViewById(R.id.animLayout);
        tvRegister = (TextView) findViewById(R.id.tvRegister);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogin = (LinearLayout) findViewById(R.id.btnLogin);

        customDialog = new CustomDialog(LoginActivity.this, R.layout.custom_dialog_2);

        motionLayout.post(new Runnable() {
            @Override
            public void run() {
                motionLayout.transitionToEnd();
            }

        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent introduce = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(introduce);
            }
        });

        btnLogin.setOnClickListener(v -> {
            btnLogin.setEnabled(false);
            String username = txtUsername.getText().toString().toLowerCase(Locale.ROOT).trim();
            String password = txtPassword.getText().toString();

            if (username.isEmpty() || username.isBlank() || password.isEmpty())
            {
                customDialog.show();
                customDialog.setQuestion("Username hoặc password rỗng");
                customDialog.setMessage("Vui lòng nhập đầy đủ thông tin để đăng nhập");
                customDialog.setOnCancelClickListener((dialog, which) -> {
                    dialog.dismiss();
                });
                customDialog.setOnContinueClickListener((dialog, which) -> {
                    dialog.dismiss();
                });
                btnLogin.setEnabled(true);
                return;
            }

            employeeController.checkUser(username, password, new AuthenticationListener() {
                @Override
                public void onUsernameNotFound() {
                    customDialog.show();
                    customDialog.setQuestion("Username không đúng");
                    customDialog.setMessage("Không tìm thấy tài khoản với username là " + username + ". Vui lòng thử lại.");
                    customDialog.setOnCancelClickListener((dialog, which) -> {
                        dialog.dismiss();
                    });
                    customDialog.setOnContinueClickListener((dialog, which) -> {
                        dialog.dismiss();
                    });
                    btnLogin.setEnabled(true);
                }

                @Override
                public void onPasswordIncorrect() {
                    customDialog.show();
                    customDialog.setQuestion("Mật khẩu không đúng");
                    customDialog.setMessage("Mật khẩu xác thực không khớp với tài khoản " + username + ". Vui lòng thử lại.");
                    customDialog.setOnCancelClickListener((dialog, which) -> {
                        dialog.dismiss();
                    });
                    customDialog.setOnContinueClickListener((dialog, which) -> {
                        dialog.dismiss();
                    });
                    btnLogin.setEnabled(true);
                }

                @Override
                public void onAuthenticationSuccess(Employee employee) {
                    LoginManager loginManager = new LoginManager(LoginActivity.this);
                    loginManager.setLoggedInUserId(employee.getId());
                    goToEmployeeActivity();
                    finish();
                }
            });

        });
    }

    private void goToEmployeeActivity() {
        Intent i = new Intent(LoginActivity.this, EmployeeActivity.class);
        startActivity(i);
    }

}