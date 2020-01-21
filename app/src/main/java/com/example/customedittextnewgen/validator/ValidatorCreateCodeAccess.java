package com.example.customedittextnewgen.validator;


import com.example.customedittextnewgen.utilities.Utilities;

public class ValidatorCreateCodeAccess implements IValidator {
    @Override
    public boolean validateText(String p_strText) {
        return Utilities.checkContainLetterAndNumber(p_strText) && p_strText.length() == 6;
    }

    @Override
    public String getErrorMessage() {
        return "%d harus 6 karakter huruf dan angka";
    }
}
