package com.example.businix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;

public class EmployeeActivity extends ActionBar {
    private BottomNavigationView navBar;
    private HashMap<Integer, Fragment> fragmentMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        //SideNav
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportMyActionBar("", false);

        fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.action_home, new HomeFragment());
        fragmentMap.put(R.id.action_profile, new ProfileFragment());
        Fragment homeFragment = fragmentMap.get(R.id.action_home);
        changeFragment(homeFragment);
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