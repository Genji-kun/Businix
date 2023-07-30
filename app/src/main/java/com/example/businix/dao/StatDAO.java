package com.example.businix.dao;

import android.util.Log;

import com.example.businix.models.Attendance;
import com.example.businix.utils.DateUtils;
import com.example.businix.utils.StatData;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StatDAO {
    private FirebaseFirestore db;

    public StatDAO() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<List<StatData.AttendanceData>> getAttendancesByDate(Date minTime, Date maxTime, DocumentReference emp) {
        Query query = db.collection("attendances");
        if (minTime != null && maxTime != null) {
            query = query.whereGreaterThanOrEqualTo("checkInTime", minTime).whereLessThanOrEqualTo("checkInTime", maxTime)
                    .whereEqualTo("employee", emp);
        } else {
            return null;
        }
        return query.get().continueWith(task -> {
            if (task.isSuccessful()) {
                List<StatData.AttendanceData> attendancesData = new ArrayList<>();
                if (!task.getResult().isEmpty()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        Attendance attendance = documentSnapshot.toObject(Attendance.class);
                        StatData.AttendanceData attendanceData = new StatData.AttendanceData();

                        if (attendance.getCheckInTime() == null || attendance.getCheckOutTime() == null)
                            continue;
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(attendance.getCheckInTime());
                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);
                        attendanceData.setDate(cal.getTime());

                        cal.set(Calendar.HOUR_OF_DAY, 8);
                        Date startTime = attendance.getCheckInTime();
                        double lateHours = 0;

                        if (cal.getTime().after(startTime)) {
                            startTime = cal.getTime();
                        }
                        else {
                            double temp = DateUtils.getDiffHours(startTime, cal.getTime());
                            if (temp*60 > 15) {
                                lateHours = temp;
                            }
                        }


                        Date endTime = attendance.getCheckOutTime();
                        double overtimeHours = 0;
                        double workHours = DateUtils.getDiffHours(startTime, endTime);
                        cal.set(Calendar.HOUR_OF_DAY, 17);
                        if (endTime.after(cal.getTime())) {
                            if (cal.getTime().after(startTime)) {
                                overtimeHours = DateUtils.getDiffHours(endTime, cal.getTime());
                                workHours -= overtimeHours;
                                workHours = workHours < 0 ? 0 : workHours;
                            } else {
                                overtimeHours = workHours;
                                workHours = 0;
                            }

                        }
                        attendanceData.setWorkHours(workHours);
                        attendanceData.setOverTimeHours(overtimeHours);
                        attendanceData.setLateHours(lateHours);
                        attendancesData.add(attendanceData);
                    }
                }
                return attendancesData;
            } else {
                Log.e("AttendanceDAO", "Lỗi khi truy xuất danh sách (getAttendancesByDate)", task.getException());
                throw task.getException();
            }
        });
    }

}