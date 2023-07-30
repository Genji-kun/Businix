package com.example.businix.controllers;

import com.example.businix.dao.StatDAO;
import com.example.businix.utils.StatData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StatController {
    private StatDAO statDAO;
    public StatController() {
        statDAO = new StatDAO();
    }

    public void getAttendancesMonth(Date date, DocumentReference emp, OnCompleteListener<List<StatData.AttendanceData>> onCompleteListener) {
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
        Task<List<StatData.AttendanceData>> getAttendancesDataTask = statDAO.getAttendancesByDate(minTime, maxTime, emp);
        getAttendancesDataTask.addOnCompleteListener(onCompleteListener);
    }
}
