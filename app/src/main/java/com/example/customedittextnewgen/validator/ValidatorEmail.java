package com.example.customedittextnewgen.validator;

public class ValidatorEmail implements IValidator  {
    @Override
    public boolean validateText(String p_strText) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(p_strText).matches();
    }

    @Override
    public String getErrorMessage() {
        return "%d tidak valid";
    }
}
