package com.axon.webrtc.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatEditText;

import timber.log.Timber;


public class KeyBoardHelper {

    Activity activity;

    public KeyBoardHelper(Activity activity) {
        this.activity = activity;
    }

    public void closeKeyBoard() {
        InputMethodManager imm = null;

        try {
            imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            if (view == null) {
                view = new View(activity);
            }
            if (imm == null) {
                return;
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void focusRequest(AppCompatEditText editText) {
        try {
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null) {
                return;
            }
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void focusRequest(EditText editText) {
        try {
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null) {
                return;
            }
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public View.OnFocusChangeListener hideKeyboardTouchOutSide() {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if (!focus) {
                    closeKeyBoard();
                }
            }
        };
    }
}
