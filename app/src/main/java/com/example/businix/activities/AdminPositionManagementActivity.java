package com.example.businix.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.businix.R;

public class AdminPositionManagementActivity extends AppCompatActivity {

    private ImageView btnHome;
    private LinearLayout btnAddEmployee;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private String[] positionNames = {"Software Engineer","Tester","Fullstack Developer","Design"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_position_management);
    }
}