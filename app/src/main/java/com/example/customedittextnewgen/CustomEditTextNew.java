package com.example.customedittextnewgen;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.example.customedittextnewgen.validator.IValidator;
import com.example.customedittextnewgen.validator.OnCheckValidatorListener;
import com.example.customedittextnewgen.validator.ValidableView;
import com.example.customedittextnewgen.validator.ValidatorEmptyText;
import com.example.customedittextnewgen.validator.ValidatorMinLengthAlphabet;
import com.example.customedittextnewgen.validator.ValidatorMinLengthDigit;

import java.util.ArrayList;

public class CustomEditTextNew extends LinearLayout implements ValidableView {

    //-->Data Member Style
    @StyleRes
    int resIdTitleWithError = 0;
    @StyleRes
    int resIdTitleWithoutError = 0;
    @StyleRes
    int resIdError = 0;
    @StyleRes
    int resIdSubHint = 0;

    //-->Context
    private Context g_context;

    //-->Layout Object
    private TextView g_tv_title;
    private EditText g_edittext;
    private TextView g_tv_subhint;
    private TextView g_tv_error;

    //-->Layout Flag
    private boolean IS_TITLE_ADDED = false;
    private boolean IS_ERROR_ADDED = false;
    private boolean HAS_FOCUS = false;
    private boolean KEYBOARD_TYPE = true;

    private ArrayList<IValidator> g_validatorArrayList;
    private OnCheckValidatorListener onCheckValidatorListener;
    private TextWatcher textWatcher;

    //-->Layout Constants
    private final static String TAG_KEY_SUPER = "TAG_KEY_SUPER";
    private final static String TAG_KEY_PARSE = "TAG_KEY_PARSE";

    private final static boolean TAG_ALPHANUMERIC_KEYBOARD = true;
    private final static boolean TAG_NUMERIC_KEYBOARD = false;

    public CustomEditTextNew(Context context) {
        super(context);
        init(context);
    }

    public CustomEditTextNew(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void initLayout(Context context){
        g_context = context;
        View l_view = LayoutInflater.from(g_context).inflate(R.layout.layout_custom_edittext_new, this);
        g_tv_title = l_view.findViewById(R.id.tv_title);
        g_edittext = l_view.findViewById(R.id.et_main);
        g_tv_subhint = l_view.findViewById(R.id.tv_subhint);
        g_tv_error = l_view.findViewById(R.id.tv_error);
    }

    public void init(Context context){
        initLayout(context);
        g_edittext.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        g_edittext.setOnFocusChangeListener(new CustomFocus(g_edittext));
        g_edittext.addTextChangedListener(new CustomTextWatcher(g_edittext));
        g_validatorArrayList = new ArrayList<>();
    }

    public void init(Context context, AttributeSet attrs){
        init(context);
        setAttributes(attrs);
    }

    private void setAttributes(AttributeSet attrs) {
        TypedArray tmpArrStyleAttributes = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.CustomEditText, 0, 0);

        try {
            String tmpStrHint = tmpArrStyleAttributes.getString(R.styleable.CustomEditText_title);
            if (!TextUtils.isEmpty(tmpStrHint)) {
                setTitle(tmpStrHint);
            }

            String tmpStrSubHint = tmpArrStyleAttributes.getString(R.styleable.CustomEditText_subHint);
            if (!TextUtils.isEmpty(tmpStrSubHint)) {
                setSubHint(tmpStrSubHint);
            }

            String tmpStrError = tmpArrStyleAttributes.getString(R.styleable.CustomEditText_error);
            setErrorMessage(tmpStrError);

            int size = tmpArrStyleAttributes.getInteger(R.styleable.CustomEditText_size, 12);
            if (size > 0) {
                setTextSize(size);
            }

            if (tmpArrStyleAttributes.getBoolean(R.styleable.CustomEditText_singleLine, false)) {
                setSingleLine();
            }

            int keyboard = tmpArrStyleAttributes.getInteger(R.styleable.CustomEditText_keyboard, -1);
            if (keyboard != -1) {
                if (keyboard == 1) {
                    setKeyboardAlphaNumeric();
                } else if (keyboard == 2) {
                    setKeyboardAlphaNumericComaAndDot();
                } else if (keyboard == 3) {
                    setKeyboardNumeric();
                } else if (keyboard == 4) {
                    setKeyboardAlphaNumericComaSlashAndDot();
                } else if (keyboard == 5) {
                    setKeyboardNumberAndSlash();
                }
            }

            if (tmpArrStyleAttributes.getBoolean(R.styleable.CustomEditText_isPassword, false)) {
                setPasswordMode(true);
            }

            int maxLength = tmpArrStyleAttributes.getInteger(R.styleable.CustomEditText_maxLength, -1);
            if (maxLength != -1) {
                setTextLength(maxLength);
            }



            if (tmpArrStyleAttributes.getBoolean(R.styleable.CustomEditText_notEmpty, false)) {
                addValidator(new ValidatorEmptyText());
            }

            int minLengthAlphabet = tmpArrStyleAttributes.getInteger(R.styleable.CustomEditText_minLengthAlphabet, -1);
            if (minLengthAlphabet != -1) {
                setMinLengthAlphabet(minLengthAlphabet);
            }

            int minLengthDigit = tmpArrStyleAttributes.getInteger(R.styleable.CustomEditText_minLengthDigit, -1);
            if (minLengthDigit != -1) {
                setMinLengthDigit(minLengthDigit);
            }

            String message = tmpArrStyleAttributes.getString(R.styleable.CustomEditText_message);
            if (!TextUtils.isEmpty(message)) {
                setText(message);
            }

            int roundedType = tmpArrStyleAttributes.getInteger(R.styleable.CustomEditText_isRounded, -1);
            if (roundedType != -1) {
                setRoundedEdittext(roundedType);
            }

            int tmpBackgroundColor = tmpArrStyleAttributes.getResourceId(R.styleable.CustomEditText_backgroundColor, -1);
            if(tmpBackgroundColor != -1){
                this.setBackgroundColor(getResources().getColor(tmpBackgroundColor));
            }

            int textHintColor = tmpArrStyleAttributes.getResourceId(R.styleable.CustomEditText_textHintColor, -1);
            if (textHintColor != -1) {
                setEditTextHintColor(textHintColor);
            }else {
                g_edittext.setHintTextColor(tmpArrStyleAttributes.getColor(R.styleable.CustomEditText_textHintColor, Color.BLACK));
            }

            int titleAppearance = tmpArrStyleAttributes.getResourceId(R.styleable.CustomEditText_titleAppearance, -1);
            if (titleAppearance != -1) {
                setTitleAppearance(titleAppearance);
            }

            int subHintAppearance = tmpArrStyleAttributes.getResourceId(R.styleable.CustomEditText_subHintApperance, -1);
            if (subHintAppearance != -1) {
                resIdSubHint = subHintAppearance;
            }

            int errorAppearance = tmpArrStyleAttributes.getResourceId(R.styleable.CustomEditText_errorApperance, -1);
            if (errorAppearance != -1) {
                resIdError = errorAppearance;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            tmpArrStyleAttributes.recycle();
        }
    }

    public void setTitle(String p_strText) {
        g_tv_title.setText(p_strText);
        g_edittext.setHint(p_strText);
    }

    public String getTitle() {
        return g_tv_title.getText().toString();
    }

    public void setSubHint(String subhint) {
        g_tv_subhint.setText(subhint);
        setSubHintTextAppearance();
        refreshLayout();
//        g_tv_subhint.setVisibility((subhint.length() > 0) ? VISIBLE : INVISIBLE);
    }

    public void setErrorMessage(String error) {
        g_tv_error.setText(error);
        setErrorMessageAppearance();
        refreshLayout();
//        if(error.length() > 0){
//            g_tv_error.setVisibility(VISIBLE);
//            refreshLayout();
//        } else {
//            g_tv_error.setVisibility(INVISIBLE);
//            refreshLayout();
//        }
    }

    public void setTextSize(int size) {
        //cast to dp use TypedValue
        g_tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        g_edittext.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        g_tv_subhint.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (int) ((double) size * 0.8)); //:1.4
        g_tv_error.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (int) ((double) size * 0.8));   //:1.4
        this.setMinimumHeight(size * 10);//x12
    }

    public void setSingleLine() {
        g_edittext.setSingleLine();
    }

    public void setKeyboardAlphaNumeric() {
        // --> set keyboard type flag
        KEYBOARD_TYPE = TAG_ALPHANUMERIC_KEYBOARD;

        // --> accepted characters
        g_edittext.setKeyListener(new NumberKeyListener() {
            protected char[] getAcceptedChars() {
                return new char[]{
                        '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                        'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
                        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ' '};
            }

            public int getInputType() {
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
            }
        });
    }

    public void setKeyboardAlphaNumericComaAndDot() {
        // --> set keyboard type flag
        KEYBOARD_TYPE = TAG_ALPHANUMERIC_KEYBOARD;

        // --> accepted characters
        g_edittext.setKeyListener(new NumberKeyListener() {
            protected char[] getAcceptedChars() {
                return new char[]{
                        '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                        'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
                        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ' ', ',', '.'};
            }

            public int getInputType() {
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
            }
        });
    }

    public void setKeyboardAlphaNumericComaSlashAndDot() {
        // --> set keyboard type flag
        KEYBOARD_TYPE = TAG_ALPHANUMERIC_KEYBOARD;

        // --> accepted characters
        g_edittext.setKeyListener(new NumberKeyListener() {
            protected char[] getAcceptedChars() {
                return new char[]{
                        '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                        'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
                        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ' ', '/', ',', '.', '\'', '-'};
            }

            public int getInputType() {
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
            }
        });
    }

    public void setKeyboardAlphabet() {
        // --> set keyboard type flag
        KEYBOARD_TYPE = TAG_ALPHANUMERIC_KEYBOARD;

        // --> accepted characters
        g_edittext.setKeyListener(new NumberKeyListener() {
            protected char[] getAcceptedChars() {
                return new char[]{
                        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                        'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
                        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ' '};
            }

            public int getInputType() {
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
            }
        });
    }

    public void setKeyboardNumberAndSlash() {
        // --> set keyboard type flag
        KEYBOARD_TYPE = TAG_NUMERIC_KEYBOARD;

        // --> accepted characters
        g_edittext.setKeyListener(new NumberKeyListener() {

            protected char[] getAcceptedChars() {
                return new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '/'};
            }

            public int getInputType() {
                return InputType.TYPE_CLASS_NUMBER;
            }
        });
    }

    public void setKeyboardNumeric() {
        // --> set keyboard type flag
        KEYBOARD_TYPE = TAG_NUMERIC_KEYBOARD;

        // --> accepted characters
        g_edittext.setKeyListener(new NumberKeyListener() {
            protected char[] getAcceptedChars() {
                return new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
            }

            public int getInputType() {
                return InputType.TYPE_CLASS_NUMBER;
            }
        });
    }

    //TODO: CHECK THIS
    public void setPasswordMode(boolean passwordMode) {
        if(passwordMode){
            if (KEYBOARD_TYPE == TAG_ALPHANUMERIC_KEYBOARD)
                g_edittext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            else
                g_edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            g_edittext.setSelection(g_edittext.getText().length());
        } else {
            if (KEYBOARD_TYPE == TAG_ALPHANUMERIC_KEYBOARD)
                g_edittext.setInputType(InputType.TYPE_CLASS_TEXT);
            else
                g_edittext.setInputType(InputType.TYPE_CLASS_NUMBER);

            g_edittext.setSelection(g_edittext.getText().length());
        }
    }

    public void setTextLength(int textLength) {
        g_edittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(textLength)});
    }

    private void setMinLengthAlphabet(int minLength) {
        addValidator(new ValidatorMinLengthAlphabet(minLength));
    }

    private void setMinLengthDigit(int minLength) {
        addValidator(new ValidatorMinLengthDigit(minLength));
    }

    public String getText() {
        return g_edittext.getText().toString();
    }

    public void setText(String p_text) {
        //Refresh Layout
        refreshLayout();
        g_edittext.setText(p_text);
    }

    public void setEditTextHintColor(int color){
        g_edittext.setHintTextColor(getResources().getColor(color));
    }

    public void setRoundedEdittext(int type) {
        switch(type){
            case 1 :
                this.setBackground(getResources().getDrawable(R.drawable.style_background_custom_edittext));
                break;
            case 2 :
                this.setBackground(getResources().getDrawable(R.drawable.style_background_custom_edittext_top));
                break;
            case 3 :
                this.setBackground(getResources().getDrawable(R.drawable.style_background_custom_edittext_bottom));
                break;
            case 4:
                this.setBackgroundColor(Color.TRANSPARENT);
                break;
        }
    }

    private void setTitleAppearance(int id) {
        g_tv_title.setTextColor(getResources().getColorStateList(id));
    }

    public void setFilterCharacterBerita() {
        g_edittext.setKeyListener(new NumberKeyListener() {
            protected char[] getAcceptedChars() {
                return new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                        'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
                        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ' ',
                        '.', '(', ')', ',', ':', ';', '?', '!', '-', '=', '_', '<', '>'
                };
            }

            public int getInputType() {
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
            }
        });
    }

    private void setSubHintTextAppearance() {
        if (resIdSubHint != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                g_tv_subhint.setTextAppearance(resIdSubHint);
            }
        } else {
            g_tv_subhint.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        }
    }

    public void setErrorMessageAppearance() {
        if (resIdError != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                g_tv_error.setTextAppearance(resIdError);
            }
        } else {
            g_tv_error.setTextColor(Color.RED);
        }
    }

    public EditText getEditText() {
        return g_edittext;
    }

    public void onTextChangeListener(TextWatcher textWatcher) {
        this.textWatcher = textWatcher;
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        //##0. Reconstruct current saved object [Sparse Array Issue]
        IS_TITLE_ADDED = false;
        SparseArray tmpChildComponents = new SparseArray();
        for(int tmpI = 0; tmpI < this.getChildCount(); tmpI++){
            this.getChildAt(tmpI).saveHierarchyState(tmpChildComponents);
        }

        //##1. Insert it to "will be saved" bundle
        Bundle tmpBundle = new Bundle();
        tmpBundle.putParcelable(TAG_KEY_SUPER, super.onSaveInstanceState());
        tmpBundle.putSparseParcelableArray(TAG_KEY_PARSE, tmpChildComponents);
        return tmpBundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        //##0. Reconstruct New State based on saved bundle in onSaveInstanceState
        Parcelable tmpNewState = state;
        if (tmpNewState instanceof Bundle){
            SparseArray<Parcelable> childrenState = ((Bundle) tmpNewState).getSparseParcelableArray(TAG_KEY_PARSE);
            for(int tmpI = 0; tmpI < this.getChildCount(); tmpI++){
                this.getChildAt(tmpI).restoreHierarchyState(childrenState);
            }

            tmpNewState = ((Bundle) tmpNewState).getParcelable(TAG_KEY_SUPER);
        }
        super.onRestoreInstanceState(tmpNewState);

        //##1. Refresh latest state
        refreshLayout();
    }

    public void refreshLayout(){
        if(HAS_FOCUS){
            //if contains error
            if(g_tv_error.length() > 0){
//                setTextAppearance(true);
                g_tv_title.setEnabled(false);
                Log.d("WIN", "CEK ENABLED : " + String.valueOf(g_tv_title.isEnabled()));
                // if no error hasn't been added, add error
                if(!IS_ERROR_ADDED){
                    g_tv_title.setGravity(Gravity.CENTER_VERTICAL);
                    g_edittext.setGravity(Gravity.CENTER_VERTICAL);
                    g_tv_error.setVisibility(VISIBLE);
                    IS_ERROR_ADDED = true;
                }
            } else {
                g_tv_title.setEnabled(true);
                Log.d("WIN", "CEK ENABLED : " + String.valueOf(g_tv_title.isEnabled()));
//                setTextAppearance(false);
                g_tv_title.setGravity(Gravity.BOTTOM);
                g_edittext.setGravity(Gravity.TOP);

                //--> check whether there is tag view error in layout
                g_tv_error.setVisibility(GONE);
                IS_ERROR_ADDED = false;
            }

            g_edittext.setTypeface(Typeface.DEFAULT);
            g_edittext.setHint("");

            if(IS_TITLE_ADDED == false){
                g_tv_title.setVisibility(VISIBLE);
                IS_TITLE_ADDED = true;
            }

            //-->Remove subhit if there's one
            g_tv_subhint.setVisibility(GONE);

            //-->Show keyboard when focused
            g_edittext.requestFocus();
            InputMethodManager imm = (InputMethodManager) g_context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(g_edittext, InputMethodManager.SHOW_IMPLICIT);
        }
        else if(g_edittext.length() > 0 && !IS_TITLE_ADDED ){
            g_tv_title.setEnabled(true);
            Log.d("WIN", "CEK ENABLED : " + String.valueOf(g_tv_title.isEnabled()));
//            setTextAppearance(false);
            g_tv_title.setVisibility(VISIBLE);
            g_edittext.setTypeface(Typeface.DEFAULT);

            IS_TITLE_ADDED = true;
        }
        else if(g_edittext.length() <= 0 && g_tv_subhint.length() > 0){
            g_edittext.setGravity(Gravity.BOTTOM);
            g_tv_subhint.setGravity(Gravity.TOP);
            g_tv_subhint.setVisibility(VISIBLE);
        }
    }

    @Override
    public void addValidator(IValidator p_validator) {
        g_validatorArrayList.add(p_validator);
    }

    @Override
    public boolean checkValidator() {
        boolean tmpIsValid = false;
        g_tv_error.setText("");
        for (int tmpI = 0; tmpI < g_validatorArrayList.size(); tmpI++) {
            tmpIsValid = g_validatorArrayList.get(tmpI).validateText(g_edittext.getText().toString());

            if(!tmpIsValid){
                //Set ErrorText
                g_tv_error.setText(g_validatorArrayList.get(tmpI).getErrorMessage().replace("%d", getTitle()));

                //Break from process
                break;
            }
        }

        if (onCheckValidatorListener != null) {
            onCheckValidatorListener.onCheckedValidator(tmpIsValid);
        }

        //Refresh Layout whether its valid or not
        refreshLayout();

        return tmpIsValid;
    }

    private class CustomFocus implements OnFocusChangeListener{

        private EditText l_text;

        private CustomFocus(EditText p_text){
            l_text = p_text;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            HAS_FOCUS = hasFocus;
            refreshLayout();

        }
    }

    private class CustomTextWatcher implements TextWatcher {
        private EditText l_edittext;

        private CustomTextWatcher(EditText p_edittext){
            l_edittext = p_edittext;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (textWatcher != null) {
                textWatcher.beforeTextChanged(s, start, count, after);
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (textWatcher != null) {
                textWatcher.onTextChanged(s, start, before, count);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (textWatcher != null) {
                textWatcher.afterTextChanged(s);
            }
            //##1.Check Validator if it has some focus
            if(this.l_edittext.hasFocus()){
                boolean valid = checkValidator();
            }
            //##2. Force first character can't be started with space
            if(l_edittext.getText().toString().trim().equals("")){
                l_edittext.getText().clear();
            }
        }
    }
}
