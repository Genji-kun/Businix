package com.example.businix.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.businix.R;

public class CustomDialog extends AlertDialog {
    private TextView btnContinue, btnCancel;
    private TextView tvQuestion, tvMsg;
    private OnClickListener continueClickListener, cancelClickListener;
    private int layout;


    public CustomDialog(Context context, int layout) {
        super(context);
        this.layout = layout;
    }

    public void load() {
        show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = LayoutInflater.from(getContext()).inflate(layout, null);
        setContentView(view);

        btnContinue = view.findViewById(R.id.btn_continue);
        btnCancel = view.findViewById(R.id.btn_cancel);
        tvQuestion = view.findViewById(R.id.question);
        tvMsg = view.findViewById(R.id.msg);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (continueClickListener != null) {
                    continueClickListener.onClick(CustomDialog.this, 1);
                }
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelClickListener != null) {
                    cancelClickListener.onClick(CustomDialog.this, 1);
                }
                dismiss();
            }
        });
    }

    public void setOnContinueClickListener(OnClickListener listener) {
        this.continueClickListener = listener;
    }

    public void setOnCancelClickListener(OnClickListener listener) {
        this.cancelClickListener = listener;
    }

    public void setQuestion(String question) {
        tvQuestion.setText(question);
    }

    public void setMessage(String msg) {
        tvMsg.setText(msg);
    }

    public void setTextBtnContinue(String text) {
        btnContinue.setText(text);
    }

    public void setTextBtnCancel(String text) {
        btnCancel.setText(text);
    }
}