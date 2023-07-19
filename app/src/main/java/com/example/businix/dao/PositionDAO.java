package com.example.businix.dao;

import android.util.Log;

import com.example.businix.models.Position;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PositionDAO {
    private FirebaseFirestore db;
    private String collectionPath;

    public PositionDAO() {
        db = FirebaseFirestore.getInstance();
        collectionPath = "positions";
    }

    public void addPosition(Position position) {
        db.collection(collectionPath)
                .add(position)
                .addOnSuccessListener(documentReference -> {
                    Log.d("PositionDAO", "Thêm thành công với ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("PositionDAO", "Error adding document", e);
                });
    }

    public void updatePosition(String id, Position position) {
        db.collection(collectionPath).document(id)
                .set(position)
                .addOnSuccessListener(command -> {
                    Log.d("PositionDAO", "Cập nhật thành công");
                })
                .addOnFailureListener(e -> {
                    Log.w("PositionDAO", "Error updating document", e);
                });
    }

    public void deletePosition(String id) {
        db.collection(collectionPath).document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("PositionDAO", "Xóa thành công");
                })
                .addOnFailureListener(e -> {
                    Log.w("PositionDAO", "Error deleting document", e);
                });
    }

    public Task<List<Position>> getPositionList() {
        CollectionReference positionsRef = db.collection(collectionPath);
        return positionsRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                List<Position> positionList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Position position = document.toObject(Position.class);
                    positionList.add(position);
                }
                return positionList;
            }
            else
                throw task.getException();
        });
    }

    public Task<Position> getPositionById(String positionID) {
        DocumentReference positionRef = db.collection("positions").document(positionID);
        return positionRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    return document.toObject(Position.class);
                }
                else {
                    throw new Exception("Position not found");
                }
            }
            else
                throw task.getException();
        });
    }
}
