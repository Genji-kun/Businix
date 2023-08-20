package com.example.businix.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.businix.R;
import com.example.businix.activities.admin.AdminAddListEmployeeActivity;
import com.example.businix.controllers.DepartmentController;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.controllers.PositionController;
import com.example.businix.models.Department;
import com.example.businix.models.Employee;
import com.example.businix.models.Position;
import com.example.businix.models.Status;
import com.example.businix.models.UserRole;
import com.example.businix.utils.FindListener;
import com.example.businix.utils.PasswordHash;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmployeeMemberAdapter extends ArrayAdapter<Employee> {
    private List<Employee> employeeList;
    private Context context;
    private TextInputEditText inputName, inputUsername, inputPassword;
    private TextInputLayout layoutUsername, layoutPassword;
    private AutoCompleteTextView dropdownRole, dropdownPosition, dropdownDepartment;
    private TextView tvEmplNumber;
    private ArrayAdapter<String> posNameAdapter, departmentNameAdapter, roleAdapter;
    private List<String> posItems, departmentItems, roleItems;
    private List<Position> posList;
    private List<Department> departmentList;
    private PositionController positionController;
    private DepartmentController departmentController;
    private List<TextInputLayout> layoutUsernameList;

    public EmployeeMemberAdapter(Context context, int resource, List<Employee> employeeList) {
        super(context, resource, employeeList);
        this.employeeList = employeeList;
        this.context = context;
        this.layoutUsernameList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return employeeList.size();
    }

    @Override
    public Employee getItem(int position) {
        return employeeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.list_view_employee_member, null);
        }
        Employee employee = employeeList.get(position);
        employee.setStatus(Status.ACTIVE);
        tvEmplNumber = (TextView) view.findViewById(R.id.tv_empl_number);
        tvEmplNumber.setText("Nhân viên " + (position + 1));
        inputName = (TextInputEditText) view.findViewById(R.id.input_name);
        inputUsername = (TextInputEditText) view.findViewById(R.id.input_username);
        inputPassword = (TextInputEditText) view.findViewById(R.id.input_password);
        layoutUsername = (TextInputLayout) view.findViewById(R.id.layout_username);
        if (position >= layoutUsernameList.size()) {
            layoutUsernameList.add(layoutUsername);
        }
        layoutPassword = (TextInputLayout) view.findViewById(R.id.layout_password);
        dropdownRole = (AutoCompleteTextView) view.findViewById(R.id.dropdown_role);
        dropdownPosition = (AutoCompleteTextView) view.findViewById(R.id.dropdown_position);
        dropdownDepartment = (AutoCompleteTextView) view.findViewById(R.id.dropdown_department);


        EmployeeController employeeController = new EmployeeController();

        if (employee.getUsername() == null) {
            inputUsername.setText("");
        }

        if (employee.getPassword() == null) {
            inputPassword.setText("");
        }

        if (employee.getFullName() == null) {
            inputName.setText("");
        }

        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                employee.setFullName(s.toString());
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
                        String username = s.toString();
                        employee.setUsername(username);
                        layoutUsername.setError("*Username đã tồn tại trong CSDL*");
                    }

                    @Override
                    public void onNotFound() {
                        layoutUsername.setError("");
                        String username = s.toString();
                        employee.setUsername(username);
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
                        employee.setPassword(PasswordHash.hashPassword(password));
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

        if(employee.getPosition() == null){
            if (dropdownPosition.getAdapter() == null) {
                posItems = new ArrayList<String>();
                positionController = new PositionController();
                posList = new ArrayList<>();
                positionController.getPositionList(task -> {
                    if (task.isSuccessful()) {
                        posList = task.getResult();
                        for (Position pos : posList) {
                            posItems.add(pos.getName());
                        }
                        posNameAdapter = new ArrayAdapter<String>(getContext(), R.layout.dropdown_menu, posItems);
                        dropdownPosition.setAdapter(posNameAdapter);
                    } else {
                        // Xử lý lỗi
                    }
                });
                dropdownPosition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        for (Position pos : posList) {
                            if (pos.getName().equals(posItems.get(position))) {
                                employee.setPosition(employeeController.getPositionRef(pos.getId()));
                            }
                        }
                    }
                });
            } else {
                dropdownPosition.setText("", false);
            }

        }


        if (employee.getDepartment() == null) {
            if (dropdownDepartment.getAdapter() == null) {
                departmentItems = new ArrayList<String>();
                departmentController = new DepartmentController();
                departmentList = new ArrayList<>();
                departmentController.getDepartmentList(task -> {
                    if (task.isSuccessful()) {
                        departmentList = task.getResult();
                        for (Department department : departmentList) {
                            departmentItems.add(department.getName());
                        }
                        departmentNameAdapter = new ArrayAdapter<String>(getContext(), R.layout.dropdown_menu, departmentItems);
                        dropdownDepartment.setAdapter(departmentNameAdapter);
                    } else {
                        //Xử lý lỗi
                    }
                    dropdownDepartment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            for (Department department : departmentList) {
                                if (department.getName().equals(departmentItems.get(position))) {
                                    employee.setDepartment(employeeController.getDepartmentRef(department.getId()));
                                }
                            }
                        }
                    });
                });
            }
            else {
                dropdownDepartment.setText("", false);
            }


        }


        if (employee.getUserRole() == null) {
            if (dropdownRole.getAdapter() == null) {
                roleItems = new ArrayList<>();
                UserRole[] roles = UserRole.values();
                for (UserRole role : roles) {
                    roleItems.add(role.toString());
                }
                roleAdapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_menu, roleItems);
                dropdownRole.setAdapter(roleAdapter);
                dropdownRole.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        employee.setUserRole(UserRole.valueOf(dropdownRole.getText().toString()));
                    }
                });
            }
            else {
                dropdownRole.setText("", false);
            }
        }




        LinearLayout btnDeletePosition = (LinearLayout) view.findViewById(R.id.btn_delete_member);
        btnDeletePosition.setOnClickListener(v -> {
            employeeList.remove(position);
            layoutUsernameList.remove(position);
            notifyDataSetChanged();
        });

        return view;
    }

    public TextInputLayout getLayoutUsername(int position) {
        return layoutUsernameList.get(position);
    }

}
