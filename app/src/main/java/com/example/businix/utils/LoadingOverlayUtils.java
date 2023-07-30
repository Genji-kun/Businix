package com.example.businix.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ProgressBar;

import com.example.businix.R;

public class LoadingOverlayUtils {

    private static Dialog loadingDialog;

    public static void showLoadingOverlay(Context context) {
        loadingDialog = new Dialog(context);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(R.layout.loading_overlay);
        loadingDialog.show();
    }

    public static void hideLoadingOverlay() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
}
