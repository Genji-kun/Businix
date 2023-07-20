package com.example.businix.controllers;

import com.example.businix.dao.PositionDAO;
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

}
