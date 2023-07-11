package com.example.businix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private LinearLayout btnNextStep;
    private TextView tvGetHelp, tvBtnNextStep;
    private ImageView btnBack;
    private int stepIndex;
    private Dialog dialogAlert;
    private Button btnContinue, btnCancel;
    private Boolean isTransitioning;

    private TextInputLayout layoutConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        btnNextStep = (LinearLayout) findViewById(R.id.btnNextStep);
        tvGetHelp = (TextView) findViewById(R.id.tvGetHelp);
        tvBtnNextStep = (TextView) findViewById(R.id.tvBtnNextStep);
        btnBack = (ImageView) findViewById(R.id.btnBack);


        // Dialog alert
        dialogAlert = new Dialog(SignUpActivity.this);
        dialogAlert.setContentView(R.layout.custom_dialog);
        dialogAlert.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogAlert.setCancelable(false);
        dialogAlert.getWindow().setWindowAnimations(R.style.animation);
        btnContinue = (Button) dialogAlert.findViewById(R.id.btnContinue);
        btnCancel = (Button) dialogAlert.findViewById(R.id.btnCancel);

        isTransitioning = false;

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAlert.dismiss();
            }
        });

        // Setup Fragment
        stepIndex = 0;
        Fragment intro = new IntroductionFragment();
        // Step One
        Fragment stepOne = new SignUpStepOneFragment();
        SignUpStepOneFragment stepOneFragment = (SignUpStepOneFragment) stepOne;


        // Step Two
        Fragment stepTwo = new SignUpStepTwoFragment();
        SignUpStepTwoFragment stepTwoFragment = (SignUpStepTwoFragment) stepTwo;


        // Step Three
        Fragment stepThree = new SignUpStepThreeFragment();
        SignUpStepThreeFragment stepThreeFragment = (SignUpStepThreeFragment) stepThree;

        // Change fragment to introduction
        changeFragment(intro);
        Intent i = new Intent(SignUpActivity.this, SendOtpActivity.class);

        tvGetHelp.setVisibility(View.INVISIBLE);
        btnNextStep.setOnClickListener(v -> {
            if (!isTransitioning) {
                isTransitioning = true;
                stepIndex += 1;
                switch (stepIndex) {
                    case 1:
                        changeFragment(stepOne);
                        tvGetHelp.setVisibility(View.VISIBLE);
                        tvBtnNextStep.setText("Tiếp tục");
                        break;
                    case 2:
                        int rs1 = stepOneFragment.checkStepOne();
                        if (rs1 == 0)
                            changeFragment(stepTwo);
                        else {
                            stepIndex--;
                        }
                        break;
                    case 3:
                        int rs2 = stepTwoFragment.checkStepTwo();
                        if (rs2 == 0) {
                            TextInputEditText inputPhone = stepTwoFragment.getInputPhone();
                            i.putExtra("phone_number", inputPhone.getText().toString());
                            changeFragment(stepThree);
                        } else {
                            stepIndex--;
                        }
                        break;
                    case 4:
                        int rs3 = stepThreeFragment.checkStepThree();
                        if (rs3 == 0) {
                            startActivity(i);
                        } else {
                            stepIndex--;
                        }
                        break;
                    default:
                        break;
                }
                isTransitioning = false;
            }

        });

        btnBack.setOnClickListener(v -> {
            stepIndex -= 1;
            switch (stepIndex) {
                case 0:
                    changeFragment(intro);
                    tvGetHelp.setVisibility(View.VISIBLE);
                    break;

                case 1:
                    changeFragment(stepOne);
                    tvGetHelp.setVisibility(View.VISIBLE);
                    tvBtnNextStep.setText("Tiếp tục");
                    break;
                case 2:
                    changeFragment(stepTwo);
                    break;
                case 3:
                    changeFragment(stepThree);
                    break;
                default:
                    dialogAlert.show();
                    break;
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
    }

    private void changeFragment(Fragment newFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.input_container, newFragment);
        fragmentTransaction.commit();
    }
}