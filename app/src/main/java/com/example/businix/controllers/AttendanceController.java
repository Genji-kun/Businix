package com.example.businix.controllers;

import com.example.businix.dao.AttendanceDAO;
import com.example.businix.models.Attendance;
import com.example.businix.utils.MyFindListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        Task<Void> updateAttendanceTask = attendanceDAO.updateAttendance(id, attendance);
        updateAttendanceTask.addOnCompleteListener(onCompleteListener);
    }
    public void getAttendanceByMonth(Date date, DocumentReference emp, MyFindListener myFindListener) {
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
        Task<Attendance>  getAttendanceByDateTask = attendanceDAO.getAttendanceByDate(minTime, maxTime, emp);
        getAttendanceByDateTask.addOnCompleteListener(task -> {
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

    public void getAttendanceById(String id, MyFindListener myFindListener) {
        Task<Attendance>  getAttendanceByIdTask = attendanceDAO.getAttendanceById(id);
        getAttendanceByIdTask.addOnCompleteListener(task -> {
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

    public void getAttendancesOfEmployeeByMonth(Date date, DocumentReference emp , OnCompleteListener<List<Attendance>> onCompleteListener) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date minTime = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date maxTime = cal.getTime();
        Task<List<Attendance>> getAttendancesDataTask = attendanceDAO.getAttendancesOfEmployeeByDate(minTime, maxTime, emp);
        getAttendancesDataTask.addOnCompleteListener(onCompleteListener);
    }

    public void getStatAttendanceGroupByDate(Date date, OnCompleteListener<Map<Date, List<Attendance>>> onCompleteListener) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date minTime = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date maxTime = cal.getTime();
        Task<Map<Date, List<Attendance>>> getAttendancesDataTask = attendanceDAO.getStatAttendanceGroupByDate(minTime, maxTime);
        getAttendancesDataTask.addOnCompleteListener(onCompleteListener);
    }
}
