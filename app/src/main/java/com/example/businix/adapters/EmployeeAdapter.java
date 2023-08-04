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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.businix.R;
import com.example.businix.activities.admin.AdminEditEmployeeActivity;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.models.Employee;
import com.example.businix.models.Position;
import com.example.businix.models.Status;

import java.util.ArrayList;
import java.util.List;

public class EmployeeAdapter extends ArrayAdapter<Employee> {
    private List<Employee> employeeList;
    private List<Employee> filteredList;
    private Context context;

    public EmployeeAdapter(Context context, int resource, List<Employee> employeeList) {
        super(context, resource, employeeList);

        this.employeeList = employeeList;
        this.filteredList = employeeList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.list_view_employee, null);
        }
        EmployeeController employeeController = new EmployeeController();
        Employee employee = filteredList.get(position);
        if (employee != null) {
            ImageView imgAvatar = (ImageView) view.findViewById(R.id.img_avatar);
            if (imgAvatar != null)
                Glide.with(context).load(employee.getAvatar()).into(imgAvatar);
            TextView tvEmplName = (TextView) view.findViewById(R.id.tv_empl_name);
            if (tvEmplName != null) {
                tvEmplName.setText(employee.getFullName());
            }
            TextView tvPosName = (TextView) view.findViewById(R.id.tv_position_name);
            if (tvPosName != null) {
                employee.getPosition().get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Position pos = documentSnapshot.toObject(Position.class);
                        String positionName = pos.getName();
                        if (employee.getUserRole().toString().equals("ADMIN"))
                            tvPosName.setText("Admin");
                        else
                            tvPosName.setText(positionName);
                    } else {
                        tvPosName.setText("Chưa có chức vụ");
                    }
                });
            }

            LinearLayout btnEditEmployee = (LinearLayout) view.findViewById(R.id.btn_edit_employee);
            btnEditEmployee.setOnClickListener(v -> {
                Employee currentEmployee = filteredList.get(position);

                // Gửi thông tin của vị trí hiện tại qua Activity mới
                Intent intent = new Intent(context, AdminEditEmployeeActivity.class);
                intent.putExtra("employeeId", currentEmployee.getId());
                intent.putExtra("employeeUsername", currentEmployee.getUsername());
                context.startActivity(intent);
            });
            LinearLayout btnDeleteEmployee = (LinearLayout) view.findViewById(R.id.btn_delete_employee);
            btnDeleteEmployee.setOnClickListener(v -> {
                String employeeId = employeeList.get(position).getId(); // Lấy id của nhân viên từ nút xóa

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_2, null);
                builder.setView(dialogView);

                AlertDialog alertDialog = builder.create();
                TextView tvQuestion = (TextView) dialogView.findViewById(R.id.question);
                tvQuestion.setText("Bạn có muốn xóa nhân viên này không?");
                TextView tvMsg = (TextView) dialogView.findViewById(R.id.msg);
                tvMsg.setText("Việc xóa nhân viên sẽ mất thời gian nếu nhầm lẫn, hãy cân nhắc trước khi xóa");
                alertDialog.show();

                TextView btnConfirmDelete = (TextView) dialogView.findViewById(R.id.btn_continue);
                btnConfirmDelete.setOnClickListener(mv -> {
                    alertDialog.dismiss();
                    // Xác nhận xóa, gọi phương thức deletePosition
                    employeeController.deleteEmployee(employeeId, task -> {
                        if (task.isSuccessful()) {
                            // Xóa thành công, bạn có thể cập nhật lại danh sách và thông báo cho adapter
                            employeeList.remove(employee); // Xóa nhân viên khỏi danh sách dự phòng
                            notifyDataSetChanged(); // Cập nhật lại ListView
                            Toast.makeText(context, "Xóa nhân viên thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Xử lý lỗi khi xóa không thành công
                            Log.e("AdminEmployeeManagement", "Error deleting employee");
                            Toast.makeText(context, "Lỗi khi xóa nhân viên!", Toast.LENGTH_SHORT).show();
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
                    results.values = employeeList;
                    results.count = employeeList.size();
                } else {
                    List<Employee> filteredEmployees = new ArrayList<>();
                    for (Employee empl : employeeList) {
//                        boolean matchPosition = selectedPositionName.isEmpty() || positionName.equals(selectedPositionName);
                        boolean matchName = empl.getFullName().toLowerCase().contains(constraint.toString().toLowerCase());
//                        boolean matchStatus = selectedStatus.isEmpty() || empl.getStatus().equals(selectedStatus);
                        if (matchName) {
                            filteredEmployees.add(empl);
                        }
                    }

                    results.values = filteredEmployees;
                    results.count = filteredEmployees.size();

                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<Employee>) results.values;
                notifyDataSetChanged();
            }

        };
    }

}
