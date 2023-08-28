package com.example.businix.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.businix.R;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.models.Employee;
import com.example.businix.ui.CustomDialog;
import com.example.businix.utils.PasswordHash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class VerifyOtpActivity extends AppCompatActivity {
    private TextView tvPhone, tvResend;
    private LinearLayout btnVerify;
    private ProgressBar progressBar;
    private TextView tvBtnVerify;
    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    private String verificationId;
    private Employee newEmpl;
    private EmployeeController employeeController;
    private CustomDialog customDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        tvPhone = (TextView) findViewById(R.id.tv_phone);
        tvPhone.setText(String.format("%s", getIntent().getStringExtra("phone_number")));

        inputCode1 = (EditText) findViewById(R.id.input_code_1);
        inputCode2 = (EditText) findViewById(R.id.input_code_2);
        inputCode3 = (EditText) findViewById(R.id.input_code_3);
        inputCode4 = (EditText) findViewById(R.id.input_code_4);
        inputCode5 = (EditText) findViewById(R.id.input_code_5);
        inputCode6 = (EditText) findViewById(R.id.input_code_6);
        setUpOTP();

        btnVerify = (LinearLayout) findViewById(R.id.btn_verify_otp);
        tvBtnVerify = (TextView) findViewById(R.id.tv_btn_verify_otp);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        tvResend = (TextView) findViewById(R.id.tv_resend);

        verificationId = getIntent().getStringExtra("verification_id");

        Serializable serializable = getIntent().getSerializableExtra("employee");
        if (serializable instanceof Employee) {
            newEmpl = (Employee) serializable;
//            newEmpl.setResendToken((PhoneAuthProvider.ForceResendingToken) getIntent().getExtras().get("resend_token"));
        } else {
            Toast.makeText(VerifyOtpActivity.this, "Có lỗi xãy ra", Toast.LENGTH_SHORT).show();
        }
        employeeController = new EmployeeController();
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputCode1.getText().toString().trim().isEmpty()
                        || inputCode2.getText().toString().trim().isEmpty()
                        || inputCode3.getText().toString().trim().isEmpty()
                        || inputCode4.getText().toString().trim().isEmpty()
                        || inputCode5.getText().toString().trim().isEmpty()
                        || inputCode6.getText().toString().trim().isEmpty()) {
                    Toast.makeText(VerifyOtpActivity.this, "Hãy nhận đầy đủ mã OTP", Toast.LENGTH_SHORT).show();
                    return;
                }
                String code = inputCode1.getText().toString() +
                        inputCode2.getText().toString() +
                        inputCode3.getText().toString() +
                        inputCode4.getText().toString() +
                        inputCode5.getText().toString() +
                        inputCode6.getText().toString();
                if (verificationId != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    tvBtnVerify.setVisibility(View.GONE);
                    btnVerify.setEnabled(false);

                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, code);
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            tvBtnVerify.setVisibility(View.VISIBLE);
                            if (task.isSuccessful()) {
                                newEmpl.setPassword(PasswordHash.hashPassword(newEmpl.getPassword()));
                                employeeController.addEmployee(newEmpl, addTask -> {
                                    if (addTask.isSuccessful()) {
                                        customDialog = new CustomDialog(VerifyOtpActivity.this, R.layout.custom_dialog_2);
                                        customDialog.show();
                                        customDialog.setQuestion("Thông báo");
                                        customDialog.setMessage("Đăng ký thành công, vui lòng liên hệ với Admin để phê duyệt tài khoản");
                                        customDialog.setTextBtnCancel("");
                                        customDialog.setOnContinueClickListener((dialog, which) -> {
                                            dialog.dismiss();
                                        });

                                        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                                        login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(login);
                                    } else {
                                        btnVerify.setEnabled(true);
                                        Toast.makeText(VerifyOtpActivity.this, "Đã có lỗi xảy ra", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                btnVerify.setEnabled(true);
                                Toast.makeText(VerifyOtpActivity.this, "Mã xác nhận OTP của bạn không đúng, vui lòng thử lại", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
            }
        });

        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber(tvPhone.getText().toString()) // Số điện thoại cần xác minh
                        .setTimeout(60L, TimeUnit.SECONDS) // Thời gian hiệu lực của mã OTP
                        .setActivity(VerifyOtpActivity.this) // Activity (để liên kết callback)
                        .setCallbacks(
                                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                        Toast.makeText(VerifyOtpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                        verificationId = newVerificationId;
//                                        newEmpl.setResendToken(forceResendingToken);
                                        Toast.makeText(VerifyOtpActivity.this, "Đã gửi lại mã OTP", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        ) // OnVerificationStateChangedCallbacks
                        .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });
    }

    private void setUpOTP() {
        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty())
                    inputCode2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty())
                    inputCode3.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty())
                    inputCode4.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty())
                    inputCode5.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty())
                    inputCode6.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}