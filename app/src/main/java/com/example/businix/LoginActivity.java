package com.example.businix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    private MotionLayout motionLayout;
    private EditText txtUsername;
    private EditText txtPassword;
    private LinearLayout btnLogin;
    private TextView tvRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        motionLayout = (MotionLayout) findViewById(R.id.animLayout);
        tvRegister = (TextView) findViewById(R.id.tvRegister);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogin = (LinearLayout) findViewById(R.id.btnLogin);

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
            Intent i = new Intent(LoginActivity.this, EmployeeActivity.class);
            startActivity(i);
        });
    }

}