package com.example.businix.activities.employee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.businix.R;
import com.example.businix.activities.LoginActivity;
import com.example.businix.activities.SendOtpActivity;
import com.example.businix.activities.VerifyOtpActivity;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.models.Employee;
import com.example.businix.ui.CustomDialog;
import com.example.businix.utils.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber("+84792146269") // Số điện thoại cần xác minh
                .setTimeout(60L, TimeUnit.SECONDS) // Thời gian hiệu lực của mã OTP
                .setActivity(ChangePasswordActivity.this) // Activity (để liên kết callback)
                .setCallbacks(
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                Toast.makeText(ChangePasswordActivity.this, "thafnh cong", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(ChangePasswordActivity.this, "khong thafnh cong", Toast.LENGTH_LONG).show();
                                System.out.println(e.getMessage());
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                Toast.makeText(ChangePasswordActivity.this, "dang gui code ne", Toast.LENGTH_LONG).show();
                                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, "234567");
                                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ChangePasswordActivity.this, "benh roi", Toast.LENGTH_LONG).show();
                                        } else {
                                        }

                                    }
                                });
                            }
                        }
                )
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
}