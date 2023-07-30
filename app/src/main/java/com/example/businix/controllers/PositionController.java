package com.example.businix.controllers;

import android.util.Log;

import com.example.businix.dao.PositionDAO;
import com.example.businix.models.Position;
import com.example.businix.utils.FindListener;
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
    public void updatePosition(String PositionId, Position position, OnCompleteListener<Void> onCompleteListener) {
        Task<Void> updatePositionTask = positionDAO.updatePosition(PositionId, position);
        updatePositionTask.addOnCompleteListener(onCompleteListener);
    }

    public void deletePosition(String id, OnCompleteListener<Void> onCompleteListener) {
        Task<Void> deletePositionTask = positionDAO.deletePosition(id);
        deletePositionTask.addOnCompleteListener(onCompleteListener);
    }

    public void getPositionList(OnCompleteListener<List<Position>> onCompleteListener) {
        Task<List<Position>> getPositionListTask = positionDAO.getPositionList();
        getPositionListTask.addOnCompleteListener(onCompleteListener);
    }

    public void getPositionById(String PositionId, OnCompleteListener<Position> onCompleteListener) {
        Task<Position> getPositionTask = positionDAO.getPositionById(PositionId);
        getPositionTask.addOnCompleteListener(onCompleteListener);
    }

    public void isExistedPosition(String positionName, FindListener findListener) {
        Task<Position> getPositionTask = positionDAO.getPositionByName(positionName);
        getPositionTask.addOnCompleteListener(task -> {
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
