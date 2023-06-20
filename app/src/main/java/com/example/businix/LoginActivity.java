package com.example.businix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {
    //BlurLayout blurLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MotionLayout motionLayout = findViewById(R.id.animLayout);
        motionLayout.setTransition(R.id.transition);
    }

}