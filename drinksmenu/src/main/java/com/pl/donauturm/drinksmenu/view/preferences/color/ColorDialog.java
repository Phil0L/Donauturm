package com.pl.donauturm.drinksmenu.view.preferences.color;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.pl.donauturm.drinksmenu.R;
import com.rarepebble.colorpicker.ColorPickerView;

/**
 * Created by Kizito Nwose on 9/28/2016.
 */
public class ColorDialog extends DialogFragment {
    private OnColorSelectedListener colorSelectedListener;

    private ColorPreference preference;

    private ColorDialog() {
    }

    public static ColorDialog newInstance(ColorPreference preference) {
        ColorDialog dialog = new ColorDialog();
        dialog.preference = preference;
        return dialog;
    }

    public void setOnColorSelectedListener(OnColorSelectedListener colorSelectedListener) {
        this.colorSelectedListener = colorSelectedListener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnColorSelectedListener) {
            setOnColorSelectedListener((OnColorSelectedListener) context);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View rootView = layoutInflater.inflate(R.layout.dialog_select_color, null);

        int color = preference.getValue();
        final ColorPickerView colorPickerView = rootView.findViewById(R.id.colorPicker);
        colorPickerView.setColor(color);

        return new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setTitle("Choose Color")
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    preference.setColor(colorPickerView.getColor());
                    colorSelectedListener.onColorSelected(colorPickerView.getColor());
                })
                .setNegativeButton("ABBRECHEN", null)
                .create();
    }

    public interface OnColorSelectedListener {
        void onColorSelected(int newColor);
    }
}
