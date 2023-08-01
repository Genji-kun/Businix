package com.example.businix.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.businix.R;
import com.example.businix.activities.admin.AdminEditPositionActivity;
import com.example.businix.controllers.PositionController;
import com.example.businix.models.Position;

import java.util.ArrayList;
import java.util.List;

public class PositionAdapter extends ArrayAdapter<Position> {
    private List<Position> positionList;
    private List<Position> filteredList;
    private Context context;

    public PositionAdapter(Context context, int resource, List<Position> positionList) {
        super(context, resource, positionList);
        this.positionList = positionList;
        this.filteredList = positionList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.list_view_position, null);
        }
        Position pos = filteredList.get(position);
        if (pos != null) {
            TextView tvPosName = (TextView) view.findViewById(R.id.tv_pos_name);
            if (tvPosName != null) {
                tvPosName.setText(pos.getName());
            }
            TextView tvPosNumber = (TextView) view.findViewById(R.id.tv_position_number);
            if (tvPosNumber != null) {
                tvPosNumber.setText(String.valueOf(position + 1));
            }
            LinearLayout btnEditPosition = (LinearLayout) view.findViewById(R.id.btn_edit_position);
            btnEditPosition.setOnClickListener(v -> {
                Position currentPosition = filteredList.get(position);

                // Gửi thông tin của vị trí hiện tại qua Activity mới
                Intent intent = new Intent(context, AdminEditPositionActivity.class);
                intent.putExtra("positionId", currentPosition.getId()); // Gửi ID của vị trí
                intent.putExtra("positionName", currentPosition.getName()); // Gửi tên vị trí
                intent.putExtra("salary", currentPosition.getSalary().toString()); // Gửi lương
                context.startActivity(intent);
            });
            LinearLayout btnDeletePosition = (LinearLayout) view.findViewById(R.id.btn_delete_position);
            btnDeletePosition.setOnClickListener(v -> {
                String positionIdToDelete = positionList.get(position).getId(); // Lấy id của vị trí từ nút xóa

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_2, null);
                builder.setView(dialogView);

                AlertDialog alertDialog = builder.create();
                TextView tvQuestion = (TextView) dialogView.findViewById(R.id.question);
                tvQuestion.setText("Bạn có muốn xóa thành phần này không?");
                TextView tvMsg = (TextView) dialogView.findViewById(R.id.msg);
                tvMsg.setText("Việc xóa thành phần sẽ làm mất đi dữ liệu hiện tại, hãy cân nhắc trước khi xóa");
                alertDialog.show();

                TextView btnConfirmDelete = (TextView) dialogView.findViewById(R.id.btn_continue);
                btnConfirmDelete.setOnClickListener(mv -> {
                    alertDialog.dismiss();
                    // Xác nhận xóa, gọi phương thức deletePosition
                    PositionController positionController = new PositionController();
                    positionController.deletePosition(positionIdToDelete, task -> {
                        if (task.isSuccessful()) {
                            // Xóa thành công, bạn có thể cập nhật lại danh sách và thông báo cho adapter
                            positionList.remove(pos); // Xóa vị trí khỏi danh sách dự phòng
                            notifyDataSetChanged(); // Cập nhật lại ListView
                            Toast.makeText(context, "Xóa vị trí thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Xử lý lỗi khi xóa không thành công
                            Log.e("AdminPositionManagement", "Error deleting position");
                            Toast.makeText(context, "Lỗi khi xóa vị trí!", Toast.LENGTH_SHORT).show();
                        }
                    });
                });

                TextView btnCancelDelete = (TextView) dialogView.findViewById(R.id.btn_cancel);
                btnCancelDelete.setOnClickListener(mv -> {
                    alertDialog.dismiss();
                    // Hủy xóa, không làm gì cả
                });
            });
        }
        return view;
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = positionList;
                    results.count = positionList.size();
                } else {
                    List<Position> filteredPositions = new ArrayList<>();
                    for (Position pos : positionList) {
                        if (pos.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filteredPositions.add(pos);
                        }
                    }
                    results.values = filteredPositions;
                    results.count = filteredPositions.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<Position>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}