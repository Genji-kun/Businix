package com.example.businix.models;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.util.Date;

public class LeaveRequestDetail {
    @Exclude
    private
    String id;
    private Date date;
    private String shift;
    private DocumentReference leaveRequest;

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getShift() {
        return shift;
    }

    @Exclude
    public int getShiftPosition() {
        switch (shift) {
            case "Cả ngày":
                return 0;
            case "Ca sáng":
                return 1;
            case "Ca chiều":
                return 2;
            default:
                return -1;
        }
    }

    @Exclude
    public void setShiftByPosition(int position) {
        switch (position) {
            case 0:
                shift = "Cả ngày";
                break;
            case 1:
                shift = "Ca sáng";
                break;
            case 2:
                shift = "Ca chiều";
                break;
            default:
                shift = "";
                break;
        }
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public DocumentReference getLeaveRequest() {
        return leaveRequest;
    }

    public void setLeaveRequest(DocumentReference leaveRequest) {
        this.leaveRequest = leaveRequest;
    }
}
