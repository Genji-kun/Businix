package com.example.businix.utils;

import com.example.businix.models.Employee;

public interface AuthenticationListener {
    void onUsernameNotFound();
    void onPasswordIncorrect();
    void onAuthenticationSuccess(Employee employee);
    void onUserPending();
    void onUserInactive();
}
