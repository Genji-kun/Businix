package com.example.businix.activities.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.example.businix.activities.LoginActivity;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.fragments.admin.AdminHomeFragment;
import com.example.businix.fragments.admin.AdminNotificationFragment;
import com.example.businix.R;
import com.example.businix.fragments.admin.AdminAbsentMailFragment;
import com.example.businix.fragments.admin.AdminProfileFragment;
import com.example.businix.models.Employee;
import com.example.businix.ui.ActionBar;
import com.example.businix.utils.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;

public class AdminActivity extends ActionBar {
    private BottomNavigationView navBar;
    private HashMap<Integer, Fragment> fragmentMap;
    private NavigationView navView;
    private LoginManager loginManager;
    private EmployeeController employeeController;
    private Employee admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        employeeController = new EmployeeController();
        loginManager = new LoginManager(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.admin_drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportMyActionBar("", false, true);
        setTitleText("BUSINIX", 16, R.font.airbeat, R.color.light_purple);
        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return false;
            }
        });

        //Thêm fragments vào danh sách
        fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.action_home, new AdminHomeFragment());
        fragmentMap.put(R.id.action_notification, new AdminNotificationFragment());
        fragmentMap.put(R.id.action_profile, new AdminProfileFragment());

        //Set fragment mặc định là home
        Fragment homeFragment = fragmentMap.get(R.id.action_home);
        changeFragment(homeFragment);

        //Viết sự thay đổi fragments
        navBar = findViewById(R.id.nav_bar);
        navBar.setOnItemSelectedListener(item -> {

            //Lấy id của item được chọn
            int itemId = item.getItemId();
            if (itemId == R.id.action_home)
                setTitleText("THÔNG BÁO");
            else if (itemId == R.id.action_home) {
                setTitleText("BUSINIX", 20, R.font.airbeat, R.color.light_purple);
            } else {
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