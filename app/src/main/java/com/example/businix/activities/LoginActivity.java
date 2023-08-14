package com.example.businix.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.businix.R;
import com.example.businix.activities.admin.AdminActivity;
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
    private TextView tvRegister, tvBtnLogin;
    private CustomDialog customDialog;
    private EmployeeController employeeController;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        employeeController = new EmployeeController();

        motionLayout = (MotionLayout) findViewById(R.id.animLayout);
        tvRegister = (TextView) findViewById(R.id.tv_register);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogin = (LinearLayout) findViewById(R.id.btn_login);
        tvBtnLogin = (TextView) findViewById(R.id.tv_btn_login);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

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
            processingView();
            btnLogin.setEnabled(false);
            String username = txtUsername.getText().toString().toLowerCase(Locale.ROOT).trim();
            String password = txtPassword.getText().toString();

            if (username.isEmpty() || username.isBlank() || password.isEmpty())
            {
                normalView();
                customDialog.show();
                customDialog.setQuestion("Username hoặc password rỗng");
                customDialog.setMessage("Vui lòng nhập đầy đủ thông tin để đăng nhập");
                customDialog.getBtnCancel().setVisibility(View.GONE);
                customDialog.setOnContinueClickListener((dialog, which) -> {
                    dialog.dismiss();
                });
                btnLogin.setEnabled(true);
                return;
            }

            employeeController.checkUser(username, password, new AuthenticationListener() {
                @Override
                public void onUsernameNotFound() {
                    normalView();
                    customDialog.show();
                    customDialog.setQuestion("Username không đúng");
                    customDialog.setMessage("Không tìm thấy tài khoản với username là " + username + ". Vui lòng thử lại.");
                    customDialog.getBtnCancel().setVisibility(View.GONE);
                    customDialog.setOnContinueClickListener((dialog, which) -> {
                        dialog.dismiss();
                    });
                    btnLogin.setEnabled(true);
                }

                @Override
                public void onPasswordIncorrect() {
                    normalView();
                    customDialog.show();
                    customDialog.setQuestion("Mật khẩu không đúng");
                    customDialog.setMessage("Mật khẩu xác thực không khớp với tài khoản " + username + ". Vui lòng thử lại.");
                    customDialog.getBtnCancel().setVisibility(View.GONE);
                    customDialog.setOnContinueClickListener((dialog, which) -> {
                        dialog.dismiss();
                    });
                    btnLogin.setEnabled(true);
                }

                @Override
                public void onAuthenticationSuccess(Employee employee) {
                    LoginManager loginManager = new LoginManager(LoginActivity.this);
                    loginManager.setLoggedInUserId(employee.getId());
                    loginManager.setLoggedInRole(employee.getUserRole().name());
                    if(employee.getUserRole().name().equals("USER"))
                        goToEmployeeActivity();
                    else
                        goToAdminActivity();
                    finish();
                }

                @Override
                public void onUserPending() {
                    normalView();
                    customDialog.show();
                    customDialog.setQuestion("Không thể truy cập");
                    customDialog.setMessage("Bạn chưa được Administrator cấp quyền, hãy liên hệ để được truy cập");
                    customDialog.getBtnCancel().setVisibility(View.GONE);
                    customDialog.setOnContinueClickListener((dialog, which) -> {
                        dialog.dismiss();
                    });
                    btnLogin.setEnabled(true);
                }

                @Override
                public void onUserInactive() {
                    normalView();
                    customDialog.show();
                    customDialog.setQuestion("Không thể truy cập");
                    customDialog.setMessage("Tài khoản đã bị vô hiệu hóa bởi Administrator");
                    customDialog.getBtnCancel().setVisibility(View.GONE);
                    customDialog.setOnContinueClickListener((dialog, which) -> {
                        dialog.dismiss();
                    });
                    btnLogin.setEnabled(true);
                }
            });

        });
    }

    private void goToEmployeeActivity() {
        Intent i = new Intent(LoginActivity.this, EmployeeActivity.class);
        startActivity(i);
    }

    private void goToAdminActivity() {
        Intent i = new Intent(LoginActivity.this, AdminActivity.class);
        startActivity(i);
    }

    public void processingView() {
        progressBar.setVisibility(View.VISIBLE);
        tvBtnLogin.setVisibility(View.GONE);
        btnLogin.setEnabled(false);
        btnLogin.setBackgroundColor(Color.LTGRAY);
    }

    public void normalView() {
        progressBar.setVisibility(View.GONE);
        tvBtnLogin.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(true);
        btnLogin.setBackgroundColor(getResources().getColor(R.color.light_purple));
    }

}