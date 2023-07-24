package com.example.businix.models;

import com.google.firebase.firestore.Exclude;
import java.util.Date;

public class Attendance {
    @Exclude
    private String id;
    private Date checkInTime;
    private Date  checkOutTime;
    private double lateHours;
    private double overHours;

    @Exclude
    public String getId() {
        return id;
    }
    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public Date  getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Date  checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Date  getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(Date  checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public double getLateHours() {
        return lateHours;
    }

    public void setLateHours(double lateHours) {
        this.lateHours = lateHours;
    }

    public double getOverHours() {
        return overHours;
    }

    public void setOverHours(double overHours) {
        this.overHours = overHours;
    }
}
