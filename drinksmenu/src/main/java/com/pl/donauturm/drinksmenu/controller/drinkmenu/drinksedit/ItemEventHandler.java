package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.BottomSheetViewHolder;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.ItemBottomSheet;
import com.pl.donauturm.drinksmenu.model.Item;
import com.pl.donauturm.drinksmenu.view.views.ItemView;

public class ItemEventHandler implements ItemBottomSheet.ItemEvent {

    protected final EditorProvider provider;
    private boolean hasChanged;

    public ItemEventHandler(EditorProvider provider) {
        this.provider = provider;
    }

    @Override
    public void onResize(float width, float height, @Nullable ItemView itemView) {
        onAnyChange();
        if (itemView != null && provider.getSelectedView() == null)
            provider.generateImage();
        if (itemView == null) itemView = provider.getSelectedView();
        if (itemView == null) return;
        Item item = itemView.item;
        if (item == null) return;
        item.setWidth(width);
        item.setHeight(height);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.height = DrinksMenuEditorActivity.ValueScale.with(itemView.getContext()).scalePositionToView((int) height);
        layoutParams.width = DrinksMenuEditorActivity.ValueScale.with(itemView.getContext()).scalePositionToView((int) width);
        itemView.setLayoutParams(layoutParams);
        if (provider.getBottomSheet() != null)
            provider.getBottomSheet().update();

    }

    @Override
    public void onSizeLock(boolean nowLocked) {
        ItemView itemView = provider.getSelectedView();
        if (itemView == null) return;
        Item item = itemView.item;
        if (item == null) return;
        item.setSizeLocked(nowLocked);
        if (nowLocked){
            itemView.deactivateResize();
        }else{
            itemView.activateResize();
        }
        itemView.notifyChanged();
    }

    @Override
    public void onReposition(float left, float top, @Nullable ItemView itemView) {
        onAnyChange();
        if (itemView != null && provider.getSelectedView() == null)
            provider.generateImage();
        if (itemView == null) itemView = provider.getSelectedView();
        Item item = itemView.item;
        if (item == null) return;
        item.setLeft(left);
        item.setTop(top);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) itemView.getLayoutParams();
        layoutParams.topMargin = DrinksMenuEditorActivity.ValueScale.with(itemView.getContext()).scalePositionToView((int) top);
        layoutParams.leftMargin = DrinksMenuEditorActivity.ValueScale.with(itemView.getContext()).scalePositionToView((int) left);
        itemView.setLayoutParams(layoutParams);
        if (provider.getBottomSheet() != null)
            provider.getBottomSheet().update();
    }

    @Override
    public void onPositionLock(boolean nowLocked) {
        ItemView itemView = provider.getSelectedView();
        if (itemView == null) return;
        Item item = itemView.item;
        if (item == null) return;
        item.setPositionLocked(nowLocked);
        if (nowLocked){
            itemView.deactivateDrag();
        }else{
            itemView.activateDrag();
        }
        itemView.notifyChanged();
    }


    @Override
    public void onNameChanged(String name) {
        onAnyChange();
        Item item;
        if ((item = provider.getCurrentItem()) != null)
            item.setName(name);
        if (provider.getBottomSheet() != null)
            provider.getBottomSheet().update();
        if (provider.getSelectedView() != null)
            provider.getSelectedView().notifyChanged();
    }

    public void onDelete(){
        onAnyChange();
        if (provider != null)
            provider.generateImage();
    }

    public void onAdd(){
        onAnyChange();
        if (provider != null)
            provider.generateImage();
    }

    protected void onAnyChange(){
        hasChanged = true;
    }

    public boolean hasChanged(){
        return hasChanged;
    }

    public void resetChanged(){
        hasChanged = false;
    }

    public interface EditorProvider{
        Item getCurrentItem();
        ItemView getSelectedView();
        BottomSheetViewHolder getBottomSheet();
        void generateImage();
    }

}
