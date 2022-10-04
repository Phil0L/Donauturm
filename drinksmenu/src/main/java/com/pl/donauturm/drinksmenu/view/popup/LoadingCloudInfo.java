package com.pl.donauturm.drinksmenu.view.popup;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.util.popupbubble.BubbleLayout;
import com.pl.donauturm.drinksmenu.util.popupbubble.BubblePopup;

public class LoadingCloudInfo extends BubblePopup {

    private final String message;
    private final BubbleActionCallback<?> onCancel;

    public LoadingCloudInfo(Activity context) {
        super(context);
        this.message = "Currently updating the drinks menu.";
        this.onCancel = null;
    }

    public LoadingCloudInfo(Activity context, BubbleActionCallback<?> onCancel) {
        super(context);
        this.message = "Currently updating the drinks menu.";
        this.onCancel = onCancel;
    }

    public LoadingCloudInfo(Activity context, String message) {
        super(context);
        this.message = message;
        this.onCancel = null;
    }

    public LoadingCloudInfo(Activity context, String message, BubbleActionCallback<?> onCancel) {
        super(context);
        this.message = message;
        this.onCancel = onCancel;
    }

    @Override
    protected BubbleLayout onCreateView(@NonNull View anchor, @NonNull LayoutInflater inflater, @Nullable BubbleLayout cachedView) {
        BubbleLayout bubbleLayout = (BubbleLayout) inflater.inflate(R.layout.popup_pulling_info, (ViewGroup) anchor.getRootView(), false);
        TextView textView = bubbleLayout.findViewById(R.id.cancel);
        textView.setOnClickListener(this::cancel);
        TextView messageView = bubbleLayout.findViewById(R.id.text);
        messageView.setText(message);
        return bubbleLayout;
    }

    private void cancel(View v) {
        if (onCancel != null) {
            onCancel.onClicked(null);
            dismiss();
        }
    }
}
