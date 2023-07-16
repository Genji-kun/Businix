package com.example.businix;

public class
SignUpUtils {
    public int CheckStepOne(String name, String dob, String selectedGender, String identityCard) {
        if ((name.trim().isEmpty() || selectedGender.isEmpty() || dob.isEmpty() || identityCard.trim().isEmpty())) {
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

    public int CheckStepThree(String txtUsername, String txtPassword, String txtConfirmPassword) {
        if (txtUsername.isEmpty() || txtPassword.isEmpty() || txtConfirmPassword.isEmpty()) {
            return 1;
        }
        return 0;
    }

}
