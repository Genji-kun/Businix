package com.example.businix;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ScrollView scrollView;
    private Toolbar toolbar;

    private TextView btnShowPersonal;
    private TextView btnShowWork;
    private LinearLayout infoPersonal;
    private LinearLayout infoWork;
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        btnShowPersonal = (TextView) view.findViewById(R.id.btn_show_personal);
        btnShowWork = (TextView) view.findViewById(R.id.btn_show_work);
        infoPersonal = (LinearLayout) view.findViewById(R.id.info_personal);
        infoWork = (LinearLayout) view.findViewById(R.id.info_work);

        btnShowWork.setOnClickListener(v -> {
            btnShowPersonal.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.white, null)));
            btnShowPersonal.setTextColor(ResourcesCompat.getColor(getResources(), R.color.dark_gray, null));


            btnShowWork.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.light_purple, null)));
            btnShowWork.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));

            infoWork.setVisibility(View.VISIBLE);
            infoPersonal.setVisibility(View.GONE);
        });

        btnShowPersonal.setOnClickListener(v -> {
            btnShowWork.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.white, null)));
            btnShowWork.setTextColor(ResourcesCompat.getColor(getResources(), R.color.dark_gray, null));

            btnShowPersonal.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.light_purple, null)));
            btnShowPersonal.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));

            infoPersonal.setVisibility(View.VISIBLE);
            infoWork.setVisibility(View.GONE);
        });
//        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
//        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
//        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
//            int scrollY = scrollView.getScrollY();
//            if (scrollY > 50) {
//                toolbar.setBackgroundResource(R.drawable.gradient_purple_to_transparent);
//            } else {
//                toolbar.setBackgroundResource(R.color.transparent);
//            }
//        });
        return view;
    }

}