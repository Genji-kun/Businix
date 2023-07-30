package com.example.businix.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class GPSManager {

    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private final double companyLatitude = 10.8170599; // Vĩ độ công ty
    private final double companyLongitude = 106.6768298; // Kinh độ công ty
    private OnLocationCheckListener onLocationCheckListener;

    public interface OnLocationCheckListener {
        void onLocationCheck(boolean isAtCompany);
    }

    //10.762068, 106.692808
    //10.8170599, 106.6768298
    public GPSManager(Context context) {
        this.context = context;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void setOnLocationCheckListener(OnLocationCheckListener listener) {
        this.onLocationCheckListener = listener;
    }

    @SuppressLint("MissingPermission")
    public void checkInAtCompany() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double currentLatitude = location.getLatitude();
                            double currentLongitude = location.getLongitude();

                            float[] results = new float[1];
                            Location.distanceBetween(currentLatitude, currentLongitude, companyLatitude, companyLongitude, results);
                            float distanceToCompany = results[0];

                            // Khoảng cách kiểm tra (tùy chỉnh theo yêu cầu của bạn)
                            float thresholdDistance = 100.0f; // Đơn vị mét, ví dụ 100 mét

                            boolean isAtCompany = distanceToCompany <= thresholdDistance;

                            if (onLocationCheckListener != null) {
                                onLocationCheckListener.onLocationCheck(isAtCompany);
                            }
                        }
                    }
                });
    }
}
