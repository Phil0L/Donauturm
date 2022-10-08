package com.pl.donauturm.drinksmenu.view.dialogs;

import static com.pl.donauturm.pisignageapi.requests.SessionRequest.HTTP_ERROR;
import static com.pl.donauturm.pisignageapi.requests.SessionRequest.LOGIN_SUCCESS;
import static com.pl.donauturm.pisignageapi.requests.SessionRequest.NO_CONNECTION;
import static com.pl.donauturm.pisignageapi.requests.SessionRequest.WRONG_CREDENTIALS;

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
import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.controller.util.AreYouSure;
import com.pl.donauturm.drinksmenu.controller.util.api.DMCredentials;
import com.pl.donauturm.drinksmenu.controller.util.api.DrinksMenuAPI;
import com.pl.donauturm.drinksmenu.databinding.DialogSetEditDmCredsBinding;

public class SetEditDMCDialog extends BottomSheetDialogFragment {

    private DialogSetEditDmCredsBinding binding;
    private OnSavedListener listener;

    private static String username = "";
    private static String password = "";
    private boolean editMode = false;


    public SetEditDMCDialog() {
        if (username != null && password != null) {
            editMode = true;
        }
    }

    public static SetEditDMCDialog newInstance(Context context) {
        DMCredentials credentials = DMCredentials.getCredentials(context);
        SetEditDMCDialog.username = (credentials != null) ? credentials.getUsername() : "";
        SetEditDMCDialog.password = (credentials != null) ? credentials.getPassword() : "";
        return new SetEditDMCDialog();
    }

    public SetEditDMCDialog setOnSavedListener(OnSavedListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogSetEditDmCredsBinding.inflate(inflater, container, false);

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
                DMCredentials.deleteCredentials(binding.getRoot().getContext());
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
        DrinksMenuAPI api = DrinksMenuAPI.simple(username, password, binding.getRoot().getContext());
        Handler handler = new Handler(Looper.getMainLooper());
        api.asynchronous.testConnection(result -> handler.post(() -> {
            switch (result) {
                case LOGIN_SUCCESS:
                    binding.testConnection.setText("Test connection" + " - " + "Connected");
                    binding.testConnection.setTextColor(ResourcesCompat.getColor(getResources(), R.color.teal_900, null));
                    if (listener != null)
                        listener.onSaved(username, password);
                    dismiss();
                    break;
                case HTTP_ERROR:
                case NO_CONNECTION:
                    binding.testConnection.setText("Test connection" + " - " + "Connection failed");
                    binding.testConnection.setTextColor(ResourcesCompat.getColor(getResources(), R.color.pink_600, null));
                    break;
                case WRONG_CREDENTIALS:
                    binding.testConnection.setText("Test connection" + " - " + "Wrong credentials");
                    binding.testConnection.setTextColor(ResourcesCompat.getColor(getResources(), R.color.pink_600, null));
                    break;
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
        DrinksMenuAPI api = DrinksMenuAPI.simple(username, password, binding.getRoot().getContext());
        Handler handler = new Handler(Looper.getMainLooper());
        api.asynchronous.testConnection(result -> handler.post(() -> {
            switch (result) {
                case LOGIN_SUCCESS:
                    binding.testConnection.setText("Test connection" + " - " + "Connected");
                    binding.testConnection.setTextColor(ResourcesCompat.getColor(getResources(), R.color.teal_900, null));
                    break;
                case HTTP_ERROR:
                case NO_CONNECTION:
                    binding.testConnection.setText("Test connection" + " - " + "Connection failed");
                    binding.testConnection.setTextColor(ResourcesCompat.getColor(getResources(), R.color.pink_600, null));
                    break;
                case WRONG_CREDENTIALS:
                    binding.testConnection.setText("Test connection" + " - " + "Wrong credentials");
                    binding.testConnection.setTextColor(ResourcesCompat.getColor(getResources(), R.color.pink_600, null));
                    break;
            }
        }));

    }

    public interface OnSavedListener {
        void onSaved(String username, String password);
    }

}
