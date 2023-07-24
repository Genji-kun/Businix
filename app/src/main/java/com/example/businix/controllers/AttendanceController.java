package com.example.businix.controllers;

import com.example.businix.dao.AttendanceDAO;
import com.example.businix.models.Attendance;
import com.example.businix.utils.DateUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.Date;

public class AttendanceController {
    private AttendanceDAO attendanceDAO;

    private Attendance setLateAndOverTime(Attendance attendance) {
        if (attendance.getCheckInTime() != null) {
            Date checkInTime = attendance.getCheckInTime();
            Calendar cal = Calendar.getInstance();
            cal.setTime(checkInTime);
            cal.set(Calendar.HOUR_OF_DAY, 8);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date fixedTime = cal.getTime();

            Double diffInMinutes = DateUtils.getDiffInMinutes(checkInTime, fixedTime);
            if (diffInMinutes >= 15) {
                attendance.setLateHours(diffInMinutes/60);
            }
            else {
                attendance.setLateHours(0);
            }
        }
        if (attendance.getCheckOutTime() != null) {
            Date checkOutTime = attendance.getCheckOutTime();
            Calendar cal = Calendar.getInstance();
            cal.setTime(checkOutTime);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            if (hour > 17) {
                cal.set(Calendar.HOUR_OF_DAY, 17); // Đặt giờ là 9 giờ
                cal.set(Calendar.MINUTE, 0); // Đặt phút là 0
                cal.set(Calendar.SECOND, 0); // Đặt giây là 0
                cal.set(Calendar.MILLISECOND, 0); // Đặt mili giây là 0
                Date fixedTime = cal.getTime();
                Double diffInMinutes = DateUtils.getDiffInMinutes(checkOutTime, fixedTime);
                if (diffInMinutes >= 30) {
                    attendance.setOverHours(diffInMinutes/60);
                }
                else {
                    attendance.setOverHours(0);
                }
            }
            else {
                attendance.setOverHours(0);
            }

        }
        return attendance;
    }
    public AttendanceController() {
        attendanceDAO = new AttendanceDAO();
    }

    public void addAttendance(Attendance attendance, OnCompleteListener<Void> onCompleteListener) {
        attendance = setLateAndOverTime(attendance);
        Task<Void> addAttendanceTask = attendanceDAO.addAttendance(attendance);
        addAttendanceTask.addOnCompleteListener(onCompleteListener);
    }

    public void updateAttendance(String id, Attendance attendance, OnCompleteListener<Void> onCompleteListener) {
        attendance = setLateAndOverTime(attendance);
        Task<Void> updateAttendanceTask = attendanceDAO.updateAttandance(id, attendance);
        updateAttendanceTask.addOnCompleteListener(onCompleteListener);
    }
    public void getAttandanceByDate(Date minTime, Date maxTime)
}
