package com.pl.donauturm.checklist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pl.donauturm.checklist.databinding.DialogAreYouSureBinding;

public class AreYouSureWithData<D> extends AreYouSure {

    private OnYesListener<D> listener;
    private D data;

    public AreYouSureWithData(D data) {
        // Required empty public constructor
        this.data = data;
    }

    public static <D> AreYouSureWithData<D> newInstance(D data) {
        return new AreYouSureWithData<>(data);
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
            listener.onYes(data);
        }
        dismiss();
    }

    public AreYouSureWithData<D> setOnYesListener(OnYesListener<D> listener) {
        this.listener = listener;
        return this;
    }

    public interface OnYesListener<D> {
        void onYes(D data);
    }

}
