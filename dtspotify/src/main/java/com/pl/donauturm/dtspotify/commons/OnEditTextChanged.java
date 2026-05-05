package com.pl.donauturm.dtspotify.commons;

import android.text.TextWatcher;

public interface OnEditTextChanged extends TextWatcher {

    @Override
    default void beforeTextChanged(CharSequence s, int start, int count, int after){

    }

    @Override
    default void onTextChanged(CharSequence s, int start, int before, int count){

    }
}
