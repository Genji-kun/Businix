package com.example.businix.activities;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.businix.fragments.HomeFragment;
import com.example.businix.fragments.NotificationFragment;
import com.example.businix.fragments.ProfileFragment;
import com.example.businix.R;
import com.example.businix.models.Employee;
import com.example.businix.ui.ActionBar;
import com.example.businix.ui.CustomDialog;
import com.example.businix.utils.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;

public class EmployeeActivity extends ActionBar {
    private BottomNavigationView navBar;
    private HashMap<Integer, Fragment> fragmentMap;

    private NavigationView navView;
    private LoginManager loginManager;
    private Employee employee;
    private CustomDialog customDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        customDialog = new CustomDialog(this, R.layout.custom_dialog_2);
        loginManager = new LoginManager(this);

        String empId = loginManager.getLoggedInUserId();
        if (empId == null) {
            customDialog.show();
            customDialog.setQuestion("Username hoặc password rỗng");
            customDialog.setMessage("Vui lòng nhập đầy đủ thông tin để đăng nhập");
            customDialog.setOnCancelClickListener((dialog, which) -> {
                dialog.dismiss();
            });
            customDialog.setOnContinueClickListener((dialog, which) -> {
                dialog.dismiss();
            });
            return;
        }

        //SideNav
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportMyActionBar("", false);


        //nav_view of SideNav
        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        //Thêm các fragment vào HashMap
        fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.action_home, new HomeFragment());
        fragmentMap.put(R.id.action_profile, new ProfileFragment());
        fragmentMap.put(R.id.action_notification, new NotificationFragment());

        //Set fragment mặc định là home
        Fragment homeFragment = fragmentMap.get(R.id.action_home);
        changeFragment(homeFragment);


        navBar = findViewById(R.id.nav_bar);
        navBar.setOnItemSelectedListener(item -> {

            //Lấy id của item được chọn
            int itemId = item.getItemId();
            if (itemId == R.id.action_notification)
                setTitleText("THÔNG BÁO");
            else {
                setTitleText("");
            }
            // Lấy Fragment tương ứng từ HashMap
            Fragment fragment = fragmentMap.get(itemId);
            if (fragment != null) {
                // Thay đổi Fragment
                changeFragment(fragment);
                return true;
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