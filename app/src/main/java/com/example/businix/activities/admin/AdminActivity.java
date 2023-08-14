package com.example.businix.activities.admin;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.businix.activities.employee.ChangePasswordActivity;
import com.example.businix.activities.employee.EditProfileActivity;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.fragments.admin.AdminHomeFragment;
import com.example.businix.fragments.admin.AdminNotificationFragment;
import com.example.businix.R;
import com.example.businix.fragments.admin.AdminProfileFragment;
import com.example.businix.fragments.employee.NotificationFragment;
import com.example.businix.fragments.employee.ProfileFragment;
import com.example.businix.models.Employee;
import com.example.businix.ui.ActionBar;
import com.example.businix.ui.CustomDialog;
import com.example.businix.utils.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;

public class AdminActivity extends ActionBar implements NavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView navBar;
    private HashMap<Integer, Fragment> fragmentMap;
    private NavigationView navView;
    private LoginManager loginManager;
    private Employee admin;
    private EmployeeController employeeController;
    private ActivityResultLauncher<Intent> launcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Employee emp = (Employee) data.getExtras().get("employee");
                        reloadProfile(emp);
                        navBar.setSelectedItemId(R.id.action_profile);
                    }
                }
        );
        employeeController = new EmployeeController();
        loginManager = new LoginManager(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.admin_drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportMyActionBar("", false, true);
        setTitleText("BUSINIX", 20, R.font.airbeat, R.color.light_purple);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
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
            if (itemId == R.id.action_notification)
                setTitleText("Thông báo");
            else if (itemId == R.id.action_home) {
                setTitleText("BUSINIX", 20, R.font.airbeat, R.color.light_purple);
            } else if (itemId == R.id.action_profile){
                setTitleText("Tiện ích");
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

    private Fragment currentFragment;

    public void changeFragment(Fragment newFragment) {
        // Lấy đối tượng FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Kiểm tra nếu fragment hiện tại là fragment mới, thì không làm gì cả
        if (currentFragment != null && currentFragment.getClass() == newFragment.getClass()) {
            return;
        }

        // Bắt đầu một FragmentTransaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Ẩn fragment hiện tại nếu đã tồn tại, nhưng không remove nó
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }

        // Nếu fragment đã tồn tại trong FragmentManager, chỉ cần show lại
        if (fragmentManager.findFragmentByTag(newFragment.getClass().getSimpleName()) != null) {
            if (newFragment instanceof AdminNotificationFragment) {
                ((AdminNotificationFragment) newFragment).loadNotifications();
            }
            fragmentTransaction.show(newFragment);
        } else {
            // Nếu fragment chưa tồn tại, thì add vào
            fragmentTransaction.add(R.id.frame_layout, newFragment, newFragment.getClass().getSimpleName());
        }

        // Lưu fragment mới vào currentFragment
        currentFragment = newFragment;

        // Hoàn tất việc thay đổi Fragment
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_logout) {
            CustomDialog confirmDialog = new CustomDialog(this, R.layout.custom_dialog_2);
            confirmDialog.show();
            confirmDialog.setQuestion("Xác nhận đăng xuất");
            confirmDialog.setMessage("Bạn có chắc chắn muốn đăng xuất không?");
            confirmDialog.setOnContinueClickListener((dialog, which) -> {
                finish();
            });
        } else if (itemId == R.id.nav_edit_personal) {
            Intent i = new Intent(AdminActivity.this, EditProfileActivity.class);
            launcher.launch(i);
        }
        else if (itemId == R.id.nav_edit_security) {
            Intent i = new Intent(AdminActivity.this, ChangePasswordActivity.class);
            startActivity(i);
        }
        return false;
    }

    private void reloadProfile(Employee emp) {
        Fragment profileFragment = fragmentMap.get(R.id.action_profile);
        if (profileFragment instanceof ProfileFragment) {
            ((ProfileFragment) profileFragment).setInfo(emp);
        }
    }
}