package com.example.businix.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.businix.R;
import com.example.businix.activities.admin.AdminEditEmployeeActivity;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.fragments.SignUpStepFourFragment;
import com.example.businix.fragments.SignUpStepOneFragment;
import com.example.businix.fragments.SignUpStepThreeFragment;
import com.example.businix.fragments.SignUpStepTwoFragment;
import com.example.businix.fragments.IntroductionFragment;
import com.example.businix.models.Employee;
import com.example.businix.models.Gender;
import com.example.businix.models.Status;
import com.example.businix.models.UserRole;
import com.example.businix.utils.DateUtils;
import com.example.businix.utils.FindListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private LinearLayout btnNextStep;
    private TextView tvGetHelp, tvBtnNextStep, btnContinue, btnCancel;
    private ImageView btnBack;
    private int stepIndex;
    private Dialog dialogAlert;
    private Boolean isTransitioning;
    private EmployeeController employeeController;




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
        btnContinue = (TextView) dialogAlert.findViewById(R.id.btn_continue);
        btnCancel = (TextView) dialogAlert.findViewById(R.id.btn_cancel);

        Employee employee = new Employee();
        employeeController = new EmployeeController();
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

        // Step Three
        Fragment stepFour = new SignUpStepFourFragment();
        SignUpStepFourFragment stepFourFragment = (SignUpStepFourFragment) stepFour;

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
                        if (rs1 == 0) {
                            employee.setFullName(stepOneFragment.getInputName().getText().toString().strip());
                            try {
                                employee.setDob(DateUtils.changeStringToDate(stepOneFragment.getInputDOB().getText().toString()));
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            Gender gender = Gender.valueOf(stepOneFragment.getDropdownGender().getText().toString());
                            employee.setGender(gender);
                            employee.setIdentityNum(stepOneFragment.getInputIdentityCard().getText().toString());
                            changeFragment(stepTwo);
                        } else {
                            Toast.makeText(SignUpActivity.this, "Thông tin không hợp lệ", Toast.LENGTH_SHORT).show();
                            stepIndex--;
                        }
                        break;
                    case 3:
                        int rs2 = stepTwoFragment.checkStepTwo();
                        if (rs2 == 0) {
                            String inputPhone = stepTwoFragment.getInputPhone().getText().toString();
                            if (inputPhone.length() == 10)
                                inputPhone.substring(1);
                            i.putExtra("phone_number", inputPhone);
                            employee.setPhone("+84" + inputPhone);
                            employee.setEmail(stepTwoFragment.getInputEmail().getText().toString());
                            changeFragment(stepThree);
                        } else {
                            stepIndex--;
                        }
                        break;
                    case 4:
                        TextInputLayout layoutUsername = stepThreeFragment.getLayoutUsername();
                        TextInputLayout layoutPassword = stepThreeFragment.getLayoutPassword();
                        TextInputLayout layoutConfirm = stepThreeFragment.getLayoutConfirm();

                        TextInputEditText inputUsername = stepThreeFragment.getInputUsername();
                        TextInputEditText inputPassword = stepThreeFragment.getInputPassword();

                        inputUsername.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                employeeController.checkUserExist(s.toString(), new FindListener() {
                                    @Override
                                    public void onFoundSuccess() {
                                        layoutUsername.setError("*Username đã tồn tại*");
                                    }

                                    @Override
                                    public void onNotFound() {
                                        layoutUsername.setError("");
                                    }
                                });
                            }
                        });
                        if (layoutUsername.getError() == null && layoutPassword.getError() == null && layoutConfirm.getError() == null) {
                            employee.setUsername(inputUsername.getText().toString());
                            employee.setPassword(inputPassword.getText().toString());
                            changeFragment(stepFour);
                        } else {
                            Toast.makeText(SignUpActivity.this, "Thông tin không chính xác", Toast.LENGTH_SHORT).show();
                            stepIndex--;
                        }
                        break;
                    case 5:
                        ImageView imgAvatar = stepFourFragment.getImgAvatar();
                        if (imgAvatar.getDrawable() != null) {
                            Uri imageUri = stepFourFragment.getImgUri();
                            if (imageUri != null) {
                                MediaManager.get().upload(imageUri).callback(new UploadCallback() {
                                    @Override
                                    public void onStart(String requestId) {
                                    }
                                    @Override
                                    public void onProgress(String requestId, long bytes, long totalBytes) {
                                    }

                                    @Override
                                    public void onSuccess(String requestId, Map resultData) {
                                        String imageUrl = resultData.get("secure_url").toString();
                                        employee.setAvatar(imageUrl);
                                        employee.setUserRole(UserRole.USER);
                                        employee.setStatus(Status.PENDING);
                                        i.putExtra("employee", employee);
                                        startActivity(i);
                                    }
                                    @Override
                                    public void onError(String requestId, ErrorInfo error) {
                                        Toast.makeText(SignUpActivity.this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onReschedule(String requestId, ErrorInfo error) {

                                    }
                                }).dispatch();
                            }
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
                case 4:
                    changeFragment(stepFour);
                    break;
                default:
                    dialogAlert.show();
                    break;
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment stepFourFragment = getSupportFragmentManager().findFragmentById(R.id.input_container);
        if (stepFourFragment != null && stepFourFragment instanceof SignUpStepFourFragment) {
            ((SignUpStepFourFragment) stepFourFragment).handleActivityResult(requestCode, resultCode, data);
        }
    }


}