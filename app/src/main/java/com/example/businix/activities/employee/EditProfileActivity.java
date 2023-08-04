package com.example.businix.activities.employee;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.utils.ObjectUtils;
import com.example.businix.R;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.models.Employee;
import com.example.businix.models.Gender;
import com.example.businix.ui.ActionBar;
import com.example.businix.ui.CustomDialog;
import com.example.businix.utils.DateUtils;
import com.example.businix.utils.LoginManager;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfileActivity extends ActionBar {
    private ImageView btnBack, imgAvatar, imgDatePicker;
    private TextInputEditText inputName, inputDOB, inputIdentityCard, inputPhone, inputEmail;
    private FloatingActionButton fabChangeImage;
    private AutoCompleteTextView dropdownGender;
    private LinearLayout btnEditEmployee;
    private EmployeeController employeeController;
    private List<String> posItems, departmentItems;
    private List<String> genderItems;
    private ArrayAdapter<String> genderAdapter;

    private TextInputLayout layoutName, layoutDOB, layoutIdentity, layoutPhone, layoutEmail;

    private Employee employee;
    private Uri imageUri;

    private Button btnUpdateProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        toolbar = findViewById(R.id.toolbar);
        setSupportMyActionBar("Cập nhận thông tin cá nhân", true, false);
        imgAvatar = (ImageView) findViewById(R.id.img_avatar);
        fabChangeImage = (FloatingActionButton) findViewById(R.id.fab_change_image);

        inputName = (TextInputEditText) findViewById(R.id.input_name);
        inputDOB = (TextInputEditText) findViewById(R.id.input_DOB);
        imgDatePicker = (ImageView) findViewById(R.id.img_date_picker);
        inputIdentityCard = (TextInputEditText) findViewById(R.id.input_identity_card);
        inputPhone = (TextInputEditText) findViewById(R.id.input_phone);
        inputEmail = (TextInputEditText) findViewById(R.id.input_email);

        btnUpdateProfile = findViewById(R.id.btn_update_profile);

        dropdownGender = (AutoCompleteTextView) findViewById(R.id.dropdown_gender);
        genderItems = new ArrayList<>();
        Gender[] genders = Gender.values();
        for (Gender gender : genders) {
            genderItems.add(gender.toString());
        }
        genderAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu, genderItems);
        dropdownGender.setAdapter(genderAdapter);

        inputDOB.setOnClickListener(v -> {
            showDatePickerDialog(inputDOB);
        });

        fabChangeImage.setOnClickListener(v -> {
            // Gọi ImagePicker để chọn ảnh từ máy
            ImagePicker.with(this).crop().compress(500).maxResultSize(500, 500).start();
        });

        layoutName = findViewById(R.id.layout_name);
        layoutDOB = findViewById(R.id.layout_DOB);
        layoutIdentity = findViewById(R.id.layout_identity_card);
        layoutPhone = findViewById(R.id.layout_phone);
        layoutEmail = findViewById(R.id.layout_email);

        inputName.addTextChangedListener(checkInput((s, start, before, count) -> {
            String name = s.toString();
            if (name.strip().isBlank()) {
                layoutName.setError("Họ tên không được để trống");
            } else {
                Pattern pattern = Pattern.compile("^[\\p{L} ]+$");
                Matcher matcher = pattern.matcher(s.toString());
                if (matcher.matches()) {
                    layoutName.setError(null);
                } else
                    layoutName.setError("Tên không đúng định dạng");
            }
        }));

        inputIdentityCard.addTextChangedListener(checkInput((s, start, before, count) -> {
            if (count != 12) {
                layoutIdentity.setError("Số CMMD/CCCD phải đủ 12 chữ số");
            } else {
                layoutIdentity.setError(null);
            }
        }));

        inputPhone.addTextChangedListener(checkInput((s, start, before, count) -> {
            int length = s.toString().length();
            if (length >= 9 && length <= 10) {
                Pattern pattern = Pattern.compile("^[0-9]+$");
                Matcher matcher = pattern.matcher(s.toString());
                if (matcher.matches()) {
                    layoutPhone.setError(null);
                } else {
                    layoutPhone.setError("Số điện thoại không được có kí tự đặc biệt");
                }
            } else
                layoutPhone.setError("Số điện thoại không đúng độ dài");
        }));

        inputEmail.addTextChangedListener(checkInput((s, start, before, count) -> {
            String email = s.toString();
            if (!email.strip().isBlank()) {
                Pattern pattern = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
                Matcher matcher = pattern.matcher(email);
                if (matcher.matches()) {
                    layoutEmail.setError(null);
                } else {
                    layoutEmail.setError("Email không đúng định dạng");
                }
            } else {
                layoutEmail.setError("Email không được để rỗng");
            }
        }));


        LoginManager loginManager = new LoginManager(this);
        String employeeId = loginManager.getLoggedInUserId();
        employeeController = new EmployeeController();
        employeeController.getEmployeeById(employeeId, task -> {
            employee = task.getResult();

            Glide.with(this).load(employee.getAvatar()).into(imgAvatar);
            inputName.setText(employee.getFullName());
            inputDOB.setText(DateUtils.formatDate(employee.getDob()));
            dropdownGender.setText(employee.getGender().toString(), false);
            inputIdentityCard.setText(employee.getIdentityNum());

            inputPhone.setText(employee.getPhone());
            inputEmail.setText(employee.getEmail());
        });

        btnUpdateProfile.setOnClickListener(v -> {
            CustomDialog confirmDialog = new CustomDialog(EditProfileActivity.this, R.layout.custom_dialog_2);
            confirmDialog.load();
            confirmDialog.setQuestion("Xác nhận");
            confirmDialog.setMessage("Bạn có chắc chắn muốn cập nhật không?");
            confirmDialog.setOnContinueClickListener((dialog, which) -> {
                Employee newProfile = new Employee();
                Boolean isChange = false;
                if (layoutName.getError() != null) {
                    return;
                } else if (!employee.getFullName().equals(inputName.getText().toString())) {
                    newProfile.setFullName(inputName.getText().toString().strip());
                    isChange = true;
                }
                Date dob;
                try {
                    dob = DateUtils.changeStringToDate(inputDOB.getText().toString());
                } catch (ParseException e) {
                    Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (layoutDOB.getError() != null) {
                    return;
                } else if (employee.getDob() == null || !employee.getDob().equals(dob)) {
                    newProfile.setDob(dob);
                    isChange = true;
                }

                if (!dropdownGender.getText().toString().isBlank()) {
                    if (employee.getGender() == null || !employee.getGender().toString().equals(dropdownGender.getText().toString())) {
                        newProfile.setGender(Gender.valueOf(dropdownGender.getText().toString()));
                        isChange = true;
                    }
                }

                if (layoutIdentity.getError() != null) {
                    return;
                } else if (employee.getIdentityNum() == null || !inputIdentityCard.getText().toString().equals(employee.getIdentityNum())) {
                    newProfile.setIdentityNum(inputIdentityCard.getText().toString());
                    isChange = true;
                }

                if (layoutPhone.getError() != null) {
                    return;
                } else if (employee.getPhone() == null || !inputPhone.getText().toString().equals(employee.getPhone())) {
                    newProfile.setPhone(inputPhone.getText().toString());
                    isChange = true;
                }

                if (layoutEmail.getError() != null) {
                    return;
                } else if (employee.getEmail() == null || !inputEmail.getText().toString().equals(employee.getEmail())) {
                    newProfile.setEmail(employee.getEmail());
                    isChange = true;
                }

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
                            //Xóa ảnh cũ trên cloudinary
                            String imageUrl = resultData.get("secure_url").toString();
                            newProfile.setAvatar(imageUrl);
                            employee.setAvatar(newProfile.getAvatar());
                            employeeController.updateEmployee(getIntent().getStringExtra("employeeId"), newProfile, task -> {
                                if (task.isSuccessful()) {
                                    showSuccessDialog();
                                    String[] parts = employee.getAvatar().split("/");
                                    String publicId = parts[parts.length - 1].split("\\.")[0];
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Map deleteParams = ObjectUtils.asMap("invalidate", true);
                                                MediaManager.get().getCloudinary().uploader().destroy(publicId, deleteParams);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                } else {
                                    showErrorDialog();
                                }
                            });
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            showErrorDialog();
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {
                        }
                    }).dispatch();
                }

                if (isChange) {
                    updateEmployee(newProfile);
                } else {
                    CustomDialog customDialog = new CustomDialog(EditProfileActivity.this, R.layout.custom_dialog_2);
                    customDialog.show();
                    customDialog.setQuestion("Cảnh báo");
                    customDialog.setQuestion("Vui lòng thay đổi thông tin trước khi bấm cập nhật");
                }
            });
        });
    }

    private void updateEmployee(Employee newProfile) {
        employeeController.updateEmployee(employee.getId(), newProfile, task -> {
            if (task.isSuccessful()) {
                String message = "Update successful";
                Intent intent = new Intent();
                intent.putExtra("employee", newProfile);
                setResult(RESULT_OK, intent);
                showSuccessDialog();
            } else {
                showErrorDialog();
            }
        });
    }

    private void showSuccessDialog() {
        CustomDialog customDialog = new CustomDialog(EditProfileActivity.this, R.layout.custom_dialog_2);
        customDialog.show();
        customDialog.setQuestion("Thông báo");
        customDialog.setMessage(
                "Cập nhật thành công");
        customDialog.setOnCancelClickListener((dialog, which) -> {
            finish();
        });
        customDialog.setOnContinueClickListener((dialog, which) -> {
            finish();
        });
    }

    private void showErrorDialog() {
        CustomDialog customDialog = new CustomDialog(EditProfileActivity.this, R.layout.custom_dialog);
        customDialog.show();
        customDialog.setQuestion("Báo lỗi");
        customDialog.setQuestion("Đã có lỗi xảy ra vui lòng thử lại sau");
        customDialog.setTextBtnCancel("Trở về trang trước đó");
        customDialog.setTextBtnContinue("Ở lại trang cập nhật");
        customDialog.setOnCancelClickListener((dialog, which) -> {
            finish();
        });
    }


    private void showDatePickerDialog(TextView inputDOB) {
        // Lấy ngày hiện tại để đặt làm giá trị mặc ring cho DatePickerDialog
        int year;
        int month;
        int dayOfMonth;
        if (inputDOB.getText().toString().isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            year = 1999;
            month = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            String[] endDateArr = inputDOB.getText().toString().split("/");
            year = Integer.parseInt(endDateArr[2]);
            month = Integer.parseInt(endDateArr[1]) - 1;
            dayOfMonth = Integer.parseInt(endDateArr[0]);
        }


        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, yearSelected, monthOfYear, dayOfMonthSelected) -> {
                    Calendar selectedCal = Calendar.getInstance();
                    selectedCal.set(yearSelected, monthOfYear + 1, dayOfMonthSelected);
                    if (DateUtils.isAtLeast18YearsOld(selectedCal.getTime())) {
                        layoutDOB.setError(null);
                    } else {
                        layoutDOB.setError("Tuổi phải từ 18 tuổi trở lên");
                    }
                    String selectedDate = dayOfMonthSelected + "/" + (monthOfYear + 1) + "/" + yearSelected;
                    inputDOB.setText(selectedDate);
                },
                year,
                month,
                dayOfMonth
        );

        // Hiển thị DatePickerDialog
        datePickerDialog.show();
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == this.RESULT_OK && data != null) {
            imageUri = data.getData();
            imgAvatar.setImageURI(imageUri);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleActivityResult(requestCode, resultCode, data);
    }

    private interface OnTextCheck {
        void onTextChanged(CharSequence s, int start, int before, int count);

    }

    private TextWatcher checkInput(OnTextCheck onTextCheck) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onTextCheck.onTextChanged(s, start, before, count);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }
}