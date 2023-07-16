package com.example.businix.repository;

import com.example.businix.pojo.Position;

import java.util.List;

public interface PositionRepository {
    void addPosition(Position position);
    void updatePosition(String id, Position position);
    void deletePosition(String id);
    List<Position> getPositionList();
}
