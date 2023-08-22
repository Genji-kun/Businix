package com.example.businix.controllers;

import com.example.businix.dao.LeaveRequestDAO;
import com.example.businix.models.LeaveRequest;
import com.example.businix.models.LeaveRequestStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LeaveRequestController {
    private LeaveRequestDAO leaveRequestDAO;

    public LeaveRequestController() {
        leaveRequestDAO = new LeaveRequestDAO();
    }

    public void addLeaveRequest(LeaveRequest leaveRequest, OnCompleteListener<String> onCompleteListener) {
        Task<String> addLeaveRequestTask = leaveRequestDAO.addLeaveRequest(leaveRequest);
        addLeaveRequestTask.addOnCompleteListener(onCompleteListener);
    }

    public void updateLeaveRequest(String leaveRequestId, LeaveRequest leaveRequest, OnCompleteListener<Void> onCompleteListener) {
        Task<Void> updateLeaveRequestTask = leaveRequestDAO.updateLeaveRequest(leaveRequestId, leaveRequest);
        updateLeaveRequestTask.addOnCompleteListener(onCompleteListener);
    }

    public void getLeaveRequestList(OnCompleteListener<List<LeaveRequest>> onCompleteListener) {
        Task<List<LeaveRequest>> getLeaveRequestListTask = leaveRequestDAO.getLeaveRequestList();
        getLeaveRequestListTask.addOnCompleteListener(onCompleteListener);
    }

    public void getLeaveRequestListOfEmployee(DocumentReference emp, OnCompleteListener<List<LeaveRequest>> onCompleteListener) {
        Task<List<LeaveRequest>> getLeaveRequestListTask = leaveRequestDAO.getLeaveRequestListOfEmployee(emp);
        getLeaveRequestListTask.addOnCompleteListener(onCompleteListener);
    }

    public void getLeaveRequestById(String leaveRequestId, OnCompleteListener<LeaveRequest> onCompleteListener) {
        Task<LeaveRequest> getLeaveRequestTask = leaveRequestDAO.getLeaveRequestById(leaveRequestId);
        getLeaveRequestTask.addOnCompleteListener(onCompleteListener);
    }

    public DocumentReference getLeaveRequestRef(String id) {
        return leaveRequestDAO.getLeaveRequestRef(id);
    }

    public void getLeaveRequestOfEmployeeOverlapping(Date minTime, Date maxTime, LeaveRequestStatus status, DocumentReference emp, OnCompleteListener<List<LeaveRequest>> onCompleteListener) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(minTime);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        minTime = cal.getTime();

        cal.setTime(maxTime);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        maxTime = cal.getTime();

        Task<List<LeaveRequest>> getRequestOverlapping = leaveRequestDAO.getLeaveRequestOfEmployeeOverlapping(minTime, maxTime, status ,emp);
        getRequestOverlapping.addOnCompleteListener(onCompleteListener);
    }

    public void getLeaveRequestOverlapping(Date minTime, Date maxTime, LeaveRequestStatus status, OnCompleteListener<List<LeaveRequest>> onCompleteListener) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(minTime);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        minTime = cal.getTime();

        cal.setTime(maxTime);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        maxTime = cal.getTime();

        Task<List<LeaveRequest>> getRequestOverlapping = leaveRequestDAO.getLeaveRequestOverlapping(minTime, maxTime, status);
        getRequestOverlapping.addOnCompleteListener(onCompleteListener);
    }

    public void getLeaveRequestListByStatus(LeaveRequestStatus status, OnCompleteListener<List<LeaveRequest>> onCompleteListener) {
        Task<List<LeaveRequest>> getLeaveRequestListTask = leaveRequestDAO.getLeaveRequestListByStatus(status);
        getLeaveRequestListTask.addOnCompleteListener(onCompleteListener);
    }
}