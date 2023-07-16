package com.example.businix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class LeaveActivity extends ActionBar {
    private Spinner spinnerType;
    private LinearLayout showDetail;
    private LinearLayout detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        showDetail = (LinearLayout) findViewById(R.id.show_detail);
        detail = (LinearLayout) findViewById(R.id.detail);
        spinnerType = (Spinner) findViewById(R.id.spinner_type);

        setSupportMyActionBar("Nghỉ phép", true, true);

        ArrayList<String> arrayType = new ArrayList<String>();
        arrayType.add("Nghỉ phép thường");
        arrayType.add("Nghỉ phép năm");
        arrayType.add("Nghỉ đặc quyền");
        arrayType.add("Nghỉ ốm");
        arrayType.add("Nghỉ thai sản");
        arrayType.add("Nghỉ kết hôn");
        arrayType.add("Nghỉ tang chế");
        arrayType.add("Nghỉ bù trừ");

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.item_spinner, arrayType);
        arrayAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerType.setAdapter(arrayAdapter);

        showDetail.setOnClickListener(v -> {
            if (detail.getVisibility() == View.GONE)
                detail.setVisibility(View.VISIBLE);
            else
                detail.setVisibility(View.GONE);
        });
    }
}