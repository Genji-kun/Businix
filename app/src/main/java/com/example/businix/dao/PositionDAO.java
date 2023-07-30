package com.example.businix.dao;

import android.util.Log;

import com.example.businix.models.Department;
import com.example.businix.models.Position;
import com.example.businix.utils.FirestoreUtils;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PositionDAO {
    private FirebaseFirestore db;
    private String collectionPath;

    public PositionDAO() {
        db = FirebaseFirestore.getInstance();
        collectionPath = "positions";
    }

    public Task<Void> addPosition(Position position) {
        return db.collection(collectionPath)
                .add(position)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        Log.d("PositionDAO", "Thêm thành công với ID: " + task.getResult().getId());
                        return null;
                    } else {
                        Log.e("PositionDAO", "Error adding document", task.getException());
                        throw task.getException();
                    }
                });
    }

    public Task<Void> updatePosition(String id, Position position) {
        if (position == null || id.isEmpty()) {
            Log.e("PositionDAO", "id không hợp lệ");
            return Tasks.forException(new IllegalArgumentException("id không hợp lệ"));
        }

        Map<String, Object> updates;
        try {
            updates = FirestoreUtils.prepareUpdates(position);
        } catch (IllegalAccessException e) {
            Log.e("PositionDAO", "Không lấy được dữ liệu updates.", e);
            return Tasks.forException(e);
        }

        DocumentReference documentRef = db.collection(collectionPath).document(id);
        return documentRef.update(updates)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        Log.d("PositionDAO", "Position cập nhật thành công.");
                        return null; // Trả về null để biểu thị việc thành công
                    } else {
                        Log.e("PositionDAO", "Position cập nhật thất bại.", task.getException());
                        throw task.getException();
                    }
                });
    }

    public Task<Void> deletePosition(String id) {
        return db.collection(collectionPath).document(id).delete()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        Log.d("PositionDAO", "Xóa thành công");
                        return null;
                    } else {
                        Log.e("PositionDAO", "Error deleting document", task.getException());
                        throw task.getException();
                    }
                });
    }

    public Task<List<Position>> getPositionList() {
        CollectionReference positionsRef = db.collection(collectionPath);
        return positionsRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                List<Position> positionList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Position position = document.toObject(Position.class);
                    position.setId(document.getId());
                    positionList.add(position);
                }
                return positionList;
            } else {
                Log.e("DepartmentDAO", "Lỗi khi lấy list department", task.getException());
                throw task.getException();
            }
        });
    }

    public Task<Position> getPositionById(String positionID) {
        DocumentReference positionRef = db.collection("positions").document(positionID);
        return positionRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Position position = document.toObject(Position.class);
                    position.setId(document.getId());
                    return position;
                } else {
                    throw new Exception("Position not found");
                }
            } else
                throw task.getException();
        });
    }

    public Task<Position> getPositionByName(String positionName) {
        return db.collection("positions")
                .whereEqualTo("name", positionName)
                .limit(1)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            if (document.exists()) {
                                Position position = document.toObject(Position.class);
                                position.setId(document.getId());
                                return position;
                            } else {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    } else {
                        throw task.getException();
                    }
                });
    }

    public DocumentReference getPositionRef(String id) {
        if (id != null) {
            return db.collection(collectionPath).document(id);
        } else {
            return null;
        }
    }
}
