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
import com.pl.donauturm.drinksmenu.model.content.TextItem;

public class AddTextDialog extends DialogFragment implements TextWatcher, DialogInterface.OnClickListener {

    private OnTextSelectedListener textSelectedListener;
    private EditText editText;

    private AddTextDialog() {
    }

    public static AddTextDialog newInstance() {
        return new AddTextDialog();
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

        return new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setTitle("Enter text")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", this)
                .create();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String text = editText.getText().toString();
        if (textSelectedListener != null) {
            TextItem textItem1 = new TextItem(text);
            textItem1.createNewId();
            textSelectedListener.onTextSelected(textItem1);
        }
    }


    public interface OnTextSelectedListener {
        void onTextSelected(TextItem textItem);
    }
}
