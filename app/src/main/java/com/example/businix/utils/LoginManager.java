package com.example.businix.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginManager {
    private SharedPreferences sharedPreferences;
    private static final String PREF_KEY_USER_ID = "user_id";

    public LoginManager(Context context) {
        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    }

    public void setLoggedInUserId(String userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_KEY_USER_ID, userId);
        editor.apply();
    }

    public String getLoggedInUserId() {
        return sharedPreferences.getString(PREF_KEY_USER_ID, null);
    }

    public void clearLoggedInUserId() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(PREF_KEY_USER_ID);
        editor.apply();
    }
}
