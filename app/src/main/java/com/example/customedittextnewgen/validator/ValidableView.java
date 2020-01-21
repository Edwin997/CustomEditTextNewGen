package com.example.customedittextnewgen.validator;

public interface ValidableView {
    void addValidator(IValidator p_validator);
    boolean checkValidator();
}
