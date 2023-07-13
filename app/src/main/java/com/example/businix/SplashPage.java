package com.example.businix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.businix.pojo.Employee;
import com.example.businix.pojo.Position;
import com.example.businix.repository.EmployeeRepository;
import com.example.businix.repository.impl.EmployeeRepositoryImpl;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SplashPage extends AppCompatActivity {
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_page);

        EmployeeRepository er = new EmployeeRepositoryImpl();
        Employee e1 = new Employee();
        e1.setFullName("abc");
        er.addEmployee(e1);
        Employee e2 = new Employee();
        e2.setFullName("thái len");
        er.addEmployee(e2);
        er.getEmployeeList().addOnSuccessListener(employeeList -> {
            // In ra danh sách nhân viên ở đây
            for (Employee employee : employeeList) {
                System.out.println(employee);
            }
        }).addOnFailureListener(e -> {
            // Xử lý lỗi ở đây
        });

    }
}