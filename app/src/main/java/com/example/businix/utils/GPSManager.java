package com.example.businix.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class GPSManager {

    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private final double companyLatitude = 10.762068; // Vĩ độ công ty
    private final double companyLongitude = 106.692808; // Kinh độ công ty
    private OnLocationCheckListener onLocationCheckListener;
    private LocationCallback locationCallback;

    public interface OnLocationCheckListener {
        void onLocationCheck(boolean isAtCompany);
    }

    //10.762068, 106.692808
    //10.8170599, 106.6768298
    public GPSManager(Context context) {
        this.context = context;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
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
                    stopLocationUpdates();
                }
            }
        };
    }
    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public void setOnLocationCheckListener(OnLocationCheckListener listener) {
        this.onLocationCheckListener = listener;
    }

    @SuppressLint("MissingPermission")
    public void checkInAtCompany() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(15000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }
}
