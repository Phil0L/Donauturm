package com.pl.donauturm.drinksmenu.view.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pl.donauturm.drinksmenu.databinding.DialogEditNameBinding;

public class EditNameDialog extends BottomSheetDialogFragment {

    private DialogEditNameBinding binding;
    private OnRenamedListener listener;

    private String name;

    public EditNameDialog() {
        // Required empty public constructor
    }

    public static EditNameDialog newInstance(String name) {
        EditNameDialog end = new EditNameDialog();
        end.name = name;
        return end;
    }

    public void setListener(OnRenamedListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogEditNameBinding.inflate(inflater, container, false);

        // Set the name for editing
        binding.editText.setText(name);
        binding.editText.setHint(name);
        binding.editText.setSelectAllOnFocus(true);

        // button listeners
        binding.save.setOnClickListener(v -> onSave());
        binding.cancel.setOnClickListener(v -> onCancel());

        // name is auto focused
        binding.editText.setFocusable(true);
        binding.editText.setFocusableInTouchMode(true);
        binding.editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) binding.getRoot().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.editText, InputMethodManager.SHOW_IMPLICIT);

        return binding.getRoot();
    }

    private void onSave() {
        name = binding.editText.getText().toString();
        if (name.isEmpty()) {
            binding.editText.setError("Name cannot be empty");
            return;
        }
        listener.onRenamed(name);
        dismiss();
    }

    private void onCancel() {
        dismiss();
    }

    public interface OnRenamedListener {
        void onRenamed(String name);
    }

}
