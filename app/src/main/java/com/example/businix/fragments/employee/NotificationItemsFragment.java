package com.example.businix.fragments.employee;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.businix.R;
import com.example.businix.adapters.NotifcationAdapter;
import com.example.businix.models.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationItemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationItemsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private List<Notification> notificationList = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView listView;
    private NotifcationAdapter adapter;

    public NotificationItemsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllNotiFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationItemsFragment newInstance(String param1, String param2) {
        NotificationItemsFragment fragment = new NotificationItemsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_items, container, false);
        adapter = new NotifcationAdapter(getContext(), notificationList);
        listView = view.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
//        updateListView();
        return view;
    }

    public void setNotificationList(List<Notification> notificationList) {
        this.notificationList = notificationList;
        updateListView(); // Gọi phương thức để cập nhật dữ liệu khi có danh sách notification mới
    }

    private void updateListView() {
        if (getContext() != null) {
            adapter = new NotifcationAdapter(getContext(), notificationList);
            if (listView != null)
                listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}