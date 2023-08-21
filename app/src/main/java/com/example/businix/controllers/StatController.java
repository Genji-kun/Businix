package com.example.businix.controllers;

import com.example.businix.dao.AttendanceDAO;
import com.example.businix.dao.EmployeeDAO;
import com.example.businix.dao.PositionDAO;
import com.example.businix.dao.StatDAO;
import com.example.businix.models.Attendance;
import com.example.businix.models.Employee;
import com.example.businix.models.LeaveRequestDetail;
import com.example.businix.models.Position;
import com.example.businix.utils.DateUtils;
import com.example.businix.utils.SalaryData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public class StatController {
    private EmployeeDAO employeeDAO;
    private PositionDAO positionDAO;
    private AttendanceDAO attendanceDAO;
    public StatController() {
        employeeDAO = new EmployeeDAO();
        positionDAO = new PositionDAO();
        attendanceDAO = new AttendanceDAO();
    }

    public void statSlary(Date date, Consumer<Map<Date, SalaryData>> callback) {
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

        employeeDAO.getEmployeeList().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Employee> employeeList = task.getResult();
                positionDAO.getPositionList().addOnCompleteListener(task1 -> {
                    List<Position> positionList = task1.getResult();
                    Map<DocumentReference, Double> searchMap = new HashMap<>();
                    for (Position position : positionList) {
                        DocumentReference posRef = positionDAO.getPositionRef(position.getId());
                        searchMap.put(posRef, position.getSalary());
                    }
                    Map<DocumentReference, Double> empSalary = new HashMap<>();

                    for (Employee emp : employeeList) {
                        DocumentReference empRef = employeeDAO.getEmployeeRef(emp.getId());

                        empSalary.put(empRef, searchMap.get(emp.getPosition()));
                    }
                    attendanceDAO.getStatAttendanceGroupByDate(minTime, maxTime).addOnCompleteListener(task2 -> {
                        Map<Date, List<Attendance>> datas = new TreeMap<>(task2.getResult());
                        Map<Date, SalaryData> salaryData = new TreeMap<>();
                        for (Date d : datas.keySet()) {
                            double workSalary= 0;
                            double overSalary = 0;
                            for (Attendance attendance : datas.get(d)) {
                                Double overHours = attendance.getOvertime();
                                Double workHours = DateUtils.getDiffHours(attendance.getCheckInTime(), attendance.getCheckOutTime());
                                workHours -= overHours;
                                Double sal = empSalary.get(attendance.getEmployee());
                                workSalary += workHours*sal;
                                overSalary += overHours*sal*1.5;
                            }
                            SalaryData salData = new SalaryData();
                            salData.setNormalSalary(workSalary);
                            salData.setOvertimeSalary(overSalary);
                            salaryData.put(d, salData);
                        }
                        callback.accept(salaryData);
                    });
                });
            }
        });

    }
}
