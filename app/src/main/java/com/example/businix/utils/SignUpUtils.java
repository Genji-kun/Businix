package com.example.businix.utils;

import com.example.businix.models.Gender;

public class SignUpUtils {
    public int CheckStepOne(String name, String dob, String identityCard) {
        if ((name.trim().isEmpty() || dob.isEmpty() || identityCard.trim().isEmpty())) {
            return 1;
        }
        return 0;
    }

    public int CheckStepTwo(String txtPhone, String txtEmail) {
        if (txtPhone.isEmpty() || txtEmail.isEmpty() || !(txtPhone.length() >= 9 && txtPhone.length() <= 10) ) {
            return 1;
        }
        return 0;
    }


}
