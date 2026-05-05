package com.pl.donauturm.checklist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pl.donauturm.checklist.databinding.DialogAreYouSureBinding;

public class AreYouSure extends BottomSheetDialogFragment {

    private OnYesListener listener;

    public AreYouSure() {
        // Required empty public constructor
    }

    public static AreYouSure newInstance() {
        return new AreYouSure();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DialogAreYouSureBinding binding = DialogAreYouSureBinding.inflate(inflater, container, false);
        binding.slideButton.setOnSlideCompleteListener((p) -> onYes());
        return binding.getRoot();
    }

    private void onYes() {
        if (listener != null) {
            listener.onYes();
        }
        dismiss();
    }

    public AreYouSure setOnYesListener(OnYesListener listener) {
        this.listener = listener;
        return this;
    }

    public interface OnYesListener {
        void onYes();
    }

}
