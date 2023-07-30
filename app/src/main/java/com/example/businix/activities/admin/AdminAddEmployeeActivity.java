package com.example.businix.activities.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.businix.R;
import com.example.businix.controllers.DepartmentController;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.controllers.PositionController;
import com.example.businix.models.Department;
import com.example.businix.models.Employee;
import com.example.businix.models.Gender;
import com.example.businix.models.Position;
import com.example.businix.models.Status;
import com.example.businix.models.UserRole;
import com.example.businix.utils.DateUtils;
import com.example.businix.utils.FindListener;
import com.example.businix.utils.PasswordHash;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminAddEmployeeActivity extends AppCompatActivity {

    private ImageView btnBack, imgAvatar, imgDatePicker;
    private TextInputEditText inputName, inputDOB, inputIdentityCard, inputPhone, inputEmail, inputUsername, inputPassword;
    private TextInputLayout layoutUsername, layoutPassword;
    private FloatingActionButton fabChangeAvatar;
    private AutoCompleteTextView dropdownGender, dropdownPosition, dropdownDepartment, dropdownRole;
    private LinearLayout btnAddEmployee;
    private TextView tvBtnAddEmployee;
    private ProgressBar progressBar;

    private EmployeeController employeeController;
    private PositionController positionController;
    private DepartmentController departmentController;
    private ArrayAdapter<String> posNameAdapter, departmentNameAdapter, genderAdapter, roleAdapter;
    private List<String> posItems, departmentItems, genderItems, roleItems;
    private List<Position> posList;
    private List<Department> departmentList;
    private String positionId, departmentId;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_employee);

        btnBack = (ImageView) findViewById(R.id.btn_back);
        imgAvatar = (ImageView) findViewById(R.id.img_avatar);
        fabChangeAvatar = (FloatingActionButton) findViewById(R.id.fab_change_image);

        inputName = (TextInputEditText) findViewById(R.id.input_name);
        inputDOB = (TextInputEditText) findViewById(R.id.input_DOB);
        imgDatePicker = (ImageView) findViewById(R.id.img_date_picker);
        inputIdentityCard = (TextInputEditText) findViewById(R.id.input_identity_card);
        inputPhone = (TextInputEditText) findViewById(R.id.input_phone);
        inputEmail = (TextInputEditText) findViewById(R.id.input_email);
        inputUsername = (TextInputEditText) findViewById(R.id.input_username);
        layoutUsername = (TextInputLayout) findViewById(R.id.layout_username);
        inputPassword = (TextInputEditText) findViewById(R.id.input_password);
        layoutPassword = (TextInputLayout) findViewById(R.id.layout_password);

        dropdownGender = (AutoCompleteTextView) findViewById(R.id.dropdown_gender);
        dropdownRole = (AutoCompleteTextView) findViewById(R.id.dropdown_role);
        dropdownPosition = (AutoCompleteTextView) findViewById(R.id.dropdown_position);
        dropdownDepartment = (AutoCompleteTextView) findViewById(R.id.dropdown_department);

        btnAddEmployee = (LinearLayout) findViewById(R.id.btn_add_employee);
        tvBtnAddEmployee = (TextView) findViewById(R.id.tv_btn_add_employee);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        imgDatePicker.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                inputDOB.setText(selectedDate);
            }, 2023, 0, 1); // Khởi tạo DatePickerDialog với ngày mặc định là 1/1/2023
            datePickerDialog.show();
        });


        posItems = new ArrayList<String>();
        positionController = new PositionController();
        positionController.getPositionList(task -> {
            if (task.isSuccessful()) {
                posList = task.getResult();
                for (Position pos : posList) {
                    posItems.add(pos.getName());
                }
                posNameAdapter = new ArrayAdapter<String>(AdminAddEmployeeActivity.this, R.layout.dropdown_menu, posItems);
                dropdownPosition.setAdapter(posNameAdapter);
            } else {
                // Xử lý lỗi
            }
        });

        dropdownPosition.setOnItemClickListener((parent, view, position, id) -> {
            for (Position pos : posList) {
                if (pos.getName().equals(posItems.get(position))) {
                    positionId = pos.getId();
                }
            }
        });

        departmentItems = new ArrayList<String>();
        departmentController = new DepartmentController();
        departmentController.getDepartmentList(task -> {
            if (task.isSuccessful()) {
                departmentList = task.getResult();
                for (Department department : departmentList) {
                    departmentItems.add(department.getName());
                }
                departmentNameAdapter = new ArrayAdapter<String>(AdminAddEmployeeActivity.this, R.layout.dropdown_menu, departmentItems);
                dropdownDepartment.setAdapter(departmentNameAdapter);
            } else {
                //Xử lý lỗi
            }
        });

        dropdownDepartment.setOnItemClickListener((parent, view, position, id) -> {
            for (Department department : departmentList) {
                if (department.getName().equals(departmentItems.get(position))) {
                    departmentId = department.getId();
                }
            }
        });

        genderItems = new ArrayList<>();
        Gender[] genders = Gender.values();
        for (Gender gender : genders) {
            genderItems.add(gender.toString());
        }
        genderAdapter = new ArrayAdapter<>(AdminAddEmployeeActivity.this, R.layout.dropdown_menu, genderItems);
        dropdownGender.setAdapter(genderAdapter);

        roleItems = new ArrayList<>();
        UserRole[] roles = UserRole.values();
        for (UserRole role : roles) {
            roleItems.add(role.toString());
        }
        roleAdapter = new ArrayAdapter<>(AdminAddEmployeeActivity.this, R.layout.dropdown_menu, roleItems);
        dropdownRole.setAdapter(roleAdapter);

        fabChangeAvatar.setOnClickListener(v -> {
            // Gọi ImagePicker để chọn ảnh từ máy
            ImagePicker.with(AdminAddEmployeeActivity.this).crop().compress(1024).maxResultSize(1080, 1080).start();
        });


        employeeController = new EmployeeController();
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
        inputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                if (password.length() >= 8) {
                    Pattern pattern = Pattern.compile("[^a-zA-z0-9]");
                    Matcher matcher = pattern.matcher(password);
                    boolean isHasSpeChar = matcher.find();
                    if (isHasSpeChar) {
                        layoutPassword.setHelperText("Mật khẩu mạnh");
                        layoutPassword.setError("");
                    } else {
                        layoutPassword.setHelperText("");
                        layoutPassword.setError("Mật khẩu yếu, cần ít nhất 1 ký tự đặc biệt");
                    }
                } else {
                    layoutPassword.setHelperText("");
                    layoutPassword.setError("Mật khẩu cần ít nhất 8 ký tự");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        btnAddEmployee.setOnClickListener(v -> {
            tvBtnAddEmployee.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            btnAddEmployee.setEnabled(false);
            Employee newEmpl = new Employee();

            if (inputName.getText().toString().strip().isBlank()) {
                showErrorMessege("Bạn chưa điền tên nhân viên");
                return;
            }
            if (inputUsername.getText().toString().strip().isBlank() && !inputUsername.getError().toString().strip().isBlank()) {
                showErrorMessege("Username không hợp lệ");
                return;
            }
            if (inputPassword.getText().toString().strip().isBlank() && !inputPassword.getError().toString().strip().isBlank()) {
                showErrorMessege("Password không hợp lệ");
                return;
            }
            if (dropdownRole.getText().toString().isBlank()) {
                showErrorMessege("Bạn chưa chọn vai trò");
                return;
            }
            if (dropdownPosition.getText().toString().isBlank()) {
                showErrorMessege("Bạn chưa chọn vị trí nhân viên");
                return;
            }
            if (dropdownDepartment.getText().toString().isBlank()) {
                showErrorMessege("Bạn chưa chọn phòng ban");
                return;
            }

            newEmpl.setFullName(inputName.getText().toString());
            newEmpl.setUsername(inputUsername.getText().toString().strip());
            newEmpl.setPassword(PasswordHash.hashPassword(inputPassword.getText().toString()));
            UserRole userRole = UserRole.valueOf(dropdownRole.getText().toString());
            newEmpl.setUserRole(userRole);
            newEmpl.setPosition(employeeController.getPositionRef(positionId));
            newEmpl.setDepartment(employeeController.getDepartmentRef(departmentId));
            newEmpl.setStatus(Status.ACTIVE);
            if (!inputDOB.getText().toString().strip().isBlank()) {
                try {
                    newEmpl.setDob(DateUtils.changeStringToDate(inputDOB.getText().toString()));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                } // setDOB
            }
            if (!dropdownGender.getText().toString().strip().isBlank()) {
                Gender gender = Gender.valueOf(dropdownGender.getText().toString());
                newEmpl.setGender(gender);
            }
            if (!inputIdentityCard.getText().toString().strip().isBlank() && inputIdentityCard.length() == 12) {
                newEmpl.setIdentityNum(inputIdentityCard.getText().toString().strip());
            }
            if (!inputPhone.getText().toString().strip().isBlank() && (inputPhone.length() >= 9 && inputPhone.length() <= 10)) {
                if (inputPhone.length() == 10)
                    newEmpl.setPhone(inputPhone.getText().toString().substring(1));
                else
                    newEmpl.setPhone(inputPhone.getText().toString());
            }
            if (!inputEmail.getText().toString().strip().isBlank() && inputEmail.getText().toString().contains("@")) {
                newEmpl.setEmail(inputEmail.getText().toString().strip());
            }

            //Upload cloudinary
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
                        newEmpl.setAvatar(imageUrl);
                        employeeController.addEmployee(newEmpl, task -> {
                            if (task.isSuccessful()) {
                                tvBtnAddEmployee.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                btnAddEmployee.setEnabled(false);
                                btnAddEmployee.setBackgroundColor(Color.GRAY);
                                showNotification();
                            }
                        });
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                    }
                }).dispatch();
            } else {
                employeeController.addEmployee(newEmpl, task -> {
                    showNotification();
                });
            }

        });

        btnBack.setOnClickListener(v -> {
            finish();
        });

    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AdminAddEmployeeActivity.this.RESULT_OK && data != null) {
            imageUri = data.getData();
            imgAvatar.setImageURI(imageUri);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(AdminAddEmployeeActivity.this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(AdminAddEmployeeActivity.this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleActivityResult(requestCode, resultCode, data);
    }

    public void showErrorMessege(String msg) {
        Toast.makeText(AdminAddEmployeeActivity.this, msg, Toast.LENGTH_SHORT).show();
        tvBtnAddEmployee.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        btnAddEmployee.setEnabled(true);
    }

    public void showNotification() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminAddEmployeeActivity.this);
        View dialogView = LayoutInflater.from(AdminAddEmployeeActivity.this).inflate(R.layout.custom_dialog_2, null);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        TextView tvQuestion = (TextView) dialogView.findViewById(R.id.question);
        tvQuestion.setText("Thông báo");
        TextView tvMsg = (TextView) dialogView.findViewById(R.id.msg);
        tvMsg.setText("Thêm nhân viên thành công");
        TextView btnCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        btnCancel.setVisibility(View.GONE);
        alertDialog.show();

        TextView btnConfirmDelete = (TextView) dialogView.findViewById(R.id.btn_continue);
        btnConfirmDelete.setOnClickListener(v -> {
            finish();
        });
    }
}