package com.example.businix;

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

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;

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

    public TextInputLayout getLayoutName() {
        return layoutName;
    }

    public TextInputLayout getLayoutDOB() {
        return layoutDOB;
    }

    public TextInputLayout getLayoutGender() {
        return layoutGender;
    }

    public TextInputLayout getLayoutIdentityCard() {
        return layoutIdentityCard;
    }

    public interface StepOneData {
        public void onDataPass(String data);
    }



    public SignUpStepOneFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUpStepOneFragment.
     */
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
        String[] items = {"Nam", "Nữ", "Khác"};
        ArrayAdapter<String> itemAdapter = new ArrayAdapter<String>(getContext(), R.layout.dropdown_menu, items);
        dropdownGender.setAdapter(itemAdapter);
        dropdownGender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        layoutName = (TextInputLayout) view.findViewById(R.id.layout_name);
        layoutDOB = (TextInputLayout) view.findViewById(R.id.layout_DOB);
        layoutIdentityCard = (TextInputLayout) view.findViewById(R.id.layout_identity_card);

        inputName = (TextInputEditText) view.findViewById(R.id.input_name);
        inputDOB = (TextInputEditText) view.findViewById(R.id.input_DOB);
        inputIdentityCard = (TextInputEditText) view.findViewById(R.id.input_identity_card);
        return view;
    }
}