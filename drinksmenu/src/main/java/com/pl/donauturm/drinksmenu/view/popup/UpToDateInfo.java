package com.pl.donauturm.drinksmenu.view.popup;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.util.popupbubble.BubbleLayout;
import com.pl.donauturm.drinksmenu.util.popupbubble.BubblePopup;
import com.pl.donauturm.drinksmenu.view.views.TextView;

public class UpToDateInfo extends BubblePopup {

    public UpToDateInfo(Activity context) {
        super(context);
    }

    @Override
    protected BubbleLayout onCreateView(@NonNull View anchor, @NonNull LayoutInflater inflater, @Nullable BubbleLayout cachedView) {
        BubbleLayout bubbleLayout = (BubbleLayout) inflater.inflate(R.layout.popup_up_to_date_info, (ViewGroup) anchor.getRootView(), false);
        TextView textView = bubbleLayout.findViewById(R.id.force_upload);
        textView.setOnClickListener(this::forceUpload);
        return bubbleLayout;
    }

    private void forceUpload(View v) {
        //TODO: upload the drinks menu
    }
}
