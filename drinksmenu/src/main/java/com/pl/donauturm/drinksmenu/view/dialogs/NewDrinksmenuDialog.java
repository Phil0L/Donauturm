package com.pl.donauturm.drinksmenu.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.pl.donauturm.drinksmenu.R;

public class NewDrinksmenuDialog extends DialogFragment implements TextWatcher, DialogInterface.OnClickListener {

    private OnTextSelectedListener textSelectedListener;
    private EditText editText;

    private NewDrinksmenuDialog() {
    }

    public static NewDrinksmenuDialog newInstance() {
        return new NewDrinksmenuDialog();
    }

    public void setOnTextSelectedListener(OnTextSelectedListener textSelectedListener) {
        this.textSelectedListener = textSelectedListener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnTextSelectedListener) {
            setOnTextSelectedListener((OnTextSelectedListener) context);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View rootView = layoutInflater.inflate(R.layout.dialog_enter_text, null);

        editText = rootView.findViewById(R.id.edit_text);
        editText.addTextChangedListener(this);
        editText.setHint("Enter name");
        editText.requestFocus();

        return new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setTitle("New Drinks menu")
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Ok", this)
                .create();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().trim().isEmpty()) {
            editText.setError("Text is empty");
        }
        if (s.toString().matches("[^a-zA-Z0-9 _-]+")) {
            editText.setError("Text contains forbidden characters");
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String text = editText.getText().toString();
        if (text.trim().isEmpty()) {
            editText.setError("Text is empty");
            return;
        }
        if (text.matches("[^a-zA-Z0-9 _-]+")) {
            editText.setError("Text contains forbidden characters");
            return;
        }
        if (textSelectedListener != null)
            textSelectedListener.onTextSelected(text);
    }


    public interface OnTextSelectedListener {
        void onTextSelected(String text);
    }
}
