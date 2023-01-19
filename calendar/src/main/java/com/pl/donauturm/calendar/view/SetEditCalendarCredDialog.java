package com.pl.donauturm.calendar.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pl.donauturm.calendar.AreYouSure;
import com.pl.donauturm.calendar.CalendarCredentials;
import com.pl.donauturm.calendar.CalenderDownloader;
import com.pl.donauturm.calendar.R;
import com.pl.donauturm.calendar.databinding.DialogSetEditCalendarCredsBinding;

public class SetEditCalendarCredDialog extends BottomSheetDialogFragment {

    private DialogSetEditCalendarCredsBinding binding;
    private OnSavedListener listener;

    private static String username = "";
    private static String password = "";
    private boolean editMode = false;


    public SetEditCalendarCredDialog() {
        if (username != null && password != null) {
            editMode = true;
        }
    }

    public static SetEditCalendarCredDialog newInstance(Context context) {
        CalendarCredentials credentials = CalendarCredentials.getCredentials(context);
        SetEditCalendarCredDialog.username = (credentials != null) ? credentials.getUsername() : "";
        SetEditCalendarCredDialog.password = (credentials != null) ? credentials.getPassword() : "";
        return new SetEditCalendarCredDialog();
    }

    public SetEditCalendarCredDialog setOnSavedListener(OnSavedListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogSetEditCalendarCredsBinding.inflate(inflater, container, false);

        // Set the name, description and price if the dialog is opened for editing
        binding.editTextUsername.setText(username);
        binding.editTextPassword.setText(password);

        // button listeners
        binding.save.setOnClickListener(v -> onSave());
        binding.cancel.setOnClickListener(v -> onCancel());
        binding.testConnection.setOnClickListener(v -> onTestConnection());

        // name is auto focused
        binding.editTextUsername.setFocusable(true);
        binding.editTextUsername.setFocusableInTouchMode(true);
        binding.editTextUsername.requestFocus();
        InputMethodManager imm = (InputMethodManager) binding.getRoot().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.editTextUsername, InputMethodManager.SHOW_IMPLICIT);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    private void onSave() {
        username = binding.editTextUsername.getText().toString();
        password = binding.editTextPassword.getText().toString();
        if (editMode && username.isEmpty() && password.isEmpty()) {
            AreYouSure areYouSure = AreYouSure.newInstance();
            areYouSure.setOnYesListener(() -> {
                CalendarCredentials.deleteCredentials(binding.getRoot().getContext());
                dismiss();
            });
            areYouSure.show(getChildFragmentManager(), "AreYouSure");
            return;
        }
        if (username.trim().equals("")) {
            binding.editTextUsername.setError("Username is required");
            return;
        }
        if (password.trim().equals("")) {
            binding.editTextPassword.setError("Password is required");
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        new CalenderDownloader().tryGetAllEvents(username, password, result -> handler.post(() -> {
            if (result == null || result.isEmpty()){
                binding.testConnection.setText("Test connection" + " - " + "Connection failed");
                binding.testConnection.setTextColor(ResourcesCompat.getColor(getResources(), R.color.pink_600, null));
            } else {
                binding.testConnection.setText("Test connection" + " - " + "Connected");
                binding.testConnection.setTextColor(ResourcesCompat.getColor(getResources(), R.color.teal_900, null));
                if (listener != null)
                    listener.onSaved(username, password);
                dismiss();
            }
        }));

    }

    private void onCancel() {
        username = "";
        password = "";
        dismiss();
    }

    @SuppressLint("SetTextI18n")
    private void onTestConnection() {
        username = binding.editTextUsername.getText().toString();
        password = binding.editTextPassword.getText().toString();
        if (username.trim().equals("")) {
            binding.editTextUsername.setError("Username is required");
            return;
        }
        if (password.trim().equals("")) {
            binding.editTextPassword.setError("Password is required");
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        new CalenderDownloader().tryGetAllEvents(username, password, result -> handler.post(() -> {
            if (result == null || result.isEmpty()){
                binding.testConnection.setText("Test connection" + " - " + "Connection failed");
                binding.testConnection.setTextColor(ResourcesCompat.getColor(getResources(), R.color.pink_600, null));
            } else {
                binding.testConnection.setText("Test connection" + " - " + "Connected");
                binding.testConnection.setTextColor(ResourcesCompat.getColor(getResources(), R.color.teal_900, null));
            }
        }));
    }

    public interface OnSavedListener {
        void onSaved(String username, String password);
    }

}
