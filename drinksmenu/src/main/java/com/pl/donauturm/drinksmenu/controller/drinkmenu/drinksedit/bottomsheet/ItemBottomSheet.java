package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.editors.OptionsEditorFragment;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.editors.PositionEditorFragment;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.editors.SizeEditorFragment;
import com.pl.donauturm.drinksmenu.model.content.DrinksMenuItem;

import java.util.ArrayList;
import java.util.List;

public class ItemBottomSheet extends FragmentStateAdapter {

    public static final int SIZE_EDITOR = 1;
    public static final int POSITION_EDITOR = 2;
    public static final int OPTIONS_EDITOR = 3;

    protected final DrinksMenuItem item;
    protected final ItemEvent eventHandler;
    protected final List<Fragment> editors;

    public ItemBottomSheet(@NonNull FragmentActivity fragmentActivity, DrinksMenuItem item, ItemEvent eventHandler) {
        super(fragmentActivity);
        this.item = item;
        this.eventHandler = eventHandler;
        this.editors = new ArrayList<>();
    }

    public ItemBottomSheet(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, DrinksMenuItem item, ItemEvent eventHandler) {
        super(fragmentManager, lifecycle);
        this.item = item;
        this.eventHandler = eventHandler;
        this.editors = new ArrayList<>();
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
            default: break;
        }
        return new Fragment();
    }

    @Override
    public int getItemCount() {
        return List.of(SIZE_EDITOR, POSITION_EDITOR, OPTIONS_EDITOR).size();
    }

    public static BottomSheetViewHolder.EDITORS[] getEditors(){
        return List.of(BottomSheetViewHolder.EDITORS.SIZE, BottomSheetViewHolder.EDITORS.POSITION, BottomSheetViewHolder.EDITORS.OPTIONS).toArray(new BottomSheetViewHolder.EDITORS[0]);
    }

    public void notifyDataChanged(DrinksMenuItem item){
        editors.forEach(editor -> {
            if (editor instanceof EditorUpdate)
                ((EditorUpdate) editor).onDataChanged(item);
        });
    }

    public interface ItemEvent extends
            SizeEditorFragment.OnResize,
            PositionEditorFragment.OnReposition,
            OptionsEditorFragment.OnOptionsChanged {
    }

    public interface EditorUpdate{
        void onDataChanged(DrinksMenuItem item);
    }
}
