package com.example.businix.models;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.util.Calendar;
import java.util.Date;

public class Attendance {
    @Exclude
    private String id;
    private Date checkInTime;
    private Date  checkOutTime;
    private DocumentReference employee;
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

    public DocumentReference getEmployee() {
        return employee;
    }

    public void setEmployee(DocumentReference employee) {
        this.employee = employee;
    }
}
