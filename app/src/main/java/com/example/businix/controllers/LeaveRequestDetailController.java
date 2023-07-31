package com.example.businix.controllers;


import com.example.businix.dao.LeaveRequestDetailDAO;
import com.example.businix.models.LeaveRequest;
import com.example.businix.models.LeaveRequestDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    public void getDetailsByTime(Date start, Date end, List<LeaveRequest> requests, OnCompleteListener<List<LeaveRequestDetail>> onCompleteListener) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        start = cal.getTime();

        cal.setTime(end);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        end = cal.getTime();
        if (requests.size() > 0) {
            Task<List<LeaveRequestDetail>> getDetailsByTimeTask = leaveRequestDetailDAO.getDetailsByTime(start, end, requests);
            getDetailsByTimeTask.addOnCompleteListener(onCompleteListener);
        }
    }

}
