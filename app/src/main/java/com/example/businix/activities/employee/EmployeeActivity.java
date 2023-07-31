package com.example.businix.activities.employee;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.businix.activities.LoginActivity;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.fragments.employee.HomeFragment;
import com.example.businix.fragments.employee.NotificationFragment;
import com.example.businix.fragments.employee.ProfileFragment;
import com.example.businix.R;
import com.example.businix.models.Employee;
import com.example.businix.ui.ActionBar;
import com.example.businix.ui.CustomDialog;
import com.example.businix.utils.DateUtils;
import com.example.businix.utils.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Map;

public class EmployeeActivity extends ActionBar implements NavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView navBar;
    private HashMap<Integer, Fragment> fragmentMap;

    private NavigationView navView;
    private LoginManager loginManager;
    private Employee employee;
    private EmployeeController employeeController;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        employeeController = new EmployeeController();
        loginManager = new LoginManager(this);

        //SideNav
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportMyActionBar("", false, true);


        //nav_view of SideNav
        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);



        //Thêm các fragment vào HashMap
        fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.action_home, new HomeFragment());
        fragmentMap.put(R.id.action_profile, new ProfileFragment());
        fragmentMap.put(R.id.action_notification, new NotificationFragment());

        // Khởi tạo các Fragment và thêm vào FragmentManager
        initFragments();

        // Set Fragment mặc định là HomeFragment
        currentFragment = fragmentMap.get(R.id.action_home);

        String empId = loginManager.getLoggedInUserId();
        if (empId == null) {
            backToLoginActivity();
            finish();
        }
        else {
            getEmployeeInfoAndPerformActions(empId);
        }


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

    private Fragment currentFragment;
    private void changeFragment(Fragment newFragment) {
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
            if (newFragment instanceof NotificationFragment) {
                ((NotificationFragment) newFragment).loadNotifications();
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

    private void backToLoginActivity() {
        Intent i = new Intent(EmployeeActivity.this, LoginActivity.class);
        startActivity(i);
    }

    private void getEmployeeInfoAndPerformActions(String empId) {
        employeeController.getEmployeeById(empId, new OnCompleteListener<Employee>() {
            @Override
            public void onComplete(Task<Employee> task) {
                EmployeeActivity.this.employee = task.getResult();
                View headerView = navView.getHeaderView(0);
                ((TextView) headerView.findViewById(R.id.tv_name)).setText(employee.getFullName());
                Glide.with(headerView).load(employee.getAvatar()).into((ImageView) headerView.findViewById(R.id.iv_avatar));
                ((TextView) headerView.findViewById(R.id.tv_username)).setText(employee.getUsername());


                Fragment homeFragment = fragmentMap.get(R.id.action_home);
                if (homeFragment instanceof HomeFragment) {
                    ((HomeFragment) homeFragment).setEmployeeName(employee.getFullName());
                }
                Fragment profileFragment = fragmentMap.get(R.id.action_profile);
                if (profileFragment instanceof ProfileFragment) {
                    Map<String, String> infoMap = new HashMap<>();
                    infoMap.put("avatar", employee.getAvatar());
                    infoMap.put("name", employee.getFullName());
                    infoMap.put("dob", DateUtils.formatDate(employee.getDob()));
                    infoMap.put("identityNum", "0798987575357");
                    infoMap.put("address", employee.getAddress());
                    infoMap.put("phone", employee.getPhone());
                    infoMap.put("email", employee.getEmail());
                    infoMap.put("startDate", DateUtils.formatDate(employee.getCreateAt()));
                    ((ProfileFragment) profileFragment).setInfo(infoMap);

                    employee.getPosition().get().addOnSuccessListener(documentSnapshot -> {
                        String positionName = documentSnapshot.getString("name");
                        ((ProfileFragment) profileFragment).setPosition(positionName);
                    });

                    employee.getDepartment().get().addOnSuccessListener(documentSnapshot -> {
                        String departmentName = documentSnapshot.getString("name");
                        ((ProfileFragment) profileFragment).setDepartment(departmentName);
                    });
                }
            }
        });
    }

    private void initFragments() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for (Map.Entry<Integer, Fragment> entry : fragmentMap.entrySet()) {
            Fragment fragment = entry.getValue();
            int itemId = entry.getKey();

            // Thêm Fragment vào FragmentManager nếu chưa tồn tại
            if (fragmentManager.findFragmentByTag(fragment.getClass().getSimpleName()) == null) {
                fragmentTransaction.add(R.id.frame_layout, fragment, fragment.getClass().getSimpleName());
            }

            // Ẩn các Fragment trừ HomeFragment
            if (itemId != R.id.action_home) {
                fragmentTransaction.hide(fragment);
            }
        }
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
        }
        return false;
    }

}