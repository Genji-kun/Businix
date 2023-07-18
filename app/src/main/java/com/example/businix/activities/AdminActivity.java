package com.example.businix.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.businix.fragments.AdminHomeFragment;
import com.example.businix.fragments.AdminNotificationFragment;
import com.example.businix.R;
import com.example.businix.fragments.AdminAbsentMailFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;

public class AdminActivity extends AppCompatActivity {

    private Dialog dialogAlert;
    private Button btnAccept, btnCancel;
    private BottomNavigationView navBar;
    private HashMap<Integer, Fragment> fragmentMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);



        //Thêm fragments vào danh sách
        fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.action_home, new AdminHomeFragment());
        fragmentMap.put(R.id.action_absent, new AdminAbsentMailFragment());
        fragmentMap.put(R.id.action_notification, new AdminNotificationFragment());
        fragmentMap.put(R.id.action_logout, null);

        //Set fragment mặc định là home
        Fragment homeFragment = fragmentMap.get(R.id.action_home);
        changeFragment(homeFragment);

        //Viết sự thay đổi fragments
        navBar = findViewById(R.id.nav_bar);
        navBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            System.out.print(itemId);
            // Lấy Fragment tương ứng từ HashMap
            Fragment fragment = fragmentMap.get(itemId);
            if (fragment != null) {
                // Thay đổi Fragment
                changeFragment(fragment);
                return true;
            } else {
                dialogAlert = new Dialog(AdminActivity.this);
                dialogAlert.setContentView(R.layout.custom_dialog_2);
                dialogAlert.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialogAlert.setCancelable(false);
                dialogAlert.getWindow().setWindowAnimations(R.style.animation);
                btnAccept = (Button) dialogAlert.findViewById(R.id.btn_continue);
                btnCancel = (Button) dialogAlert.findViewById(R.id.btn_cancel);

                btnCancel.setOnClickListener(v -> {
                    dialogAlert.dismiss();
                });
                btnAccept.setOnClickListener(v ->{
                    Intent i = new Intent(AdminActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                });
            }

            return false;
        });
    }

    public void changeFragment(Fragment newFragment) {
        // Lấy đối tượng FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Bắt đầu một FragmentTransaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Thay thế Fragment hiện tại bằng Fragment mới
        fragmentTransaction.replace(R.id.frame_layout, newFragment);

        // Hoàn tất việc thay đổi Fragment
        fragmentTransaction.commit();
    }
}