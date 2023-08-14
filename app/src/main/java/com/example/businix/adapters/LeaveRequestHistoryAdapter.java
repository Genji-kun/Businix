package com.example.businix.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.example.businix.R;
import com.example.businix.controllers.EmployeeController;
import com.example.businix.controllers.LeaveRequestController;
import com.example.businix.controllers.LeaveRequestDetailController;
import com.example.businix.controllers.NotificationController;
import com.example.businix.models.Department;
import com.example.businix.models.Employee;
import com.example.businix.models.LeaveRequest;
import com.example.businix.models.LeaveRequestDetail;
import com.example.businix.models.LeaveRequestStatus;
import com.example.businix.models.Notification;
import com.example.businix.models.Position;
import com.example.businix.utils.DateUtils;
import com.example.businix.utils.LoginManager;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class LeaveRequestHistoryAdapter extends ArrayAdapter<LeaveRequest> {

    private List<LeaveRequest> requests;
    private TextView btnAccept, btnDeny;
    private LinearLayout layoutEmplInfo, layoutButtons;

    private boolean isVisible = false;
    private boolean isVisibleForHistory = false;

    public LeaveRequestHistoryAdapter(Context context, List<LeaveRequest> requests) {
        super(context, R.layout.list_view_leave_history_items, requests);
        this.requests = requests;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.list_view_leave_history_items, parent, false);
        }

        TextView tvCreatedAt = view.findViewById(R.id.created_at);
        TextView tvStartDate = view.findViewById(R.id.start_date);
        TextView tvEndDate = view.findViewById(R.id.end_date);

        LeaveRequestController leaveRequestController = new LeaveRequestController();
        LeaveRequest leaveRequest = requests.get(position);
        String createAt = DateUtils.formatDate(leaveRequest.getCreatedAt());
        String startDate = DateUtils.formatDate(leaveRequest.getFromDate());
        String endDate = DateUtils.formatDate(leaveRequest.getToDate());
        tvCreatedAt.setText(createAt);
        tvStartDate.setText(startDate);
        tvEndDate.setText(endDate);

        TextView tvStatus = view.findViewById(R.id.status);
        ImageView ivStatus = view.findViewById(R.id.ic_status);
        LinearLayout lnStatus = view.findViewById(R.id.status_container);
        setStatus(tvStatus, ivStatus, lnStatus, leaveRequest.getStatus());

        TextView tvEmplName = view.findViewById(R.id.tv_empl_name);
        ImageView imgAvatar = view.findViewById(R.id.img_avatar);
        TextView tvEmplJob = view.findViewById(R.id.tv_empl_job);
        leaveRequest.getEmployee().get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Employee empl = documentSnapshot.toObject(Employee.class);
                String emplName = empl.getFullName();
                tvEmplName.setText(emplName);
                Glide.with(getContext()).load(empl.getAvatar()).into(imgAvatar);
                empl.getPosition().get().addOnSuccessListener(documentSnapshot1 -> {
                    if (documentSnapshot1.exists()) {
                        Position pos = documentSnapshot1.toObject(Position.class);
                        String posName = pos.getName();
                        AtomicReference<String> emplJob = new AtomicReference<>(posName + "/");
                        empl.getDepartment().get().addOnSuccessListener(documentSnapshot2 -> {
                            if (documentSnapshot2.exists()) {
                                Department dpm = documentSnapshot2.toObject(Department.class);
                                String dpmName = dpm.getName();
                                emplJob.set(emplJob + dpmName);
                                tvEmplJob.setText(emplJob.get());
                            }
                        });
                    }
                });
            }
        });

        layoutEmplInfo = view.findViewById(R.id.layout_empl_info);
        layoutButtons = view.findViewById(R.id.layout_buttons);
        updateButtonVisibility(isVisible);
        updateButtonVisibilityForHistory(isVisibleForHistory);

        EmployeeController employeeController = new EmployeeController();
        LoginManager loginManager = new LoginManager(getContext());
        Notification notification = new Notification();

        btnAccept = view.findViewById(R.id.btn_accept_request);
        btnAccept.setOnClickListener(v -> {
            leaveRequest.setStatus(LeaveRequestStatus.ACCEPT);
            leaveRequestController.updateLeaveRequest(leaveRequest.getId(), leaveRequest, task -> {
                if (task.isSuccessful()) {
                    requests.remove(position);
                    Toast.makeText(getContext(), "Duyệt thành công!", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();

                    //Gửi notification
                    notification.setRead(false);
                    notification.setSender(employeeController.getEmployeeRef(loginManager.getLoggedInUserId()));
                    notification.setTitle("Đơn nghỉ được duyệt");
                    String msg = "Đơn nghỉ từ ngày " + DateUtils.formatDate(leaveRequest.getFromDate()) + " đến " + DateUtils.formatDate(leaveRequest.getToDate()) + " đã được duyệt bởi Administrator";
                    notification.setMessage(msg);
                    List<DocumentReference> receivers = new ArrayList<>();
                    receivers.add(leaveRequest.getEmployee());
                    notification.setReceivers(receivers);
                    (new NotificationController()).addNotification(notification, task4 -> {
                        if (task4.isSuccessful()) {

                        }
                    });
                } else {
                    Log.e("LeaveRequestController", "Error update leave request");
                    Toast.makeText(getContext(), "Lỗi khi thực hiện!", Toast.LENGTH_SHORT).show();
                }
            });

        });

        btnDeny = view.findViewById(R.id.btn_deny_request);
        btnDeny.setOnClickListener(v -> {
            leaveRequest.setStatus(LeaveRequestStatus.REJECT);
            leaveRequestController.updateLeaveRequest(leaveRequest.getId(), leaveRequest, task -> {
                if (task.isSuccessful()) {
                    requests.remove(position);
                    Toast.makeText(getContext(), "Từ chối thành công!", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();

                    //Gửi notification
                    notification.setRead(false);
                    notification.setSender(employeeController.getEmployeeRef(loginManager.getLoggedInUserId()));
                    notification.setTitle("Đơn nghỉ bị từ chối");
                    String msg = "Đơn nghỉ từ ngày " + DateUtils.formatDate(leaveRequest.getFromDate()) + " đến " + DateUtils.formatDate(leaveRequest.getToDate()) + " không được xét duyệt bới Administrator";
                    notification.setMessage(msg);
                    List<DocumentReference> receivers = new ArrayList<>();
                    receivers.add(leaveRequest.getEmployee());
                    notification.setReceivers(receivers);
                    (new NotificationController()).addNotification(notification, task4 -> {
                        if (task4.isSuccessful()) {

                        }
                    });
                } else {
                    Log.e("LeaveRequestController", "Error update leave request");
                    Toast.makeText(getContext(), "Lỗi khi thực hiện!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        TextView btnShowDetail = view.findViewById(R.id.btn_show_detail);
        ListView listViewDetail = view.findViewById(R.id.list_view_detail);

        LeaveRequestDetailController leaveRequestDetailController = new LeaveRequestDetailController();
        leaveRequestDetailController.getLeaveRequestDetailList(leaveRequestController.getLeaveRequestRef(leaveRequest.getId()), task -> {
            if (task.isSuccessful()) {
                List<LeaveRequestDetail> leaveRequestDetailList = task.getResult();
                LeaveDetailHistoryAdapter adapter = new LeaveDetailHistoryAdapter(getContext(), leaveRequestDetailList);
                listViewDetail.setAdapter(adapter);
            }
        });

        btnShowDetail.setOnClickListener(v -> {
            if (listViewDetail.getVisibility() == View.GONE) {
                listViewDetail.setVisibility(View.VISIBLE);
                listViewDetail.post(() -> {
                    setHeightListView(listViewDetail, listViewDetail.getAdapter().getCount());
                });
                btnShowDetail.setText("Ẩn chi tiết");
            } else {
                listViewDetail.setVisibility(View.GONE);
                btnShowDetail.setText("Hiện chi tiết");
            }
        });
        return view;
    }

    private void setStatus(TextView text, ImageView img, LinearLayout container, LeaveRequestStatus status) {
        switch (status) {
            case ACCEPT:
                text.setText("Đã duyệt");
                text.setTextColor(getContext().getResources().getColor(R.color.accept_line, null));
                img.setImageDrawable(getContext().getDrawable(R.drawable.ic_status_accept));
                img.setImageTintList(AppCompatResources.getColorStateList(getContext(), R.color.accept_line));
                container.setBackgroundTintList(AppCompatResources.getColorStateList(getContext(), R.color.accept));
                break;
            case PENDING:
                text.setText("Chờ duyệt");
                text.setTextColor(getContext().getResources().getColor(R.color.waiting_line, null));
                img.setImageDrawable(getContext().getDrawable(R.drawable.ic_status_waiting));
                img.setImageTintList(AppCompatResources.getColorStateList(getContext(), R.color.waiting_line));
                container.setBackgroundTintList(AppCompatResources.getColorStateList(getContext(), R.color.waiting));
                break;
            case REJECT:
                text.setText("Không duyệt");
                text.setTextColor(getContext().getResources().getColor(R.color.reject_line, null));
                img.setImageDrawable(getContext().getDrawable(R.drawable.ic_status_reject));
                img.setImageTintList(AppCompatResources.getColorStateList(getContext(), R.color.reject_line));
                container.setBackgroundTintList(AppCompatResources.getColorStateList(getContext(), R.color.reject));
                break;
        }
    }

    private void setHeightListView(ListView listView, int totalItems) {
        listView.post(() -> {
            int height = totalItems * (160 + 3) + 10;
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = height;
            listView.setLayoutParams(params);
        });
    }

    public void updateButtonVisibility(boolean isVisible) {
        layoutEmplInfo.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        layoutButtons.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
    public void updateButtonVisibilityForHistory(boolean isVisibleForHistory) {
        layoutEmplInfo.setVisibility(isVisibleForHistory ? View.VISIBLE : View.GONE);
    }

    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void setIsVisibleForHistory(boolean isVisibleForHistory) {
        this.isVisibleForHistory = isVisibleForHistory;
    }

}
