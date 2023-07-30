package com.example.businix.controllers;

import android.util.Log;

import com.example.businix.dao.DepartmentDAO;
import com.example.businix.dao.EmployeeDAO;
import com.example.businix.dao.PositionDAO;
import com.example.businix.models.Department;
import com.example.businix.models.Employee;
import com.example.businix.models.Status;
import com.example.businix.models.UserRole;
import com.example.businix.utils.AuthenticationListener;
import com.example.businix.utils.FindListener;
import com.example.businix.utils.PasswordHash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class EmployeeController {
    private EmployeeDAO employeeDAO;
    private PositionDAO positionDAO;
    private DepartmentDAO departmentDAO;

    public EmployeeController() {
        employeeDAO = new EmployeeDAO();
        positionDAO = new PositionDAO();
        departmentDAO = new DepartmentDAO();
    }

    public void addEmployee(Employee employee, OnCompleteListener<Void> onCompleteListener) {
        Task<Void> addEmployeeTask = employeeDAO.addEmployee(employee);
        addEmployeeTask.addOnCompleteListener(onCompleteListener);
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

    public void getEmployeeListByRole(UserRole userRole, OnCompleteListener<List<DocumentReference>> onCompleteListener) {
        Task<List<DocumentReference>> getEmployeeListTask = employeeDAO.getEmployeeListByRole(userRole);
        getEmployeeListTask.addOnCompleteListener(onCompleteListener);
    }

    public void getEmployeeById(String employeeId, OnCompleteListener<Employee> onCompleteListener) {
        Task<Employee> getEmployeeTask = employeeDAO.getEmployeeById(employeeId);
        getEmployeeTask.addOnCompleteListener(onCompleteListener);
    }

    public void checkUser(String username, String password, AuthenticationListener listener) {
        employeeDAO.getEmployeeByUsername(username)
                .addOnSuccessListener(employee -> {
                    String hashPass = employee.getPassword();
                    if (PasswordHash.checkPassword(password, hashPass)) {
                        //Mật khẩu đúng
                        listener.onAuthenticationSuccess(employee);
                    } else {
                        //Sai mật khẩu
                        listener.onPasswordIncorrect();
                    }
                })
                .addOnFailureListener(e -> {
                    // lỗi hoặc username không tồn tại
                    listener.onUsernameNotFound();
                });
    }

    public DocumentReference getEmployeeRef(String id) {
        return employeeDAO.getEmployeeRef(id);
    }
    public void checkUserExist(String username, FindListener findListener){
        Task<Employee> getEmployeeTask = employeeDAO.getEmployeeByUsername(username);
        getEmployeeTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    findListener.onFoundSuccess();
                }
                else
                    findListener.onNotFound();
            }
            else
                Log.e("EmployeeController", "Lỗi", task.getException());
        });
    }

    public void getEmployeeListByStatus(Status status, OnCompleteListener<List<Employee>> onCompleteListener) {
        Task<List<Employee>> getEmployeeListTask = employeeDAO.getEmployeeListByStatus(status);
        getEmployeeListTask.addOnCompleteListener(onCompleteListener);
    }

    public DocumentReference getPositionRef(String id) {
        return positionDAO.getPositionRef(id);
    }

    public DocumentReference getDepartmentRef(String id) {
        return departmentDAO.getDepartmentRef(id);
    }
}