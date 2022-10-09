package com.pl.donauturm.drinksmenu.view.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pl.donauturm.drinksmenu.databinding.DialogDmMergeConflictBinding;
import com.pl.donauturm.drinksmenu.model.DrinksMenu;

public class DrinksmenuMergeConflictDialog extends BottomSheetDialogFragment {

    private OnDecidedListener listener;

    private DrinksMenu mine;
    private DrinksMenu theirs;

    public static final int KEEP_MINE = 0;
    public static final int KEEP_THEIRS = 1;


    public DrinksmenuMergeConflictDialog() {
        // Required empty public constructor
    }

    public static DrinksmenuMergeConflictDialog newInstance(DrinksMenu mine, DrinksMenu theirs) {
        DrinksmenuMergeConflictDialog dmcd = new DrinksmenuMergeConflictDialog();
        dmcd.mine = mine;
        dmcd.theirs = theirs;
        return dmcd;
    }

    public void setListener(OnDecidedListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DialogDmMergeConflictBinding binding = DialogDmMergeConflictBinding.inflate(inflater, container, false);

        // Set the name, description and price if the dialog is opened for editing
        binding.text.setText(String.format(binding.text.getText().toString(), mine.getName()));

        // button listeners
        binding.theirs.setOnClickListener(v -> onTheirs());
        binding.mine.setOnClickListener(v -> onMine());

        return binding.getRoot();
    }

    private void onTheirs() {
        listener.onDecided(KEEP_THEIRS, theirs);
        dismiss();
    }

    private void onMine() {
        listener.onDecided(KEEP_MINE, mine);
        dismiss();
    }

    public interface OnDecidedListener {
        void onDecided(int which, DrinksMenu drinksMenu);
    }

}
