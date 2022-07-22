package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.editors.ItemsEditorFragment;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.editors.OptionsEditorFragment;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.editors.PositionEditorFragment;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.editors.SizeEditorFragment;
import com.pl.donauturm.drinksmenu.model.content.DrinkGroup;

import java.util.List;

public class DrinkGroupBottomSheet extends ItemBottomSheet {

    public static final int ITEM_EDITOR = 4;

    public DrinkGroupBottomSheet(@NonNull FragmentActivity fragmentActivity, DrinkGroup drinkGroup, DrinkGroupEvent eventHandler) {
        super(fragmentActivity, drinkGroup, eventHandler);
    }

    public DrinkGroupBottomSheet(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, DrinkGroup drinkGroup, DrinkGroupEvent eventHandler) {
        super(fragmentManager, lifecycle, drinkGroup, eventHandler);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position+1){
            case SIZE_EDITOR:
                Fragment fragment = SizeEditorFragment.newInstance(item, eventHandler);
                editors.add(fragment);
                return fragment;
            case POSITION_EDITOR:
                fragment = PositionEditorFragment.newInstance(item, eventHandler);
                editors.add(fragment);
                return fragment;
            case OPTIONS_EDITOR:
                fragment = OptionsEditorFragment.newInstance(item, eventHandler);
                editors.add(fragment);
                return fragment;
            case ITEM_EDITOR:
                fragment = ItemsEditorFragment.newInstance((DrinkGroup) item, (DrinkGroupEvent) eventHandler);
                editors.add(fragment);
                return fragment;
            default: break;
        }
        return new Fragment();
    }

    @Override
    public int getItemCount() {
        return List.of(SIZE_EDITOR, POSITION_EDITOR, OPTIONS_EDITOR, ITEM_EDITOR).size();
    }

    public static BottomSheetViewHolder.EDITORS[] getEditors(){
        return List.of(BottomSheetViewHolder.EDITORS.SIZE, BottomSheetViewHolder.EDITORS.POSITION, BottomSheetViewHolder.EDITORS.OPTIONS, BottomSheetViewHolder.EDITORS.ITEMS).toArray(new BottomSheetViewHolder.EDITORS[0]);
    }

    public interface DrinkGroupEvent extends
            ItemEvent,
            OptionsEditorFragment.OnBackgroundOptionsChanged,
            OptionsEditorFragment.OnDrinkTextOptionsChanged,
            OptionsEditorFragment.OnGridOptionsChanged,
            ItemsEditorFragment.OnItemsChanged {
    }
}
