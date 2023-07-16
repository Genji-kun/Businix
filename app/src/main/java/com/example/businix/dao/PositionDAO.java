package com.example.businix.dao;

import android.util.Log;

import com.example.businix.pojo.Position;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentReference;
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
        TaskCompletionSource<List<Position>> taskCompletionSource = new TaskCompletionSource<>();
        List<Position> positionList = new ArrayList<>();
        db.collection(collectionPath)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Position position = document.toObject(Position.class);
                            positionList.add(position);
                        }
                        taskCompletionSource.setResult(positionList);
                    } else {
                        Log.w("PositionDAO", "Error getting documents.", task.getException());
                        taskCompletionSource.setException(task.getException());
                    }
                });
        return taskCompletionSource.getTask();
    }

    public DocumentReference getPositionDocumentReferenceByID(String positionID) {
        return db.collection(collectionPath).document(positionID);
    }
}
