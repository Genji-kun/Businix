package com.example.businix.activities.employee;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.businix.R;
import com.example.businix.controllers.DepartmentController;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.controllers.PositionController;
import com.example.businix.models.Employee;
import com.example.businix.ui.ActionBar;
import com.example.businix.utils.DateUtils;
import com.example.businix.utils.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.List;

public class EditProfileActivity extends ActionBar {
    private ImageView btnBack, imgAvatar, imgDatePicker;
    private TextInputEditText inputName, inputDOB, inputIdentityCard, inputPhone, inputEmail;
    private FloatingActionButton fabChangeImage;
    private AutoCompleteTextView dropdownGender;
    private LinearLayout btnEditEmployee;
    private EmployeeController employeeController;
    private List<String> posItems, departmentItems;

    private String employeeId;
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

        dropdownGender = (AutoCompleteTextView) findViewById(R.id.dropdown_gender);

        inputDOB.setOnClickListener(v -> {
            showDatePickerDialog(inputDOB);
        });

        LoginManager loginManager = new LoginManager(this);
        employeeId = loginManager.getLoggedInUserId();
        employeeController = new EmployeeController();
        employeeController.getEmployeeById(employeeId, task -> {
            Employee employee = task.getResult();

            Glide.with(this).load(employee.getAvatar()).into(imgAvatar);
            inputName.setText(employee.getFullName());
            inputDOB.setText(DateUtils.formatDate(employee.getDob()));
            dropdownGender.setText(employee.getGender().toString());
            inputIdentityCard.setText(employee.getIdentityNum());

            inputPhone.setText(employee.getPhone().substring(3));
            inputEmail.setText(employee.getEmail());
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
}