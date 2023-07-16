package com.example.businix.dao;

import com.example.businix.pojo.Employee;
import com.google.android.gms.tasks.Task;

import java.util.List;

public interface EmployeeRepository {
    void addEmployee(Employee Employee);
    void updateEmployee(String id, Employee Employee);
    void deleteEmployee(String id);
    Task<List<Employee>> getEmployeeList();
}
