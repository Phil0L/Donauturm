package com.pl.donauturm.checklist;

import android.text.Editable;
import android.text.TextWatcher;

public class AbstractTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public interface Wrapper extends TextWatcher {

        void onTextChanged(String text);

        @Override
        default void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        default void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        default void afterTextChanged(Editable s) {
            onTextChanged(s.toString());
        }
    }
}
