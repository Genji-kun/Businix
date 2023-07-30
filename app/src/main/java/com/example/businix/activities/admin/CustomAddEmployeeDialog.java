package com.example.businix.activities.admin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.example.businix.R;

public class CustomAddEmployeeDialog extends Dialog {
    private LinearLayout btnAddOne, btnAddList;
    private Context context;

    public CustomAddEmployeeDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Loại bỏ tiêu đề của dialog
        setContentView(R.layout.custom_add_employee_dialog);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);


        btnAddOne = (LinearLayout) findViewById(R.id.btn_add_one_employee);
        btnAddList = (LinearLayout) findViewById(R.id.btn_add_list_employee);

        btnAddOne.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminAddEmployeeActivity.class);
            context.startActivity(intent);
            dismiss();
        });
        btnAddList.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminAddListEmployeeActivity.class);
            context.startActivity(intent);
            dismiss();
        });
        setCanceledOnTouchOutside(true);
    }
}
