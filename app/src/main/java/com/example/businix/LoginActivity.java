package com.example.businix;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {
    //BlurLayout blurLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //blurLayout = findViewById(R.id.blurLayout);
    }
    @Override
    protected void onStart() {
        super.onStart();
        //blurLayout.startBlur();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}