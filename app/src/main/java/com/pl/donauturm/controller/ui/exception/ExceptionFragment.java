package com.pl.donauturm.controller.ui.exception;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pl.donauturm.databinding.FragmentExceptionBinding;
import com.pl.donauturm.util.ExceptionHandler;

public class ExceptionFragment extends Fragment {

    private FragmentExceptionBinding binding;
    private final ExceptionHandler error;

    public static ExceptionFragment newInstance(ExceptionHandler error) {
        return new ExceptionFragment(error);
    }

    private ExceptionFragment(ExceptionHandler error) {

        this.error = error;
    }

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExceptionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.errorMessage.setText(error.getLog());
        binding.buttonRight.setText("Clear");
        binding.buttonRight.setOnClickListener(v -> {
            error.resetLog();
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        binding.buttonLeft.setText("Close");
        binding.buttonLeft.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
    }
}