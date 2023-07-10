package com.example.businix;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpStepThreeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpStepThreeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextInputLayout layoutUsername, layoutPassword, layoutConfirm;
    private TextInputEditText inputUsername, inputPassword, inputConfirm;
    private SignUpUtils utils;

    public TextInputLayout getLayoutUsername() {
        return layoutUsername;
    }

    public TextInputLayout getLayoutPassword() {
        return layoutPassword;
    }

    public TextInputLayout getLayoutConfirm() {
        return layoutConfirm;
    }

    public TextInputEditText getInputUsername() {
        return inputUsername;
    }

    public TextInputEditText getInputPassword() {
        return inputPassword;
    }

    public TextInputEditText getInputConfirm() {
        return inputConfirm;
    }


    public SignUpStepThreeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUpStepThreeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpStepThreeFragment newInstance(String param1, String param2) {
        SignUpStepThreeFragment fragment = new SignUpStepThreeFragment();
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
        utils = new SignUpUtils();
    }

    public int checkStepThree() {
        int result = utils.CheckStepThree(inputUsername.getText().toString(), inputPassword.getText().toString(), inputConfirm.getText().toString());
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up_step_three, container, false);

        layoutUsername = (TextInputLayout) view.findViewById(R.id.layout_username);
        layoutPassword = (TextInputLayout) view.findViewById(R.id.layout_password);
        layoutConfirm = (TextInputLayout) view.findViewById(R.id.layout_confirm);

        inputUsername = (TextInputEditText) view.findViewById(R.id.input_username);
        inputPassword = (TextInputEditText) view.findViewById(R.id.input_password);
        inputConfirm = (TextInputEditText) view.findViewById(R.id.input_confirm);

        inputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                if (password.length() >= 8) {
                    Pattern pattern = Pattern.compile("[^a-zA-z0-9]");
                    Matcher matcher = pattern.matcher(password);
                    boolean isHasSpeChar = matcher.find();
                    if (isHasSpeChar) {
                        layoutPassword.setHelperText("Mật khẩu mạnh");
                        layoutPassword.setError("");
                    } else {
                        layoutPassword.setHelperText("");
                        layoutPassword.setError("Mật khẩu yếu, cần ít nhất 1 ký tự đặc biệt");
                    }
                } else
                    layoutPassword.setHelperText("Mật khẩu cần ít nhất 8 ký tự");
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkConfirm(inputConfirm.getText().toString(), s.toString());
            }
        });

        inputConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String confirm = s.toString();
                checkConfirm(confirm, inputPassword.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    public void checkConfirm(String confirm, String password) {
        if(confirm.length() == 0)
        {
            layoutConfirm.setError("");
            layoutConfirm.setHelperText("");
        }
        if (confirm.equals(password)) {
            layoutConfirm.setHelperText("Mật khẩu khớp");
            layoutConfirm.setError("");
        } else {
            layoutConfirm.setError("Mật khẩu không khớp");
            layoutConfirm.setHelperText("");
        }
    }
}