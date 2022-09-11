package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.editors.OptionsEditorFragment;
import com.pl.donauturm.drinksmenu.model.content.DrinksMenuItem;
import com.pl.donauturm.drinksmenu.model.content.ShapeItem;

import java.util.List;

@SuppressWarnings("unused")
public class ShapeBottomSheet extends ItemBottomSheet{


    public ShapeBottomSheet(@NonNull FragmentActivity fragmentActivity, DrinksMenuItem item, ItemEvent eventHandler) {
        super(fragmentActivity, item, eventHandler);
    }

    public ShapeBottomSheet(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, ShapeItem item, ShapeEvent eventHandler) {
        super(fragmentManager, lifecycle, item, eventHandler);
    }

    public static BottomSheetViewHolder.EDITORS[] getEditors(){
        return List.of(BottomSheetViewHolder.EDITORS.SIZE, BottomSheetViewHolder.EDITORS.POSITION, BottomSheetViewHolder.EDITORS.OPTIONS).toArray(new BottomSheetViewHolder.EDITORS[0]);
    }

    public interface ShapeEvent extends
            ItemEvent,
            OptionsEditorFragment.OnBackgroundOptionsChanged{
    }
}
