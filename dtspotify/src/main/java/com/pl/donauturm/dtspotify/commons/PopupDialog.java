package com.pl.donauturm.dtspotify.commons;

import android.app.AlertDialog;
import android.content.Context;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

public class PopupDialog extends AlertDialog {

    private OnDispatchTouchEvent callback;

    public PopupDialog(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        boolean supr = super.dispatchTouchEvent(ev);
        // set this flag to tell its from a dialog
        ev.setEdgeFlags(ev.getEdgeFlags() | 0b00010000);
        boolean self = this.callback.dispatchTouchEvent(ev);
        return self || supr;
    }

    public void onDispatchTouchEvent(OnDispatchTouchEvent callback){
        this.callback = callback;
    }

    public interface OnDispatchTouchEvent{
        boolean dispatchTouchEvent(@NonNull MotionEvent ev);
    }

}
