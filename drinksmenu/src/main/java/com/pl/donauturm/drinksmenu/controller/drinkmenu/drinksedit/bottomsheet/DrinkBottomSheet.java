package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.editors.OptionsEditorFragment;
import com.pl.donauturm.drinksmenu.model.content.DrinkItem;

import java.util.List;

public class DrinkBottomSheet extends ItemBottomSheet {

    public DrinkBottomSheet(@NonNull FragmentActivity fragmentActivity, DrinkItem drinkItem, DrinkEvent eventHandler) {
        super(fragmentActivity, drinkItem, eventHandler);
    }

    public DrinkBottomSheet(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, DrinkItem drinkItem, DrinkEvent eventHandler) {
        super(fragmentManager, lifecycle, drinkItem, eventHandler);
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
