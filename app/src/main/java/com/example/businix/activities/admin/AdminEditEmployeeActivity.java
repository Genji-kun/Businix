package com.example.businix.activities.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.businix.R;
import com.example.businix.controllers.DepartmentController;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.controllers.PositionController;
import com.example.businix.models.Department;
import com.example.businix.models.Employee;
import com.example.businix.models.Position;
import com.example.businix.utils.DateUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AdminEditEmployeeActivity extends AppCompatActivity {

    private ImageView btnBack, imgAvatar, imgDatePicker;
    private TextInputEditText inputName, inputDOB, inputIdentityCard, inputPhone, inputEmail, inputUsername, inputPassword;
    private FloatingActionButton fabChangeImage;
    private AutoCompleteTextView dropdownGender, dropdownPosition, dropdownDepartment;
    private LinearLayout btnEditEmployee;
    private EmployeeController employeeController;
    private PositionController positionController;
    private DepartmentController departmentController;
    private ArrayAdapter<String> posNameAdapter, departmentNameAdapter;
    private List<String> posItems, departmentItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_employee);

        btnBack = (ImageView) findViewById(R.id.btn_back);
        imgAvatar = (ImageView) findViewById(R.id.img_avatar);
        fabChangeImage = (FloatingActionButton) findViewById(R.id.fab_change_image);

        inputName = (TextInputEditText) findViewById(R.id.input_name);
        inputDOB = (TextInputEditText) findViewById(R.id.input_DOB);
        imgDatePicker = (ImageView) findViewById(R.id.img_date_picker);
        inputIdentityCard = (TextInputEditText) findViewById(R.id.input_identity_card);
        inputPhone = (TextInputEditText) findViewById(R.id.input_phone);
        inputEmail = (TextInputEditText) findViewById(R.id.input_email);
        inputUsername = (TextInputEditText) findViewById(R.id.input_username);
        inputPassword = (TextInputEditText) findViewById(R.id.input_password);

        dropdownGender = (AutoCompleteTextView) findViewById(R.id.dropdown_gender);
        dropdownPosition = (AutoCompleteTextView) findViewById(R.id.dropdown_position);
        dropdownDepartment = (AutoCompleteTextView) findViewById(R.id.dropdown_department);

        posItems = new ArrayList<String>();
        positionController = new PositionController();
        positionController.getPositionList(task1 -> {
            if (task1.isSuccessful()) {
                List<Position> posList = task1.getResult();
                for (Position pos1 : posList) {
                    posItems.add(pos1.getName());
                }
                posNameAdapter = new ArrayAdapter<String>(AdminEditEmployeeActivity.this, R.layout.dropdown_menu, posItems);
                dropdownPosition.setAdapter(posNameAdapter);
            } else {
                // Xử lý lỗi
            }
        });


        departmentItems = new ArrayList<String>();
        departmentController = new DepartmentController();
        departmentController.getDepartmentList(task -> {
            if (task.isSuccessful()) {
                List<Department> departmentList = task.getResult();
                for (Department department : departmentList) {
                    departmentItems.add(department.getName());
                }
                departmentNameAdapter = new ArrayAdapter<String>(AdminEditEmployeeActivity.this, R.layout.dropdown_menu, departmentItems);
                dropdownDepartment.setAdapter(departmentNameAdapter);
            } else {
                //Xử lý lỗi
            }
        });



        String employeeId = getIntent().getStringExtra("employeeId");
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

            inputUsername.setText(employee.getUsername());
            inputPassword.setText(employee.getPassword());

            employee.getPosition().get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Position pos = documentSnapshot.toObject(Position.class);
                    String positionName = pos.getName();
                    dropdownPosition.setText(positionName, false);

                } else {
                    dropdownPosition.setText("", false);
                }
            });



            employee.getDepartment().get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Department department = documentSnapshot.toObject(Department.class);
                    String departmentName = department.getName();
                    dropdownDepartment.setText(departmentName, false);
                } else {
                    dropdownDepartment.setText("", false);
                }
            });

        });

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }


}