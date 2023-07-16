package com.example.businix;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.google.android.material.textfield.TextInputEditText;

public class AdminEmployeeManagementActivity extends AppCompatActivity {
    private AutoCompleteTextView dropdownPosition;
    private TextInputEditText inputName;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private String[] employeeNameItems = {"Võ Phú Phát", "Nguyễn Kim Bảo Ngân", "Phan Thanh Hải", "Lại Bình Phong"};
    private String[] positionItems = {"Trưởng phòng", "Lập trình viên", "HR"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_management);

        inputName = (TextInputEditText) findViewById(R.id.input_name);
        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                arrayAdapter.getFilter().filter(query);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dropdownPosition = (AutoCompleteTextView) findViewById(R.id.dropdown_position);
        ArrayAdapter<String> itemAdapter = new ArrayAdapter<String>(AdminEmployeeManagementActivity.this, R.layout.dropdown_menu, positionItems);
        dropdownPosition.setAdapter(itemAdapter);
        dropdownPosition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        listView = (ListView) findViewById(R.id.list_view_employee);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.listview_employee, R.id.tv_empl_name, employeeNameItems);
        listView.setAdapter(arrayAdapter);

    }
}