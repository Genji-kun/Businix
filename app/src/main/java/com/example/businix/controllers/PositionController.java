package com.example.businix.controllers;

import com.example.businix.dao.PositionDAO;
import com.example.businix.models.Employee;
import com.example.businix.models.Position;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class PositionController {

    private PositionDAO positionDAO;

    public PositionController() {
        positionDAO = new PositionDAO();
    }

    public void addPosition(Position position, OnCompleteListener<Void> onCompleteListener) {
        Task<Void> addPositionTask = positionDAO.addPosition(position);
        addPositionTask.addOnCompleteListener(onCompleteListener);
    }

    public void updateEmployee(String employeeId, Employee employee, OnCompleteListener<Void> onCompleteListener) {
        Task<Void> updateEmployeeTask = employeeDAO.updateEmployee(employeeId, employee);
        updateEmployeeTask.addOnCompleteListener(onCompleteListener);
    }

    public void deleteEmployee(String id, OnCompleteListener<Void> onCompleteListener) {
        Task<Void> deleteEmployeeTask = employeeDAO.deleteEmployee(id);
        deleteEmployeeTask.addOnCompleteListener(onCompleteListener);
    }

    public void getEmployeeList(OnCompleteListener<List<Employee>> onCompleteListener) {
        Task<List<Employee>> getEmployeeListTask = employeeDAO.getEmployeeList();
        getEmployeeListTask.addOnCompleteListener(onCompleteListener);
    }
}
