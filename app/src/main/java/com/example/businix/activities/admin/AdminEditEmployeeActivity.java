package com.example.businix.activities.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.bumptech.glide.Glide;
import com.cloudinary.Cloudinary;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.MultipartUtility;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.utils.ObjectUtils;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminEditEmployeeActivity extends AppCompatActivity {

    private ImageView btnBack, imgAvatar, imgDatePicker;
    private TextInputEditText inputName, inputDOB, inputIdentityCard, inputPhone, inputEmail, inputUsername, inputPassword;
    private TextInputLayout layoutUsername, layoutPassword;
    private FloatingActionButton fabChangeAvatar;
    private AutoCompleteTextView dropdownGender, dropdownPosition, dropdownDepartment, dropdownRole, dropdownStatus;
    private LinearLayout btnEditEmployee;
    private TextView tvBtnEditEmployee;
    private ProgressBar progressBar;
    private EmployeeController employeeController;
    private Employee emplBeforeUpdate;
    private PositionController positionController;
    private DepartmentController departmentController;
    private ArrayAdapter<String> posNameAdapter, departmentNameAdapter, genderAdapter, roleAdapter, statusAdapter;
    private List<String> posItems, departmentItems, genderItems, roleItems, statusItems;
    private List<Position> posList;
    private List<Department> departmentList;
    private String positionId, departmentId;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_employee);

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
        dropdownStatus = (AutoCompleteTextView) findViewById(R.id.dropdown_status);

        emplBeforeUpdate = new Employee();
        departmentController = new DepartmentController();
        positionController = new PositionController();

        btnEditEmployee = (LinearLayout) findViewById(R.id.btn_edit_employee);
        tvBtnEditEmployee = (TextView) findViewById(R.id.tv_btn_edit_employee);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);


        imgDatePicker.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                inputDOB.setText(selectedDate);
            }, 2023, 0, 1); // Khởi tạo DatePickerDialog với ngày mặc định là 1/1/2023
            datePickerDialog.show();
        });

        posItems = new ArrayList<String>();
        positionController.getPositionList(task -> {
            if (task.isSuccessful()) {
                posList = task.getResult();
                for (Position pos : posList) {
                    posItems.add(pos.getName());
                }
                posNameAdapter = new ArrayAdapter<String>(AdminEditEmployeeActivity.this, R.layout.dropdown_menu, posItems);
                dropdownPosition.setAdapter(posNameAdapter);
            } else {
                // Xử lý lỗi
            }
        });

        dropdownPosition.setOnItemClickListener((parent, view, position, id) -> {
            for (Position pos : posList) {
                if (pos.getName().equals(posItems.get(position))) {
                    positionId = pos.getId();
                    emplBeforeUpdate.setPosition(employeeController.getPositionRef(positionId));
                }
            }
        });

        departmentItems = new ArrayList<String>();
        departmentController.getDepartmentList(task -> {
            if (task.isSuccessful()) {
                departmentList = task.getResult();
                for (Department department : departmentList) {
                    departmentItems.add(department.getName());
                }
                departmentNameAdapter = new ArrayAdapter<String>(AdminEditEmployeeActivity.this, R.layout.dropdown_menu, departmentItems);
                dropdownDepartment.setAdapter(departmentNameAdapter);
            } else {
                //Xử lý lỗi
            }
        });

        dropdownDepartment.setOnItemClickListener((parent, view, position, id) -> {
            for (Department department : departmentList) {
                if (department.getName().equals(departmentItems.get(position))) {
                    departmentId = department.getId();
                    emplBeforeUpdate.setDepartment(employeeController.getDepartmentRef(departmentId));
                }
            }
        });

        genderItems = new ArrayList<>();
        Gender[] genders = Gender.values();
        for (Gender gender : genders) {
            genderItems.add(gender.toString());
        }
        genderAdapter = new ArrayAdapter<>(AdminEditEmployeeActivity.this, R.layout.dropdown_menu, genderItems);
        dropdownGender.setAdapter(genderAdapter);

        roleItems = new ArrayList<>();
        UserRole[] roles = UserRole.values();
        for (UserRole role : roles) {
            roleItems.add(role.toString());
        }
        roleAdapter = new ArrayAdapter<>(AdminEditEmployeeActivity.this, R.layout.dropdown_menu, roleItems);
        dropdownRole.setAdapter(roleAdapter);

        statusItems = new ArrayList<>();
        Status[] statuses = Status.values();
        for (Status status : statuses) {
            statusItems.add(status.toString());
        }
        statusAdapter = new ArrayAdapter<>(AdminEditEmployeeActivity.this, R.layout.dropdown_menu, statusItems);
        dropdownStatus.setAdapter(statusAdapter);

        employeeController = new EmployeeController();
        employeeController.getEmployeeById(getIntent().getStringExtra("employeeId"), task -> {
            emplBeforeUpdate = task.getResult();

            Glide.with(this).load(emplBeforeUpdate.getAvatar()).into(imgAvatar);
            inputName.setText(emplBeforeUpdate.getFullName());
            if (emplBeforeUpdate.getDob() != null)
                inputDOB.setText(DateUtils.formatDate(emplBeforeUpdate.getDob()));
            if (emplBeforeUpdate.getGender() != null)
                dropdownGender.setText(emplBeforeUpdate.getGender().toString(), false);
            if (emplBeforeUpdate.getIdentityNum() != null)
                inputIdentityCard.setText(emplBeforeUpdate.getIdentityNum());
            if (emplBeforeUpdate.getPhone() != null)
                inputPhone.setText(emplBeforeUpdate.getPhone().replace("+84", ""));
            if (emplBeforeUpdate.getEmail() != null)
                inputEmail.setText(emplBeforeUpdate.getEmail());
            inputUsername.setText(emplBeforeUpdate.getUsername());
            inputPassword.setText(emplBeforeUpdate.getPassword());
            dropdownRole.setText(emplBeforeUpdate.getUserRole().toString(), false);
            dropdownStatus.setText(emplBeforeUpdate.getStatus().toString(), false);

            if (emplBeforeUpdate.getPosition() != null){
                emplBeforeUpdate.getPosition().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        dropdownPosition.setText(documentSnapshot.getString("name"), false);
                    }
                });
            }

            if (emplBeforeUpdate.getDepartment() != null){
                emplBeforeUpdate.getDepartment().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        dropdownDepartment.setText(documentSnapshot.getString("name"), false);
                    }
                });
            }
        });
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
                        if (!inputUsername.getText().toString().strip().equals(getIntent().getStringExtra("employeeUsername")))
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
                    Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*_\\-+=])[A-Za-z\\d!@#$%^&*_\\-+=]+$");
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

        fabChangeAvatar.setOnClickListener(v -> {
            // Gọi ImagePicker để chọn ảnh từ máy
            ImagePicker.with(AdminEditEmployeeActivity.this).crop().compress(1024).maxResultSize(1080, 1080).start();
        });

        btnEditEmployee.setOnClickListener(v -> {
            tvBtnEditEmployee.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            btnEditEmployee.setEnabled(false);
            Employee updateEmpl = new Employee();

            if (inputName.getText().toString().strip().isBlank()) {
                showErrorMessege("Bạn chưa điền tên nhân viên");
                return;
            } else if (!inputName.getText().toString().strip().equals(emplBeforeUpdate.getFullName())) {
                updateEmpl.setFullName(inputName.getText().toString().strip());
            }
            if (inputUsername.getText().toString().trim().isEmpty() || inputUsername.getError() != null) {
                showErrorMessege("Username không hợp lệ");
                return;
            } else if (!inputUsername.getText().toString().trim().equals(emplBeforeUpdate.getUsername())) {
                updateEmpl.setUsername(inputUsername.getText().toString().trim());
            }

            if (inputPassword.getText().toString().trim().isEmpty() || inputPassword.getError() != null) {
                showErrorMessege("Username không hợp lệ");
                return;
            } else if (!inputPassword.getText().toString().trim().equals(emplBeforeUpdate.getPassword())) {
                updateEmpl.setPassword(PasswordHash.hashPassword(inputPassword.getText().toString().trim()));
            }
            if (!inputDOB.getText().toString().isBlank()) {
                if (emplBeforeUpdate.getDob() == null || !DateUtils.formatDate(emplBeforeUpdate.getDob()).equals(inputDOB.getText().toString())) {
                    try {
                        emplBeforeUpdate.setDob(DateUtils.changeStringToDate(inputDOB.getText().toString()));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            if (!dropdownGender.getText().toString().isBlank()) {
                if (emplBeforeUpdate.getGender() == null || !emplBeforeUpdate.getGender().toString().equals(dropdownGender.getText().toString())) {
                    emplBeforeUpdate.setGender(Gender.valueOf(dropdownGender.getText().toString()));
                }
            }

            if (!inputIdentityCard.getText().toString().isBlank() && inputIdentityCard.length() == 12) {
                String identityNum = inputIdentityCard.getText().toString();
                if (emplBeforeUpdate.getIdentityNum() == null || !emplBeforeUpdate.getIdentityNum().equals(identityNum))
                    updateEmpl.setIdentityNum(identityNum);
            }


            if (!inputPhone.getText().toString().strip().isBlank() && (inputPhone.length() >= 9 && inputPhone.length() <= 10)) {
                String phone = inputPhone.getText().toString();
                if (emplBeforeUpdate.getPhone() == null || !emplBeforeUpdate.getPhone().equals(phone))
                    updateEmpl.setPhone(phone);
            }

            if (!inputEmail.getText().toString().strip().isBlank()) {
                String email = inputEmail.getText().toString();
                if (emplBeforeUpdate.getEmail() == null || !emplBeforeUpdate.getEmail().equals(email))
                    updateEmpl.setEmail(email);
            }

            if (!dropdownRole.getText().toString().equals(emplBeforeUpdate.getUserRole().toString())) {
                updateEmpl.setUserRole(UserRole.valueOf(dropdownRole.getText().toString()));
            }

            if (emplBeforeUpdate.getPosition() != null) {
                if (!dropdownPosition.getText().toString().equals(emplBeforeUpdate.getPosition().toString())) {
                    updateEmpl.setPosition(employeeController.getPositionRef(positionId));
                }
            } else {
                showErrorMessege("Bạn chưa chọn chức vụ");
                return;
            }

            if (emplBeforeUpdate.getDepartment() != null) {
                if (!dropdownDepartment.getText().toString().equals(emplBeforeUpdate.getDepartment().toString())) {
                    updateEmpl.setDepartment(employeeController.getDepartmentRef(departmentId));
                }
            } else {
                showErrorMessege("Bạn chưa chọn phòng ban");
                return;
            }

            if (!dropdownStatus.getText().toString().equals(emplBeforeUpdate.getStatus().toString())) {
                updateEmpl.setStatus(Status.valueOf(dropdownStatus.getText().toString()));
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
                        //Xóa ảnh cũ trên cloudinary
                        String imageUrl = resultData.get("secure_url").toString();
                        updateEmpl.setAvatar(imageUrl);
                        employeeController.updateEmployee(getIntent().getStringExtra("employeeId"), updateEmpl, task -> {
                            if (task.isSuccessful()) {
                                showSuccessDialog();
                                String[] parts = emplBeforeUpdate.getAvatar().split("/");
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
                employeeController.updateEmployee(getIntent().getStringExtra("employeeId"), updateEmpl, task -> {
                    showSuccessDialog();
                });
            }
        });


        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AdminEditEmployeeActivity.this.RESULT_OK && data != null) {
            imageUri = data.getData();
            imgAvatar.setImageURI(imageUri);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(AdminEditEmployeeActivity.this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(AdminEditEmployeeActivity.this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleActivityResult(requestCode, resultCode, data);
    }

    public void showErrorMessege(String msg) {
        Toast.makeText(AdminEditEmployeeActivity.this, msg, Toast.LENGTH_SHORT).show();
        normalView();
    }

    public void showSuccessDialog() {
        normalView();

        AlertDialog.Builder builder = new AlertDialog.Builder(AdminEditEmployeeActivity.this);
        View dialogView = LayoutInflater.from(AdminEditEmployeeActivity.this).inflate(R.layout.custom_dialog_2, null);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        TextView tvQuestion = (TextView) dialogView.findViewById(R.id.question);
        tvQuestion.setText("Thông báo");
        TextView tvMsg = (TextView) dialogView.findViewById(R.id.msg);
        tvMsg.setText("Cập nhật thành công");
        TextView btnCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        btnCancel.setVisibility(View.GONE);
        alertDialog.show();

        TextView btnConfirm = (TextView) dialogView.findViewById(R.id.btn_continue);
        btnConfirm.setOnClickListener(v -> {
            alertDialog.dismiss();
            finish();
        });
    }

    public void processingView() {
        progressBar.setVisibility(View.VISIBLE);
        tvBtnEditEmployee.setVisibility(View.GONE);
        btnEditEmployee.setEnabled(false);
        btnEditEmployee.setBackgroundColor(Color.LTGRAY);
        btnBack.setEnabled(false);
    }

    public void normalView() {
        progressBar.setVisibility(View.GONE);
        tvBtnEditEmployee.setVisibility(View.VISIBLE);
        btnEditEmployee.setEnabled(true);
        btnEditEmployee.setBackgroundColor(getResources().getColor(R.color.light_purple));
        btnBack.setEnabled(true);
    }
}
