package com.example.businix.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.businix.fragments.SeenNotiFragment;
import com.example.businix.fragments.UnseenNotiFragment;
import com.example.businix.fragments.AllNotiFragment;

public class MyViewPagerAdapter extends FragmentStateAdapter {
    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new UnseenNotiFragment();
            case 1:
                return new SeenNotiFragment();
            default:
                return new AllNotiFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
