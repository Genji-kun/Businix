package com.example.businix.activities.employee;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.businix.R;
import com.example.businix.ui.ActionBar;

public class StatActivity extends ActionBar {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        toolbar = findViewById(R.id.toolbar);
        setSupportMyActionBar("Thống kê", true, false);


    }
}