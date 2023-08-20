package com.example.businix.dao;

import android.util.Log;

import com.example.businix.models.Attendance;
import com.example.businix.models.Employee;
import com.example.businix.models.LeaveRequestDetail;
import com.example.businix.utils.DateUtils;
import com.example.businix.utils.SalaryData;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatDAO {
    private FirebaseFirestore db;
    private EmployeeDAO employeeDAO;
    private PositionDAO positionDAO;
    private AttendanceDAO attendanceDAO;
    public StatDAO() {
        db = FirebaseFirestore.getInstance();
        employeeDAO = new EmployeeDAO();
        positionDAO = new PositionDAO();
        attendanceDAO = new AttendanceDAO();
    }

//        public Task<Map<DocumentReference, Double>> getEmployeeSalary() {

//    }

}