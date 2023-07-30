package com.example.businix.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.businix.R;
import com.example.businix.controllers.LeaveRequestController;
import com.example.businix.controllers.LeaveRequestDetailController;
import com.example.businix.models.LeaveRequest;
import com.example.businix.models.LeaveRequestDetail;
import com.example.businix.models.LeaveRequestStatus;
import com.example.businix.models.Status;
import com.example.businix.utils.DateUtils;
import com.example.businix.utils.OnSpinnerChangeListener;

import java.util.List;

public class LeaveRequestHistoryAdapter extends ArrayAdapter<LeaveRequest> {

    private List<LeaveRequest> requests;

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

        TextView btnShowDetail = view.findViewById(R.id.btn_show_detail);
        ListView listViewDetail = view.findViewById(R.id.list_view_detail);

        LeaveRequestController leaveRequestController = new LeaveRequestController();
        LeaveRequestDetailController leaveRequestDetailController = new LeaveRequestDetailController();
        leaveRequestDetailController.getLeaveRequestDetailList(leaveRequestController.getLeaveRequestRef(leaveRequest.getId()), task -> {
            if (task.isSuccessful()) {
                List<LeaveRequestDetail> leaveRequestDetailList = task.getResult();
                LeaveDetailHistoryAdapter adapter = new LeaveDetailHistoryAdapter(getContext(), leaveRequestDetailList);
                listViewDetail.setAdapter(adapter);
            }
        });

        View finalView = view;
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

}
