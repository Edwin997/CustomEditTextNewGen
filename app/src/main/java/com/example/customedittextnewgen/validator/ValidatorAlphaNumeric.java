package com.example.customedittextnewgen.validator;


import com.example.customedittextnewgen.utilities.Utilities;

public class ValidatorAlphaNumeric implements IValidator {
    @Override
    public boolean validateText(String p_strText) {
        return Utilities.checkContainLetterAndNumber(p_strText);
    }

    @Override
    public String getErrorMessage() {
        return "Format %d tidak sesuai";
    }
}
