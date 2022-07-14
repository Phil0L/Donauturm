package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.editors.OptionsEditorFragment;
import com.pl.donauturm.drinksmenu.model.Drink;

import java.util.List;

public class DrinkBottomSheet extends ItemBottomSheet {

    public DrinkBottomSheet(@NonNull FragmentActivity fragmentActivity, Drink drink, DrinkEvent eventHandler) {
        super(fragmentActivity, drink, eventHandler);
    }

    public DrinkBottomSheet(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Drink drink, DrinkEvent eventHandler) {
        super(fragmentManager, lifecycle, drink, eventHandler);
    }

    public static BottomSheetViewHolder.EDITORS[] getEditors(){
        return List.of(BottomSheetViewHolder.EDITORS.SIZE, BottomSheetViewHolder.EDITORS.POSITION, BottomSheetViewHolder.EDITORS.OPTIONS).toArray(new BottomSheetViewHolder.EDITORS[0]);
    }

    public interface DrinkEvent extends
            ItemEvent,
            OptionsEditorFragment.OnBackgroundOptionsChanged,
            OptionsEditorFragment.OnDrinkTextOptionsChanged,
            OptionsEditorFragment.OnDrinkChanged{
    }
}
