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
import com.example.businix.activities.admin.AdminEditDepartmentActivity;
import com.example.businix.controllers.DepartmentController;
import com.example.businix.models.Department;

import java.util.ArrayList;
import java.util.List;

public class DepartmentAdapter extends ArrayAdapter<Department> {
    private List<Department> departmentList;
    private List<Department> filteredList;
    private Context context;

    public DepartmentAdapter(Context context, int resource, List<Department> departmentList) {
        super(context, resource, departmentList);
        this.departmentList = departmentList;
        this.filteredList = departmentList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.list_view_department, null);
        }
        Department department = filteredList.get(position);
        if (department != null) {
            TextView tvDepartmentName = (TextView) view.findViewById(R.id.tv_department_name);
            if (tvDepartmentName != null) {
                tvDepartmentName.setText(department.getName());
            }
            TextView tvDepartmentNumber = (TextView) view.findViewById(R.id.tv_department_number);
            if (tvDepartmentNumber != null) {
                tvDepartmentNumber.setText(String.valueOf(position + 1));
            }

            LinearLayout btnEditDepartment = (LinearLayout) view.findViewById(R.id.btn_edit_department);
            btnEditDepartment.setOnClickListener(v -> {
                Department currentDepartment = filteredList.get(position);

                // Gửi thông tin của vị trí hiện tại qua Activity mới
                Intent intent = new Intent(context, AdminEditDepartmentActivity.class);
                intent.putExtra("departmentId", currentDepartment.getId()); // Gửi ID của phòng ban
                intent.putExtra("departmentName", currentDepartment.getName()); // Gửi tên phòng ban
                context.startActivity(intent);
            });

            LinearLayout btnDeleteDepartment = (LinearLayout) view.findViewById(R.id.btn_delete_department);
            btnDeleteDepartment.setOnClickListener(v -> {
                String departmentIdToDelete = departmentList.get(position).getId(); // Lấy id của vị trí từ nút xóa

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_2, null);
                builder.setView(dialogView);

                AlertDialog alertDialog = builder.create();
                TextView tvQuestion = (TextView) dialogView.findViewById(R.id.question);
                tvQuestion.setText("Bạn có muốn xóa phòng ban này không?");
                TextView tvMsg = (TextView) dialogView.findViewById(R.id.msg);
                tvMsg.setText("Việc xóa có thể ảnh hưởng hệ thống, làm mất đi dữ liệu hiện tại, hãy cân nhắc trước khi xóa");
                alertDialog.show();

                TextView btnConfirmDelete = (TextView) dialogView.findViewById(R.id.btn_continue);
                btnConfirmDelete.setOnClickListener(mv -> {
                    alertDialog.dismiss();
                    // Xác nhận xóa, gọi phương thức deletePosition
                    DepartmentController departmentController = new DepartmentController();
                    departmentController.deleteDepartment(departmentIdToDelete, task -> {
                        if (task.isSuccessful()) {
                            // Xóa thành công, bạn có thể cập nhật lại danh sách và thông báo cho adapter
                            departmentList.remove(department); // Xóa vị trí khỏi danh sách dự phòng
                            notifyDataSetChanged(); // Cập nhật lại ListView
                            Toast.makeText(context, "Xóa phòng ban thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Xử lý lỗi khi xóa không thành công
                            Log.e("AdminDepartmentManagement", "Error deleting department");
                            Toast.makeText(context, "Lỗi khi xóa phòng ban!", Toast.LENGTH_SHORT).show();
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
                    results.values = departmentList;
                    results.count = departmentList.size();
                } else {
                    List<Department> filteredDepartments = new ArrayList<>();
                    for (Department department : departmentList) {
                        if (department.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filteredDepartments.add(department);
                        }
                    }
                    results.values = filteredDepartments;
                    results.count = filteredDepartments.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<Department>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
