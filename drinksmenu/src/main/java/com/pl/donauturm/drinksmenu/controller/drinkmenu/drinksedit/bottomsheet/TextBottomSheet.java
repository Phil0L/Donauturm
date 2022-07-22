package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.editors.OptionsEditorFragment;
import com.pl.donauturm.drinksmenu.model.content.Text;

import java.util.List;

@SuppressWarnings("unused")
public class TextBottomSheet extends ItemBottomSheet {

    public TextBottomSheet(@NonNull FragmentActivity fragmentActivity, Text item, TextEvent eventHandler) {
        super(fragmentActivity, item, eventHandler);
    }

    public TextBottomSheet(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Text item, TextEvent eventHandler) {
        super(fragmentManager, lifecycle, item, eventHandler);
    }

    public static BottomSheetViewHolder.EDITORS[] getEditors(){
        return List.of(BottomSheetViewHolder.EDITORS.SIZE, BottomSheetViewHolder.EDITORS.POSITION, BottomSheetViewHolder.EDITORS.OPTIONS).toArray(new BottomSheetViewHolder.EDITORS[0]);
    }

    public interface TextEvent extends
            ItemEvent,
            OptionsEditorFragment.OnBackgroundOptionsChanged,
            OptionsEditorFragment.OnTextOptionsChanged{
    }
}
