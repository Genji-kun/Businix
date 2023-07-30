package com.example.businix.controllers;


import com.example.businix.dao.LeaveRequestDetailDAO;
import com.example.businix.models.LeaveRequest;
import com.example.businix.models.LeaveRequestDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class LeaveRequestDetailController {
    private LeaveRequestDetailDAO leaveRequestDetailDAO;

    public LeaveRequestDetailController() {
        leaveRequestDetailDAO = new LeaveRequestDetailDAO();
    }

    public void addLeaveRequestDetail(LeaveRequestDetail leaveRequestDetail, OnCompleteListener<Void> onCompleteListener) {
        Task<Void> addLeaveRequestTask = leaveRequestDetailDAO.addLeaveRequestDetail(leaveRequestDetail);
        addLeaveRequestTask.addOnCompleteListener(onCompleteListener);
    }

    public void addLeaveRequestDetails(List<LeaveRequestDetail> leaveRequestDetails, OnCompleteListener<Void> onCompleteListener) {
        Task<Void> addLeaveRequestDetailsTask = leaveRequestDetailDAO.addLeaveRequestDetails(leaveRequestDetails);
        addLeaveRequestDetailsTask.addOnCompleteListener(onCompleteListener);
    }

    public void getLeaveRequestDetailList(DocumentReference leaveRequest, OnCompleteListener<List<LeaveRequestDetail>> onCompleteListener) {
        Task<List<LeaveRequestDetail>> getDetailListTask = leaveRequestDetailDAO.getLeaveRequestDetailList(leaveRequest);
        getDetailListTask.addOnCompleteListener(onCompleteListener);
    }

}
