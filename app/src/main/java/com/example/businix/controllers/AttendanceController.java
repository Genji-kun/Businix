package com.example.businix.controllers;

import com.example.businix.dao.AttendanceDAO;
import com.example.businix.models.Attendance;
import com.example.businix.utils.MyFindListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.Calendar;
import java.util.Date;

public class AttendanceController {
    private AttendanceDAO attendanceDAO;
    public AttendanceController() {
        attendanceDAO = new AttendanceDAO();
    }

    public void addAttendance(Attendance attendance, OnCompleteListener<Void> onCompleteListener) {
        Task<Void> addAttendanceTask = attendanceDAO.addAttendance(attendance);
        addAttendanceTask.addOnCompleteListener(onCompleteListener);
    }

    public void updateAttendance(String id, Attendance attendance, OnCompleteListener<Void> onCompleteListener) {
        Task<Void> updateAttendanceTask = attendanceDAO.updateAttandance(id, attendance);
        updateAttendanceTask.addOnCompleteListener(onCompleteListener);
    }
    public void getAttandanceByMonth(Date date, DocumentReference emp, MyFindListener myFindListener) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date minTime = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date maxTime = cal.getTime();
        Task<Attendance>  getAttandanceByDateTask = attendanceDAO.getAttandanceByDate(minTime, maxTime, emp);
        getAttandanceByDateTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                if (task.getResult() == null) {
                    myFindListener.onNotFound();
                }
                else {
                    Attendance attendance = task.getResult();
                    myFindListener.onFoundSuccess(attendance);
                }
            }
            else
                myFindListener.onFail();
        });
    }

    public void getAttandanceById(String id, MyFindListener myFindListener) {
        Task<Attendance>  getAttandanceByIdTask = attendanceDAO.getAttendanceById(id);
        getAttandanceByIdTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                if (task.getResult() != null) {
                    Attendance attendance = task.getResult();
                    myFindListener.onFoundSuccess(attendance);
                }
                else {
                    myFindListener.onNotFound();
                }
            }
            else
                myFindListener.onFail();
        });
    }
}
