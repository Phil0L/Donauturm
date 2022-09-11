package com.pl.donauturm.drinksmenu.view.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.editors.PositionEditorFragment;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.editors.SizeEditorFragment;
import com.pl.donauturm.drinksmenu.model.content.DrinksMenuItem;
import com.pl.donauturm.drinksmenu.model.interfaces.Backgroundable;

import java.util.List;

public class ItemView extends ViewBuilder {

    public DrinksMenuItem item;
    private boolean selected = false;
    private OnSelect selectListener;
    private SizeEditorFragment.OnResize resizeListener;
    private PositionEditorFragment.OnReposition positionListener;

    public ItemView(Context context) {
        this(context, null);
    }

    public ItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isResizable())
            super.resizableHandler.setResizeDimensions(50, 50, 1000, 1000);
        if (isDraggable())
            super.draggableHandler.setDraggableDimensions(-2000, -2000, 2000, 2000);
        onDeselect();
        int borderWidth = getResources().getDimensionPixelSize(R.dimen.item_group_border_width);
        setPadding(borderWidth, borderWidth, borderWidth, borderWidth);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
    }

    @Override
    public List<Abilities> defineAbilities() {
        return List.of(Abilities.BACKGROUNDABLE, Abilities.CLICKABLE, Abilities.DRAGGABLE, Abilities.RESIZEABLE);
    }

    public void notifyChanged(){
        invalidate();
    }

    public void setItem(DrinksMenuItem item) {
        this.item = item;
        if (item instanceof Backgroundable) {
            setBackgroundColor(((Backgroundable) item).getBackgroundColor());
            setCornerRadius(((Backgroundable) item).getCornerRadius());
        }
    }

    @Override
    public void onResize(int newWidth, int newHeight) {
        item.setWidth(newWidth);
        item.setHeight(newHeight);
        resizeListener.onResize(newWidth, newHeight, this);
    }

    @Override
    public void onDragged(int newLeft, int newTop) {
        item.setLeft(newLeft);
        item.setTop(newTop);
        positionListener.onReposition(newLeft, newTop, this);
    }

    @Override
    public void onClick() {
        selected = !selected;
        if (selected) onSelect();
        else onDeselect();
    }

    protected void onSelect() {
        select();
        if (selectListener != null)
            selectListener.onSelect(this);
    }

    private void select() {
        setBorderDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.vec_item_group_border_active, null));
        setCircleDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.vec_item_group_circle_active, null));
    }

    protected void onDeselect() {
        deselect();
        if (selectListener != null)
            selectListener.onSelect(null);
    }

    private void deselect() {
        setBorderDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.vec_item_group_border, null));
        setCircleDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.vec_item_group_circle, null));
    }

    public void requestSelect() {
        selected = true;
        select();
        invalidate();
    }

    public void requestDeselect() {
        selected = false;
        deselect();
        invalidate();
    }

    public void setSelectListener(OnSelect callback) {
        this.selectListener = callback;
    }

    public void setResizeListener(SizeEditorFragment.OnResize callback) {
        this.resizeListener = callback;
    }

    public void setRepositionListener(PositionEditorFragment.OnReposition callback) {
        this.positionListener = callback;
    }

    public interface OnSelect {
        void onSelect(ItemView itemView);
    }
}
