package com.example.businix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.firestore.FirebaseFirestore;

public class SplashPage extends AppCompatActivity {
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_page);

//        EmployeeRepository er = new EmployeeRepositoryImpl();
//        Employee e1 = new Employee();
//        e1.setFullName("abc");
//        er.addEmployee(e1);
//        Employee e2 = new Employee();
//        e2.setFullName("thái len");
//        er.addEmployee(e2);
//        er.getEmployeeList().addOnSuccessListener(employeeList -> {
//            // In ra danh sách nhân viên ở đây
//            for (Employee employee : employeeList) {
//                System.out.println(employee);
//            }
//        }).addOnFailureListener(e -> {
//            // Xử lý lỗi ở đây
//        });

        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashPage.this, LoginActivity.class);
            startActivity(i);
            finish();
        }, 3000);

    }
}