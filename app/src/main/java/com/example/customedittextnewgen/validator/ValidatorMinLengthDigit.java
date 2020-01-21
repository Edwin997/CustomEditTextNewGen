package com.example.customedittextnewgen.validator;

import android.content.Context;


/**
 * Created by u063490 on 4/23/2018.
 */

public class ValidatorMinLengthDigit implements IValidator {
    int minLength = 0;
    public ValidatorMinLengthDigit(int minLength) {
        this.minLength = minLength;
    }

    @Override
    public boolean validateText(String p_strText) {
        if(p_strText.length() != minLength)
            return false;
        return true;
    }

    @Override
    public String getErrorMessage() {
        return "%d harus "+minLength+" digit";
    }
}
