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

public class NoConnectionInfo extends BubblePopup {

    private final String message;
    private final BubbleActionCallback<?> onRetry;

    public NoConnectionInfo(Activity context) {
        super(context);
        this.message = "Network connection failed. \nPlease check your internet connection.";
        this.onRetry = null;
    }

    public NoConnectionInfo(Activity context, BubbleActionCallback<?> onRetry) {
        super(context);
        this.message = "Network connection failed. \nPlease check your internet connection.";
        this.onRetry = onRetry;
    }

    public NoConnectionInfo(Activity context, String message) {
        super(context);
        this.message = message;
        this.onRetry = null;
    }

    public NoConnectionInfo(Activity context, String message, BubbleActionCallback<?> onRetry) {
        super(context);
        this.message = message;
        this.onRetry = onRetry;
    }

    @Override
    protected BubbleLayout onCreateView(@NonNull View anchor, @NonNull LayoutInflater inflater, @Nullable BubbleLayout cachedView) {
        BubbleLayout bubbleLayout = (BubbleLayout) inflater.inflate(R.layout.popup_no_connection_info, (ViewGroup) anchor.getRootView(), false);
        TextView textView = bubbleLayout.findViewById(R.id.retry);
        textView.setOnClickListener(this::retry);
        TextView messageView = bubbleLayout.findViewById(R.id.text);
        messageView.setText(message);
        return bubbleLayout;
    }

    private void retry(View v) {
        if (onRetry != null) {
            onRetry.onClicked(null);
            dismiss();
        }
    }
}
