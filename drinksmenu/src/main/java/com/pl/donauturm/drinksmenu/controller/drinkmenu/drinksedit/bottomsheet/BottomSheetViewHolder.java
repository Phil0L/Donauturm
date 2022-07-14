package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.DrinksMenuEditorActivity;
import com.pl.donauturm.drinksmenu.model.Drink;
import com.pl.donauturm.drinksmenu.model.DrinkGroup;
import com.pl.donauturm.drinksmenu.model.Item;
import com.pl.donauturm.drinksmenu.model.Shape;
import com.pl.donauturm.drinksmenu.model.Text;
import com.pl.donauturm.drinksmenu.view.views.ItemView;

import java.util.Arrays;
import java.util.List;

public class BottomSheetViewHolder{

    public Item item;
    protected DrinksMenuEditorActivity activity;
    protected List<EDITORS> editors;
    protected ItemBottomSheet itemBottomSheet;
    private boolean keyboardOpen;

    private final View mHandle;
    private final LinearLayout mButtonRow, mNameRow;
    private final TextView mNameView;
    private final TextView mSizeView;
    private final TextView mPositionView;
    private final FrameLayout mButtonSize, mButtonPosition, mButtonSettings, mButtonItems;
    private final ImageView mNext, mPrevious;
    private final ViewPager2 mViewPager;

    public BottomSheetViewHolder(DrinksMenuEditorActivity activity, @NonNull View root, Item item, EDITORS... editors) {
        this.activity = activity;
        this.editors = Arrays.asList(editors);
        this.item = item;
        this.mHandle = root.findViewById(R.id.bottom_sheet_handle);
        this.mButtonRow = root.findViewById(R.id.bootom_sheet_item_row);
        this.mNameRow = root.findViewById(R.id.bottom_sheet_name_row);
        this.mNameView = root.findViewById(R.id.item_group_name);
        this.mSizeView = root.findViewById(R.id.item_group_size);
        this.mPositionView = root.findViewById(R.id.item_group_position);
        this.mButtonSize = root.findViewById(R.id.bs_item_size);
        this.mButtonPosition = root.findViewById(R.id.bs_item_position);
        this.mButtonSettings = root.findViewById(R.id.bs_item_settings);
        this.mButtonItems = root.findViewById(R.id.bs_item_items);
        this.mNext = root.findViewById(R.id.next_view);
        this.mPrevious = root.findViewById(R.id.previous_view);
        this.mViewPager = root.findViewById(R.id.bottom_sheet_pager);
        this.setup();
    }

    private void setup() {
        if (item instanceof DrinkGroup)
            this.itemBottomSheet = new DrinkGroupBottomSheet(
                activity.getSupportFragmentManager(),
                activity.getLifecycle(),
                (DrinkGroup) item, (DrinkGroupBottomSheet.DrinkGroupEvent) activity.getEventHandler());
        else if (item instanceof Drink)
            this.itemBottomSheet = new DrinkBottomSheet(
                    activity.getSupportFragmentManager(),
                    activity.getLifecycle(),
                    (Drink) item, (DrinkBottomSheet.DrinkEvent) activity.getEventHandler());
        else if (item instanceof Text)
            this.itemBottomSheet = new TextBottomSheet(
                    activity.getSupportFragmentManager(),
                    activity.getLifecycle(),
                    (Text) item, (TextBottomSheet.TextEvent) activity.getEventHandler());
        else if (item instanceof Shape)
            this.itemBottomSheet = new ShapeBottomSheet(
                    activity.getSupportFragmentManager(),
                    activity.getLifecycle(),
                    (Shape) item, (ShapeBottomSheet.ShapeEvent) activity.getEventHandler());
        else
            this.itemBottomSheet = new ItemBottomSheet(
                    activity.getSupportFragmentManager(),
                    activity.getLifecycle(),
                    item, activity.getEventHandler()
            );
        this.mViewPager.setAdapter(itemBottomSheet);
        this.mButtonSize.setVisibility(View.GONE);
        if (editors.contains(EDITORS.SIZE)) {
            this.mButtonSize.setOnClickListener(this::onNavButtonClick);
            this.mButtonSize.setVisibility(View.VISIBLE);
        }
        this.mButtonPosition.setVisibility(View.GONE);
        if (editors.contains(EDITORS.POSITION)) {
            this.mButtonPosition.setOnClickListener(this::onNavButtonClick);
            this.mButtonPosition.setVisibility(View.VISIBLE);
        }
        this.mButtonSettings.setVisibility(View.GONE);
        if (editors.contains(EDITORS.OPTIONS)) {
            this.mButtonSettings.setOnClickListener(this::onNavButtonClick);
            this.mButtonSettings.setVisibility(View.VISIBLE);
        }
        this.mButtonItems.setVisibility(View.GONE);
        if (editors.contains(EDITORS.ITEMS)) {
            this.mButtonItems.setOnClickListener(this::onNavButtonClick);
            this.mButtonItems.setVisibility(View.VISIBLE);
        }
        this.mPrevious.setOnClickListener(this::previousClicked);
        this.mNext.setOnClickListener(this::nextClicked);
    }

    @SuppressLint("SetTextI18n")
    public void update() {
        this.mNameView.setText(item.getName());
        this.mSizeView.setText(((int) item.getWidth()) + "x" + ((int) item.getHeight()));
        this.mPositionView.setText(((int) item.getLeft()) + "," + ((int) item.getTop()));
        this.itemBottomSheet.notifyDataChanged(item);
    }

    private void onNavButtonClick(View v) {
        if (mButtonSize.equals(v)) mViewPager.setCurrentItem(0);
        if (mButtonPosition.equals(v)) mViewPager.setCurrentItem(1);
        if (mButtonSettings.equals(v)) mViewPager.setCurrentItem(2);
        if (mButtonItems.equals(v)) mViewPager.setCurrentItem(3);
    }

    public void onToggleSoftKeyboard(boolean isVisible) {
        this.keyboardOpen = isVisible;
        mButtonRow.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        mNext.setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
        mPrevious.setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
    }

    public void setHeight(int height) {
        height -= ((ViewGroup.MarginLayoutParams) mHandle.getLayoutParams()).topMargin;
        height -= mHandle.getHeight();
        height -= mNameRow.getHeight();
        height -= ((ViewGroup.MarginLayoutParams) mNameRow.getLayoutParams()).bottomMargin;
        if (!keyboardOpen)
            height -= mButtonRow.getHeight();
        this.mViewPager.getLayoutParams().height = height;
        this.mViewPager.invalidate();
    }

    private void previousClicked(View v){
        ItemView iv = activity.getPreviousView();
        iv.requestSelect();
        activity.onSelect(iv);
    }

    private void nextClicked(View v){
        ItemView iv = activity.getNextView();
        iv.requestSelect();
        activity.onSelect(iv);
    }

    public enum EDITORS{
        POSITION, SIZE, OPTIONS, ITEMS
    }
}
