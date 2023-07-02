package com.example.businix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

public class SignUpActivity extends AppCompatActivity {

    private LinearLayout btnNextStep;
    private TextView tvGetHelp;
    private ImageView btnBack;
    private int stepIndex;
    private Dialog dialogAlert;
    private Button btnContinue, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        btnNextStep = (LinearLayout) findViewById(R.id.btnNextStep);
        tvGetHelp = (TextView) findViewById(R.id.tvGetHelp);
        btnBack = (ImageView) findViewById(R.id.btnBack);

        // Dialog alert
        dialogAlert = new Dialog(SignUpActivity.this);
        dialogAlert.setContentView(R.layout.custom_dialog);
        dialogAlert.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogAlert.setCancelable(false);
        dialogAlert.getWindow().setWindowAnimations(R.style.animation);
        btnContinue = (Button) dialogAlert.findViewById(R.id.btnContinue);
        btnCancel = (Button) dialogAlert.findViewById(R.id.btnCancel);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAlert.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        // Setup Fragment
        stepIndex = 1;
        Fragment stepOne = new SignUpStepOneFragment();
        changeFragment(stepOne);
        btnNextStep.setOnClickListener(v -> {
            stepIndex += 1;
            switch (stepIndex){
                case 1:
                    tvGetHelp.setVisibility(View.VISIBLE);
                case 2:
                    Fragment stepTwo = new SignUpStepTwoFragment();
                    changeFragment(stepTwo);
                    tvGetHelp.setVisibility(View.INVISIBLE);
                case 3:
                case 4:
                default:
            }
        });

        btnBack.setOnClickListener(v -> {

        });
    }

    private void changeFragment(Fragment newFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.input_container, newFragment);
        fragmentTransaction.commit();
    }
}