package com.example.businix.controllers;

import com.example.businix.dao.LeaveRequestDAO;
import com.example.businix.models.LeaveRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

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

    public void getLeaveRequestById(String leaveRequestId, OnCompleteListener<LeaveRequest> onCompleteListener) {
        Task<LeaveRequest> getLeaveRequestTask = leaveRequestDAO.getLeaveRequestById(leaveRequestId);
        getLeaveRequestTask.addOnCompleteListener(onCompleteListener);
    }

    public DocumentReference getLeaveRequestRef(String id) {
        return leaveRequestDAO.getLeaveRequestRef(id);
    }
}