package com.example.businix.pojo;

import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

public class AbsentMail {
    private Date fromDate;
    private Date toDate;
    private String reason;
    private DocumentReference employee;

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public DocumentReference getEmployee() {
        return employee;
    }

    public void setEmployee(DocumentReference employee) {
        this.employee = employee;
    }
}
