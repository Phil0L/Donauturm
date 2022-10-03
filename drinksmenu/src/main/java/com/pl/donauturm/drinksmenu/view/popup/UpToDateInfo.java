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

public class UpToDateInfo extends BubblePopup {

    private final String message;

    public UpToDateInfo(Activity context) {
        super(context);
        this.message = "Drinks menu is in sync with the server.";
    }

    public UpToDateInfo(Activity context, String message) {
        super(context);
        this.message = message;
    }

    @Override
    protected BubbleLayout onCreateView(@NonNull View anchor, @NonNull LayoutInflater inflater, @Nullable BubbleLayout cachedView) {
        BubbleLayout bubbleLayout = (BubbleLayout) inflater.inflate(R.layout.popup_up_to_date_info, (ViewGroup) anchor.getRootView(), false);
        TextView textView = bubbleLayout.findViewById(R.id.force_upload);
        textView.setOnClickListener(this::forceUpload);
        TextView messageView = bubbleLayout.findViewById(R.id.text);
        messageView.setText(message);
        return bubbleLayout;
    }

    private void forceUpload(View v) {
        //TODO: upload the drinks menu
    }
}
