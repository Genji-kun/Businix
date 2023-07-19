package com.example.businix.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.businix.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SendOtpActivity extends AppCompatActivity {
    private TextView tvPhone, tvBtnSendOTP;
    private LinearLayout btnSendOTP;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otp);

        tvPhone = (TextView) findViewById(R.id.tv_phone);
        btnSendOTP = (LinearLayout) findViewById(R.id.btn_send_otp);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        tvBtnSendOTP = (TextView) findViewById(R.id.tv_btn_send_otp);

        tvPhone.setText(String.format("+84"+ "%s", getIntent().getStringExtra("phone_number")));
        btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvBtnSendOTP.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                btnSendOTP.setEnabled(false);

                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber(tvPhone.getText().toString()) // Số điện thoại cần xác minh
                        .setTimeout(60L, TimeUnit.SECONDS) // Thời gian hiệu lực của mã OTP
                        .setActivity(SendOtpActivity.this) // Activity (để liên kết callback)
                        .setCallbacks(
                                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                        tvBtnSendOTP.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                        btnSendOTP.setEnabled(true);
                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                        tvBtnSendOTP.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                        btnSendOTP.setEnabled(true);
                                        Toast.makeText(SendOtpActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                        System.out.println(e.getMessage());
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                        tvBtnSendOTP.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                        btnSendOTP.setEnabled(true);
                                        Intent i = new Intent(SendOtpActivity.this, VerifyOtpActivity.class);
                                        i.putExtra("phone_number", tvPhone.getText());
                                        i.putExtra("verification_id", verificationId);
                                        startActivity(i);
                                    }
                                }
                        ) // OnVerificationStateChangedCallbacks
                        .build();
                PhoneAuthProvider.verifyPhoneNumber(options);

            }
        });
    }
}