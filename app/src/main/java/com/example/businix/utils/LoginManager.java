package com.example.businix.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginManager {
    private SharedPreferences sharedPreferences;
    private static final String PREF_KEY_USER_ID = "user_id";
    private static final String PREF_KEY_USER_ROLE = "role_name";

    public LoginManager(Context context) {
        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    }

    public void setLoggedInUserId(String userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_KEY_USER_ID, userId);
        editor.apply();
    }

    public void setLoggedInRole(String role) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_KEY_USER_ROLE, role);
        editor.apply();
    }

    public String getLoggedInUserId() {
        return sharedPreferences.getString(PREF_KEY_USER_ID, null);
    }

    public String getLoggedInRole() {
        return sharedPreferences.getString(PREF_KEY_USER_ROLE, null);
    }

    public void clearLogged() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(PREF_KEY_USER_ID);
        editor.remove(PREF_KEY_USER_ROLE);
        editor.apply();
    }
}
