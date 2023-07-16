package com.example.businix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.businix.dao.EmployeeDAO;
import com.example.businix.dao.PositionDAO;
import com.example.businix.pojo.Employee;
import com.example.businix.pojo.Position;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashPage extends AppCompatActivity {
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_page);
        db = FirebaseFirestore.getInstance();
        DocumentReference employeeRef = db.collection("employees").document("HgQGunHIgDj051alYOMS");
        employeeRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Lấy dữ liệu của nhân viên từ DocumentSnapshot
                    Employee employee = document.toObject(Employee.class);
                    System.out.println("?");
                    // Bây giờ bạn có đối tượng Employee với thông tin từ Firestore
                    // Có thể sử dụng thông tin trong employee
                    // Và các thông tin khác của nhân viên

                    // Tiếp tục xử lý với đối tượng Employee nếu cần
                } else {
                    // Nếu tài liệu không tồn tại hoặc không có dữ liệu, xử lý tương ứng
                }
            } else {
                // Xử lý lỗi nếu không thể lấy dữ liệu nhân viên
            }
        });

        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashPage.this, LoginActivity.class);
            startActivity(i);
            finish();
        }, 3000);

    }
}