package com.example.businix.fragments.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.businix.R;
import com.example.businix.adapters.MyViewPagerAdapter;
import com.example.businix.controllers.NotificationController;
import com.example.businix.fragments.employee.NotificationFragment;
import com.example.businix.fragments.employee.NotificationItemsFragment;
import com.example.businix.models.Notification;
import com.example.businix.utils.LoginManager;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class AdminNotificationFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private NotificationController notificationController;

    public AdminNotificationFragment() {
        // Required empty public constructor
    }

    public static AdminNotificationFragment newInstance(String param1, String param2) {
        AdminNotificationFragment fragment = new AdminNotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        notificationController = new NotificationController();
        View view = inflater.inflate(R.layout.fragment_admin_notification, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        viewPager = (ViewPager2) view.findViewById(R.id.view_pager);
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new NotificationItemsFragment());
        fragmentList.add(new NotificationItemsFragment());
        fragmentList.add(new NotificationItemsFragment());
        myViewPagerAdapter = new MyViewPagerAdapter(getActivity(), fragmentList);
        viewPager.setAdapter(myViewPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    public void loadNotifications() {
        LoginManager loginManager = new LoginManager(getActivity());
        notificationController.getNotifications(loginManager.getLoggedInUserId(), task -> {
            if (task.isSuccessful()) {
                AdminNotificationFragment.this.updateNotificationData(task.getResult());
            }
        });
    }

    private void updateNotificationData(List<Notification> notificationList) {
        List<Notification> unseenNotificationList = new ArrayList<>();
        List<Notification> seenNotificationList = new ArrayList<>();
        notificationList.forEach(notification -> {
            if (notification.getRead()) {
                seenNotificationList.add(notification);
            } else {
                unseenNotificationList.add(notification);
                for (Notification noti :unseenNotificationList){
                    noti.setRead(true);
                }
            }
        });
        MyViewPagerAdapter myAdapter = (MyViewPagerAdapter) viewPager.getAdapter();
        ((NotificationItemsFragment) myAdapter.getFragment(0)).setNotificationList(unseenNotificationList);
        ((NotificationItemsFragment) myAdapter.getFragment(1)).setNotificationList(seenNotificationList);
        ((NotificationItemsFragment) myAdapter.getFragment(2)).setNotificationList(notificationList);

    }
}