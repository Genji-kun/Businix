package com.example.businix.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.businix.R;
import com.example.businix.activities.SignUpActivity;
import com.example.businix.models.Gender;
import com.example.businix.utils.SignUpUtils;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpStepOneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpStepOneFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SignUpUtils utils;
    private List<String> genderItems;
    private ArrayAdapter<String> genderAdapter;

    private TextInputLayout layoutName, layoutDOB, layoutGender, layoutIdentityCard;
    private TextInputEditText inputName, inputDOB, inputIdentityCard;
    private AutoCompleteTextView dropdownGender;

    public TextInputEditText getInputName() {
        return inputName;
    }

    public TextInputEditText getInputDOB() {
        return inputDOB;
    }

    public AutoCompleteTextView getDropdownGender() {
        return dropdownGender;
    }

    public TextInputEditText getInputIdentityCard() {
        return inputIdentityCard;
    }




    public SignUpStepOneFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static SignUpStepOneFragment newInstance(String param1, String param2) {
        SignUpStepOneFragment fragment = new SignUpStepOneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tìm kiếm các View
        RelativeLayout layoutDOB = view.findViewById(R.id.layout_relative_DOB);

        TextInputEditText inputDOB = view.findViewById(R.id.input_DOB);
        ImageView ivDatePicker = view.findViewById(R.id.iv_date_picker);

        // Tạo một DatePicker
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        MaterialDatePicker picker = builder.build();

        layoutDOB.setOnClickListener(v -> {
            inputDOB.setText("Haha");
        });
        // Gán sự kiện nhấn cho ImageView
        ivDatePicker.setOnClickListener(v -> {
            // Hiển thị DatePicker
            FragmentActivity activity = getActivity();
            if (activity != null) {
                picker.show(activity.getSupportFragmentManager(), "DATE_PICKER");
            }
        });

        picker.addOnPositiveButtonClickListener(selection -> {
            // Lấy ngày được chọn dưới dạng Long
            Long selectedDate = (Long) selection;
            // Định dạng ngày theo định dạng mong muốn
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = sdf.format(selectedDate);
            // Cập nhật nội dung của TextInputEditText với ngày được chọn
            inputDOB.setText(formattedDate);
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        utils = new SignUpUtils();

    }
    public int checkStepOne() {
        int result = utils.CheckStepOne(inputName.getText().toString(), inputDOB.getText().toString(), dropdownGender.getText().toString(), inputIdentityCard.getText().toString());
        return result;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up_step_one, container, false);


        layoutGender = (TextInputLayout) view.findViewById(R.id.layout_gender);
        dropdownGender = (AutoCompleteTextView) view.findViewById(R.id.dropdown_gender);
        genderItems = new ArrayList<>();
        Gender[] genders = Gender.values();
        for (Gender gender : genders) {
            genderItems.add(gender.toString());
        }
        genderAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_menu, genderItems);
        dropdownGender.setAdapter(genderAdapter);
        layoutName = (TextInputLayout) view.findViewById(R.id.layout_name);
        layoutDOB = (TextInputLayout) view.findViewById(R.id.layout_DOB);
        layoutIdentityCard = (TextInputLayout) view.findViewById(R.id.layout_identity_card);
        inputName = (TextInputEditText) view.findViewById(R.id.input_name);
        inputDOB = (TextInputEditText) view.findViewById(R.id.input_DOB);
        inputIdentityCard = (TextInputEditText) view.findViewById(R.id.input_identity_card);

        return view;
    }
}