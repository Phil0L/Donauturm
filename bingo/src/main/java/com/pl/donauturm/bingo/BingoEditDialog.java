package com.pl.donauturm.bingo;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pl.donauturm.bingo.databinding.DialogBingoEditBinding;

import java.util.ArrayList;
import java.util.List;

public class BingoEditDialog extends BottomSheetDialogFragment implements TextWatcher, CompoundButton.OnCheckedChangeListener {

    private int width;
    private int height;
    private String itemString = "";
    private DialogBingoEditBinding binding;
    private OnBingoEdit listener;

    public BingoEditDialog() {
        // Required empty public constructor
    }

    public BingoEditDialog(int width, int height, String itemString) {
        this.width = width;
        this.height = height;
        this.itemString = itemString;
    }

    @NonNull
    public static BingoEditDialog newInstance(int width, int height, String itemString) {
        return new BingoEditDialog(width, height, itemString);
    }

    @NonNull
    public static BingoEditDialog newInstance(int width, int height, List<String> strings) {
        return newInstance(width, height, String.join(", ", strings) + ", ");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogBingoEditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // data
        binding.editHeigth.setText(String.valueOf(height));
        binding.editWidth.setText(String.valueOf(width));
        binding.editItems.setText(itemString);
        binding.editItems.addTextChangedListener(this);
        binding.switch1.setOnCheckedChangeListener(this);

        // name is auto focused
        binding.editItems.setFocusable(true);
        binding.editItems.setFocusableInTouchMode(true);
        binding.editItems.requestFocus();
        InputMethodManager imm = (InputMethodManager) binding.getRoot().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.editItems, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (binding.editWidth.getText() != null && !binding.editWidth.getText().toString().isEmpty() && !binding.editWidth.getText().toString().trim().equals(String.valueOf(width)) && listener != null){
            listener.onWidthChange(Integer.parseInt(binding.editWidth.getText().toString().trim()));
        }
        if (binding.editHeigth.getText() != null && !binding.editWidth.getText().toString().isEmpty() && !binding.editHeigth.getText().toString().trim().equals(String.valueOf(height)) && listener != null){
            listener.onHeightChange(Integer.parseInt(binding.editHeigth.getText().toString().trim()));
        }
        if (binding.editHeigth.getText() != null && !binding.editItems.getText().toString().trim().equals(itemString) && listener != null){
            listener.onItemChanged(binding.editItems.getText().toString().trim());
        }
        if (binding.switch1.isChecked() && listener != null && binding.editHeigth.getText().toString().startsWith(itemString)){
            String addedString = binding.editHeigth.getText().toString().substring(itemString.length()).trim();
            String[] words = addedString.replaceAll(",[, ]*", ",").split(",");
            for (String word : words){
                if (word.isEmpty() || word.equals(",")) continue;
                listener.addItem(word);
            }
            listener.flush();
        }
    }

    public BingoEditDialog setOnBingoEdit(OnBingoEdit listener) {
        this.listener = listener;
        return this;
    }

    private String textBefore;
    private boolean lock;
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        textBefore = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (lock) return;
        if (binding.switch1.isChecked() && !s.toString().startsWith(itemString)){
            lock = true;
            s.clear();
            s.clearSpans();
            s.append(textBefore);
            binding.editItems.setError("These items are fixed!");
            lock = false;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked && !binding.editItems.getText().toString().startsWith(itemString)){
            binding.editItems.getText().clear();
            binding.editItems.getText().append(itemString);
        }
    }

    public interface OnBingoEdit{
        List<String> itemCache = new ArrayList<>();
        void onHeightChange(int newHeight);
        void onWidthChange(int newWidth);
        void onItemChanged(String itemString);
        void onItemsAdded(String... item);
        default void addItem(String item){
            itemCache.add(item);
        }
        default void flush(){
            if (itemCache.isEmpty()) return;
            onItemsAdded(itemCache.toArray(new String[]{}));
            itemCache.clear();
        }
    }
}
