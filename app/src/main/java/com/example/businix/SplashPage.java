package com.example.businix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.businix.activities.LoginActivity;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.dao.EmployeeDAO;
import com.example.businix.models.Employee;
import com.example.businix.models.Gender;
import com.example.businix.models.Status;
import com.example.businix.utils.PasswordHash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SplashPage extends AppCompatActivity {
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_page);

//        Employee e1 = new Employee();
//        e1.setFullName("Võ Phú Phát");
//        e1.setGender(Gender.FEMALE);
//        e1.setPhone("+84987654321");
//        e1.setAddress("17/10 Trần Văn Ơn");
//        Date date = new GregorianCalendar(2002, Calendar.NOVEMBER, 14).getTime();
//        e1.setEmail("vophuphat@gmail.com");
//        e1.setAvatar("https://scontent.fsgn5-10.fna.fbcdn.net/v/t39.30808-6/350308009_646407656823067_8308207455622143142_n.jpg?_nc_cat=106&cb=99be929b-59f725be&ccb=1-7&_nc_sid=09cbfe&_nc_ohc=N7wHUiaDHDYAX-h-x-M&_nc_ht=scontent.fsgn5-10.fna&oh=00_AfBYTF73dCW7GbFi6Wxgjf710epljgGQIpxxCk7Tw7V8ow&oe=64B985D6");
//        e1.setUsername("emp_phat123");
//        e1.setPassword(PasswordHash.hashPassword("phat123"));
//        e1.setStatus(Status.ACTIVE);
//        DocumentReference posRef = FirebaseFirestore.getInstance().collection("positions").document("I51WIsDtgJnBsv1LkeHw");
//        e1.setPosition(posRef);
//        e1.setDob(date);
//

        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashPage.this, LoginActivity.class);
            startActivity(i);
            finish();
        }, 3000);

    }
}