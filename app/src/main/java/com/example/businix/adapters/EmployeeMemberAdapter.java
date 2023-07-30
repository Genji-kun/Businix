package com.example.businix.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.businix.R;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.controllers.PositionController;
import com.example.businix.models.Employee;
import com.example.businix.utils.FindListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmployeeMemberAdapter extends ArrayAdapter<Employee> {
    private List<Employee> employeeList;
    private Context context;

    public EmployeeMemberAdapter(Context context, int resource, List<Employee> employeeList) {
        super(context, resource, employeeList);
        this.employeeList = employeeList;
        this.context = context;
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
            view = inflater.inflate(R.layout.listview_employee_member, null);
        }
        Employee employee = employeeList.get(position);

        TextView tvEmplNumber = (TextView) view.findViewById(R.id.tv_empl_number);
        tvEmplNumber.setText("Nhân viên " + (position + 1));
        TextInputEditText inputName = (TextInputEditText) view.findViewById(R.id.input_name);
        TextInputEditText inputUsername = (TextInputEditText) view.findViewById(R.id.input_username);
        TextInputEditText inputPassword = (TextInputEditText) view.findViewById(R.id.input_password);
        TextInputLayout layoutUsername = (TextInputLayout) view.findViewById(R.id.layout_username);
        TextInputLayout layoutPassword = (TextInputLayout) view.findViewById(R.id.layout_password);

        EmployeeController employeeController = new EmployeeController();
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



        LinearLayout btnDeletePosition = (LinearLayout) view.findViewById(R.id.btn_delete_position);
        btnDeletePosition.setOnClickListener(v -> {
            employeeList.remove(position);
            notifyDataSetChanged();
        });

        return view;
    }
}
