package com.example.businix.controllers;
import android.util.Log;

import com.example.businix.dao.DepartmentDAO;
import com.example.businix.models.Department;
import com.example.businix.models.Position;
import com.example.businix.utils.FindListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class DepartmentController {
    private DepartmentDAO departmentDAO;
    public DepartmentController() {
        departmentDAO = new DepartmentDAO();
    }

    public void addDepartment(Department department, OnCompleteListener<Void> onCompleteListener) {
        Task<Void> addDepartmentTask = departmentDAO.addDepartment(department);
        addDepartmentTask.addOnCompleteListener(onCompleteListener);
    }

    public void updateDepartment(String departmentId, Department department, OnCompleteListener<Void> onCompleteListener) {
        Task<Void> updateDepartmentTask = departmentDAO.updateDepartment(departmentId, department);
        updateDepartmentTask.addOnCompleteListener(onCompleteListener);
    }

    public void deleteDepartment(String id, OnCompleteListener<Void> onCompleteListener) {
        Task<Void> deleteDepartmentTask = departmentDAO.deleteDepartment(id);
        deleteDepartmentTask.addOnCompleteListener(onCompleteListener);
    }

    public void getDepartmentList(OnCompleteListener<List<Department>> onCompleteListener) {
        Task<List<Department>> getDepartmentListTask = departmentDAO.getDepartmentList();
        getDepartmentListTask.addOnCompleteListener(onCompleteListener);
    }

    public void getDepartmentById(String departmentId, OnCompleteListener<Department> onCompleteListener) {
        Task<Department> getDepartmentTask =departmentDAO.getDepartmentById(departmentId);
        getDepartmentTask.addOnCompleteListener(onCompleteListener);
    }

    public void isExistedDepartment(String departmentName, FindListener findListener) {
        Task<Department> getDepartmentTask = departmentDAO.getDepartmentByName(departmentName);
        getDepartmentTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    findListener.onFoundSuccess();
                }
                else
                    findListener.onNotFound();
            }
            else
                Log.e("PositionController", "Lỗi", task.getException());
        });

    }
}
