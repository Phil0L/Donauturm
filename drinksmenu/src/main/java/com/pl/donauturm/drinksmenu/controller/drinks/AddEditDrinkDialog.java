package com.pl.donauturm.drinksmenu.controller.drinks;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pl.donauturm.drinksmenu.databinding.DialogAddEditDrinkBinding;

import java.math.BigDecimal;

public class AddEditDrinkDialog extends BottomSheetDialogFragment {

    private DialogAddEditDrinkBinding binding;
    private OnSavedListener listener;

    private static String name = "";
    private static String description = "";
    private static String price = "";


    public AddEditDrinkDialog() {
        // Required empty public constructor
    }

    public static AddEditDrinkDialog newInstance(String name, String description, BigDecimal price) {
        AddEditDrinkDialog fragment = new AddEditDrinkDialog();
        AddEditDrinkDialog.name = name;
        AddEditDrinkDialog.description = description;
        AddEditDrinkDialog.price = price.toString();
        return fragment;
    }

    public static AddEditDrinkDialog newInstance() {
        return new AddEditDrinkDialog();
    }

    public void setOnSavedListener(OnSavedListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddEditDrinkBinding.inflate(inflater, container, false);

        // Set the name, description and price if the dialog is opened for editing
        binding.editTexts.drinkName.setText(name);
        binding.editTexts.drinkDescription.setText(description);
        binding.editTexts.drinkPrice.setText(price);

        // button listeners
        binding.save.setOnClickListener(v -> onSave());
        binding.cancel.setOnClickListener(v -> onCancel());

        // name is auto focused
        binding.editTexts.drinkName.setFocusable(true);
        binding.editTexts.drinkName.setFocusableInTouchMode(true);
        binding.editTexts.drinkName.requestFocus();
        InputMethodManager imm = (InputMethodManager) binding.getRoot().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.editTexts.drinkName, InputMethodManager.SHOW_IMPLICIT);
        return binding.getRoot();
    }

    private void onSave() {
        name = binding.editTexts.drinkName.getText().toString();
        description = binding.editTexts.drinkDescription.getText().toString();
        price = binding.editTexts.drinkPrice.getText().toString();
        if (name.trim().equals("")){
            binding.editTexts.drinkName.setError("Name is required");
            return;
        }
        if (price.trim().equals("")){
            price = "0";
        }
        if (!price.matches("^[0-9.]+$")){
            binding.editTexts.drinkPrice.setError("Price must be a number");
            return;
        }
        if (!price.matches("^[0-9]+(\\.[0-9]{1,2})?$")){
            binding.editTexts.drinkPrice.setError("Price must be a number with max 2 decimal places");
            return;
        }
        if (listener != null)
            listener.onSaved(name, description, new BigDecimal(price));
        name = "";
        description = "";
        price = "";
        dismiss();
    }

    private void onCancel() {
        name = "";
        description = "";
        price = "";
        dismiss();
    }

    public interface OnSavedListener {
        void onSaved(String name, String description, BigDecimal price);
    }

}
