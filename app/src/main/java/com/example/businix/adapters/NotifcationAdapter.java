package com.example.businix.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.businix.R;
import com.example.businix.models.Notification;

import java.util.List;

public class NotifcationAdapter extends ArrayAdapter<Notification> {

    private List<Notification> notificationList;

    public NotifcationAdapter(Context context, List<Notification> notificationList) {
        super(context, R.layout.list_view_notification, notificationList);
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.list_view_notification, parent, false);
        }

        Notification notification = notificationList.get(position);

        TextView title = view.findViewById(R.id.title);
        TextView message = view.findViewById(R.id.message);

        title.setText(notification.getTitle());
        message.setText(notification.getMessage());
        return view;
    }
}
