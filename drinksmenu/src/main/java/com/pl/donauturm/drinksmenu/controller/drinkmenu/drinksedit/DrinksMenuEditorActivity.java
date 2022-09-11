package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit;

import static com.pl.donauturm.drinksmenu.model.DrinksMenu.deserializer;
import static com.pl.donauturm.drinksmenu.model.DrinksMenu.serializer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.DrinksMenuRenderer;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.BottomSheetViewHolder;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.DrinkBottomSheet;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.DrinkGroupBottomSheet;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.ItemBottomSheet;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.ShapeBottomSheet;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.TextBottomSheet;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.generator.DrinkGenerator;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.generator.DrinkGroupGenerator;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.generator.ShapeGenerator;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.generator.TextGenerator;
import com.pl.donauturm.drinksmenu.databinding.ActivityDrinksMenuEditorBinding;
import com.pl.donauturm.drinksmenu.model.DrinksMenu;
import com.pl.donauturm.drinksmenu.model.content.DrinksMenuItem;
import com.pl.donauturm.drinksmenu.model.content.DrinkItem;
import com.pl.donauturm.drinksmenu.model.content.DrinkGroupItem;
import com.pl.donauturm.drinksmenu.model.content.ShapeItem;
import com.pl.donauturm.drinksmenu.model.content.TextItem;
import com.pl.donauturm.drinksmenu.util.KeyboardListener;
import com.pl.donauturm.drinksmenu.view.dialogs.AddDrinkDialog;
import com.pl.donauturm.drinksmenu.view.dialogs.AddDrinkGroupDialog;
import com.pl.donauturm.drinksmenu.view.dialogs.AddTextDialog;
import com.pl.donauturm.drinksmenu.view.dialogs.ItemSelectorDialog;
import com.pl.donauturm.drinksmenu.view.views.DrinkGroupView;
import com.pl.donauturm.drinksmenu.view.views.DrinkView;
import com.pl.donauturm.drinksmenu.view.views.ItemView;
import com.pl.donauturm.drinksmenu.view.views.ShapeView;
import com.pl.donauturm.drinksmenu.view.views.TextView;

import java.util.ArrayList;

import lv.chi.photopicker.PhotoPickerFragment;

@SuppressWarnings("FieldCanBeLocal")
public class DrinksMenuEditorActivity extends AppCompatActivity implements
        ItemView.OnSelect, ItemEventHandler.EditorProvider, ViewTreeObserver.OnGlobalLayoutListener,
        KeyboardListener.SoftKeyboardToggleListener, PhotoPickerFragment.Callback {

    private DrinksMenu drinksMenu;
    private ItemView selectedView;
    private BottomSheetBehavior<?> sheetBehavior;
    private ItemEventHandler eventHandler;
    private BottomSheetViewHolder bottomViewHolder;
    private KeyboardListener keyboardListener;
    private ActivityDrinksMenuEditorBinding binding;

    private View mRoot;
    private android.widget.TextView headerLeft, headerRight;
    private LinearLayout mLayoutBottomSheet;


    @Override
    public DrinksMenuItem getCurrentItem() {
        if (selectedView == null) return null;
        return selectedView.item;
    }

    @Override
    public ItemView getSelectedView() {
        return selectedView;
    }

    public ItemView getNextView() {
        int ci = binding.previewHolder.indexOfChild(getSelectedView());
        return getNextView(ci + 1);
    }

    @NonNull
    private ItemView getNextView(int ci) {
        View nv = binding.previewHolder.getChildAt(ci);
        if (nv == null) return getNextView(0);
        if (!(nv instanceof ItemView)) return getNextView(ci + 1);
        return (ItemView) nv;
    }

    public ItemView getPreviousView() {
        int ci = binding.previewHolder.indexOfChild(getSelectedView());
        return getPreviousView(ci - 1);
    }

    @NonNull
    private ItemView getPreviousView(int ci) {
        View nv = binding.previewHolder.getChildAt(ci);
        if (nv == null) return getPreviousView(binding.previewHolder.getChildCount() - 1);
        if (!(nv instanceof ItemView)) return getPreviousView(ci - 1);
        return (ItemView) nv;
    }

    public ItemView getViewFromItem(DrinksMenuItem item) {
        for (int i = 0; i < binding.previewHolder.getChildCount(); i++) {
            View v = binding.previewHolder.getChildAt(i);
            if (v instanceof ItemView) {
                ItemView iv = (ItemView) v;
                if (iv.item.equals(item)) return iv;
            }
        }
        return null;
    }

    @Override
    public BottomSheetViewHolder getBottomSheet() {
        return bottomViewHolder;
    }

    public ItemBottomSheet.ItemEvent getEventHandler() {
        return eventHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityDrinksMenuEditorBinding.inflate(getLayoutInflater());
        super.setContentView(binding.getRoot());

        // action bar
        super.setSupportActionBar(binding.toolbar);
        this.drinksMenu = deserializer(this).fromJson(getIntent().getStringExtra("menu"), DrinksMenu.class);
        if (getSupportActionBar() != null) {
            super.getSupportActionBar().setTitle(drinksMenu.getName());
            super.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            super.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // buttons
        this.binding.saveButton.setOnClickListener(v -> save());
        this.binding.cancelButton.setOnClickListener(v -> cancel());

        // preview
        this.binding.previewBackground.setImageBitmap(drinksMenu.getBackGround());
        this.binding.previewBackground.setOnClickListener(v -> onSelect(null));

        // bottom sheet
        this.mLayoutBottomSheet = findViewById(R.id.bottom_sheet_container);
        this.eventHandler = new ItemEventHandler(this);
        this.keyboardListener = new KeyboardListener(this, this);
        this.sheetBehavior = BottomSheetBehavior.from(mLayoutBottomSheet);
        this.sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        this.sheetBehavior.setSkipCollapsed(true);
        this.sheetBehavior.setFitToContents(false);
        this.sheetBehavior.setUpdateImportantForAccessibilityOnSiblings(true);
        this.sheetBehavior.setHalfExpandedRatio(.0000001f);
        this.sheetBehavior.addBottomSheetCallback(new BottomSheetActionHandler());

        // root
        this.mRoot = getWindow().getDecorView().getRootView();
        this.mRoot.getViewTreeObserver().addOnGlobalLayoutListener(this);

        // actions and info
        this.headerLeft = binding.actions.parentLayout.findViewById(R.id.header_text);
        this.headerRight = binding.infos.parentLayout.findViewById(R.id.header_text);
        this.headerLeft.setText(R.string.header_actions);
        this.headerRight.setText(R.string.header_info);
        this.binding.actions.parentLayout.setOnClickListener(v -> {
            if (binding.actions.isExpanded()) binding.actions.collapse();
            else binding.actions.expand();
        });
        this.binding.infos.parentLayout.setOnClickListener(v -> {
            if (binding.infos.isExpanded()) binding.infos.collapse();
            else binding.infos.expand();
        });
        this.binding.actions.setSpinnerColor(getResources().getColor(R.color.black, null));
        this.binding.infos.setSpinnerColor(getResources().getColor(R.color.black, null));
        this.binding.actions.secondLayout.findViewById(R.id.change_background).setOnClickListener(v -> onChangeBackgroundClicked());
        this.binding.actions.secondLayout.findViewById(R.id.change_name).setOnClickListener(v -> onChangeNameClicked());
        this.updateInfo();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drinks_menu_editor_actions, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem deleteItem = menu.findItem(R.id.delete_item);
        deleteItem.setVisible(selectedView != null);
        MenuItem cloneItem = menu.findItem(R.id.clone_item);
        cloneItem.setVisible(selectedView != null);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            cancel();
        }
    }

    @Override
    public void onGlobalLayout() {
        mRoot.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        new PreviewRenderer().renderPreview();
        keyboardListener.provideHeightReference(binding.saveButton);
        mRoot.post(this::generateImage);
    }


    @Override
    public void onToggleSoftKeyboard(boolean isVisible, int height) {
        if (bottomViewHolder != null) {
            bottomViewHolder.onToggleSoftKeyboard(isVisible);
            int topOffset = (int) (binding.previewBackground.getY() + binding.previewBackground.getPaddingTop() + binding.previewBackground.getHeight() + binding.previewBackground.getPaddingBottom() - ((ViewGroup.MarginLayoutParams) mLayoutBottomSheet.getLayoutParams()).topMargin);
            int bottomHeight = binding.rootCoordinator.getHeight() - topOffset;
            bottomViewHolder.setHeight(bottomHeight);
        }
    }

    @Override
    public void onSelect(ItemView itemView) {
        int topOffset = (int) (binding.previewBackground.getY() + binding.previewBackground.getPaddingTop() + binding.previewBackground.getHeight() + binding.previewBackground.getPaddingBottom() - ((ViewGroup.MarginLayoutParams) mLayoutBottomSheet.getLayoutParams()).topMargin);
        int bottomHeight = binding.rootCoordinator.getHeight() - topOffset;
        this.sheetBehavior.setExpandedOffset(topOffset);
        if (selectedView != null)
            selectedView.requestDeselect();
        if (itemView == null) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            selectedView = null;
            if (eventHandler.hasChanged()) {
                generateImage();
                eventHandler.resetChanged();
            }
            invalidateOptionsMenu();
            return;
        }
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        selectedView = itemView;
        if (itemView.item instanceof DrinkGroupItem) {
            eventHandler = new DrinkGroupEventHandler(this);
            bottomViewHolder = new BottomSheetViewHolder(this, mLayoutBottomSheet, itemView.item, DrinkGroupBottomSheet.getEditors());
        } else if (itemView.item instanceof DrinkItem) {
            eventHandler = new DrinkEventHandler(this);
            bottomViewHolder = new BottomSheetViewHolder(this, mLayoutBottomSheet, itemView.item, DrinkBottomSheet.getEditors());
        } else if (itemView.item instanceof TextItem) {
            eventHandler = new TextEventHandler(this);
            bottomViewHolder = new BottomSheetViewHolder(this, mLayoutBottomSheet, itemView.item, TextBottomSheet.getEditors());
        } else if (itemView.item instanceof ShapeItem) {
            eventHandler = new ShapeEventHandler(this);
            bottomViewHolder = new BottomSheetViewHolder(this, mLayoutBottomSheet, itemView.item, ShapeBottomSheet.getEditors());
        } else {
            eventHandler = new ItemEventHandler(this);
            bottomViewHolder = new BottomSheetViewHolder(this, mLayoutBottomSheet, itemView.item, ItemBottomSheet.getEditors());
        }
        bottomViewHolder.setHeight(bottomHeight);
        bottomViewHolder.update();
        binding.previewHolder.bringChildToFront(selectedView);
        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getTitle().toString()) {
            case "AddItem":
                return onAddItemClicked();
            case "DeleteItem":
                return onDeleteItemClicked();
            case "CloneItem":
                return onCloneItemClicked();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean onCloneItemClicked() {
        DrinksMenuItem item = selectedView.item;
        DrinksMenuItem clone = item.clone();
        // offset clone by 50 pixel to avoid overlapping with original item
        clone.setLeft(item.getLeft() + 50);
        clone.setTop(item.getTop() + 50);
        // render clone
        drinksMenu.addItem(clone);
        new PreviewRenderer().renderItem(clone);
        eventHandler.onAdd();
        selectedView.post(() -> {
            ItemView cloneView = getViewFromItem(clone);
            onSelect(cloneView);
        });
        return true;
    }

    private boolean onDeleteItemClicked() {
        if (selectedView == null) return false;
        drinksMenu.removeItem(selectedView.item);
        new PreviewRenderer().killItem(selectedView);
        eventHandler.onDelete();
        onSelect(null);
        return true;
    }

    private void onChangeBackgroundClicked() {
        PhotoPickerFragment.Companion
                .newInstance(false, false, 1, R.style.ChiliPhotoPicker_Light)
                .show(getSupportFragmentManager(), "PhotoPickerFragment");
    }

    @Override
    public void onImagesPicked(@NonNull ArrayList<Uri> arrayList) {
        Glide.with(this)
                .asBitmap()
                .load(arrayList.get(0))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (resource.getWidth() < resource.getHeight()) {
                            Toast.makeText(getApplicationContext(), "Image must be in landscape orientation", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        drinksMenu.provideBackGround(resource);
                        binding.previewBackground.setImageBitmap(resource);
                        mRoot.post(() -> generateImage());
                        updateInfo();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    private void onChangeNameClicked() {

    }

    @SuppressLint("DefaultLocale")
    private void updateInfo() {
        android.widget.TextView size = binding.infos.secondLayout.findViewById(R.id.info_size);
        android.widget.TextView created = binding.infos.secondLayout.findViewById(R.id.info_created);
        android.widget.TextView elements = binding.infos.secondLayout.findViewById(R.id.info_element_count);
        size.setText(String.format("%s x %s", drinksMenu.getWidth(), drinksMenu.getHeight()));
        created.setText(String.format("Version %s", drinksMenu.getVersion()));
        elements.setText(String.format("Elements: %d", drinksMenu.getItems().size()));
    }

    private boolean onAddItemClicked() {
        ItemSelectorDialog itemSelectorDialog = ItemSelectorDialog.newInstance();
        itemSelectorDialog.setOnItemSelectedListener(itemClass -> {
            switch (itemClass.getSimpleName()) {
                case "Drink":
                    AddDrinkDialog addDrinkDialog = AddDrinkDialog.newInstance();
                    addDrinkDialog.setOnDrinkSelectedListener(drink -> {
                        drinksMenu.addItem(drink);
                        new PreviewRenderer().renderDrink(drink);
                        eventHandler.onAdd();
                        mRoot.post(() -> {
                            ItemView newView = getViewFromItem(drink);
                            newView.requestSelect();
                            onSelect(newView);
                        });
                    });
                    addDrinkDialog.show(getSupportFragmentManager(), getClass().getSimpleName());
                    break;
                case "DrinkGroup":
                    AddDrinkGroupDialog addDrinkGroupDialog = AddDrinkGroupDialog.newInstance();
                    addDrinkGroupDialog.setOnDrinkGroupSelectedListener(drinkGroup -> {
                        drinksMenu.addItem(drinkGroup);
                        new PreviewRenderer().renderDrinkGroup(drinkGroup);
                        eventHandler.onAdd();
                        mRoot.post(() -> {
                            ItemView newView = getViewFromItem(drinkGroup);
                            newView.requestSelect();
                            onSelect(newView);
                        });
                    });
                    addDrinkGroupDialog.show(getSupportFragmentManager(), getClass().getSimpleName());
                    break;
                case "Text":
                    AddTextDialog addTextDialog = AddTextDialog.newInstance();
                    addTextDialog.setOnTextSelectedListener(text -> {
                        drinksMenu.addItem(text);
                        new PreviewRenderer().renderText(text);
                        eventHandler.onAdd();
                        mRoot.post(() -> {
                            ItemView newView = getViewFromItem(text);
                            newView.requestSelect();
                            onSelect(newView);
                        });
                    });
                    addTextDialog.show(getSupportFragmentManager(), getClass().getSimpleName());
                    break;
                case "Shape":
                    ShapeItem shapeItem = new ShapeItem("New Shape", Color.WHITE, 100);
                    shapeItem.createNewId();
                    drinksMenu.addItem(shapeItem);
                    new PreviewRenderer().renderShape(shapeItem);
                    eventHandler.onAdd();
                    mRoot.post(() -> {
                        ItemView newView = getViewFromItem(shapeItem);
                        newView.requestSelect();
                        onSelect(newView);
                    });
                    break;

            }
        });
        itemSelectorDialog.show(getSupportFragmentManager(), getClass().getSimpleName());
        return true;
    }

    @Override
    public void generateImage() {
        updateInfo();
        binding.generatedMenu.setImageBitmap(drinksMenu.getBackGround());
        new DrinksMenuRenderer().renderAsyncFromView(binding.previewHolder, bm -> {
            drinksMenu.provideMenuImage(bm);
            binding.generatedMenu.post(() -> binding.generatedMenu.setImageBitmap(bm));
        });
    }

    public void save() {
        drinksMenu.increaseVersion();
        Intent intent = new Intent();
        String menuString = serializer(this).toJson(drinksMenu);
        intent.putExtra("menu", menuString);
        setResult(RESULT_OK, intent);
        finish();

    }

    public void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private class PreviewRenderer {

        private void renderPreview() {
            for (DrinksMenuItem item : drinksMenu.getItems()) {
                renderItem(item);
            }
        }

        private void renderItem(DrinksMenuItem item) {
            if (item instanceof DrinkGroupItem)
                renderDrinkGroup((DrinkGroupItem) item);
            if (item instanceof DrinkItem)
                renderDrink((DrinkItem) item);
            if (item instanceof TextItem)
                renderText(((TextItem) item));
            if (item instanceof ShapeItem)
                renderShape((ShapeItem) item);
        }

        private void renderDrinkGroup(DrinkGroupItem drinkGroupItem) {
            DrinkGroupGenerator drinkGroupGenerator = new DrinkGroupGenerator();
            DrinkGroupView drinkGroupView = drinkGroupGenerator.generateNewPreviewDrinkGroup(binding.previewHolder, drinkGroupItem);
            drinkGroupView.setItem(drinkGroupItem);
            DrinkGroupView.GridAdapter adapter = drinkGroupView.newGridAdapter();
            drinkGroupView.setAdapter(adapter);
            drinkGroupView.setSelectListener(DrinksMenuEditorActivity.this);
            drinkGroupView.setResizeListener(eventHandler);
            drinkGroupView.setRepositionListener(eventHandler);
        }

        private void renderDrink(DrinkItem drinkItem) {
            DrinkGenerator drinkGroupGenerator = new DrinkGenerator();
            DrinkView drinkView = drinkGroupGenerator.generateNewPreviewDrink(binding.previewHolder, drinkItem);
            drinkView.setItem(drinkItem);
            DrinkView.SingleAdapter adapter = drinkView.newSingleAdapter();
            drinkView.setAdapter(adapter);
            drinkView.setSelectListener(DrinksMenuEditorActivity.this);
            drinkView.setResizeListener(eventHandler);
            drinkView.setRepositionListener(eventHandler);
        }

        private void renderText(TextItem textItem) {
            TextGenerator textGenerator = new TextGenerator();
            TextView textView = textGenerator.generateNewPreviewText(binding.previewHolder, textItem);
            textView.setItem(textItem);
            TextView.SingleAdapter adapter = textView.newSingleAdapter();
            textView.setAdapter(adapter);
            textView.setSelectListener(DrinksMenuEditorActivity.this);
            textView.setResizeListener(eventHandler);
            textView.setRepositionListener(eventHandler);
        }

        private void renderShape(ShapeItem shapeItem) {
            ShapeGenerator shapeGenerator = new ShapeGenerator();
            ShapeView shapeView = shapeGenerator.generateNewPreviewShape(binding.previewHolder, shapeItem);
            shapeView.setItem(shapeItem);
            shapeView.setSelectListener(DrinksMenuEditorActivity.this);
            shapeView.setResizeListener(eventHandler);
            shapeView.setRepositionListener(eventHandler);
        }

        private void killItem(ItemView itemView) {
            binding.previewHolder.removeView(itemView);
        }
    }

    public static class ValueScale {

        private final DrinksMenuEditorActivity context;
        private int previewWidth = 0;
        private int bitmapWidth = 0;

        public static ValueScale with(Context context) {
            return new ValueScale(context);
        }

        public static ValueScale with(int bitmapWidth, int previewWidth) {
            ValueScale vs = new ValueScale(null);
            vs.bitmapWidth = bitmapWidth;
            vs.previewWidth = previewWidth;
            return vs;
        }

        private ValueScale(Context context) {
            if (context instanceof DrinksMenuEditorActivity)
                this.context = (DrinksMenuEditorActivity) context;
            else this.context = null;
        }

        public int scaleViewToPosition(int draggedPosition) {
            if (context == null && previewWidth == 0) return draggedPosition;
            if (context != null && bitmapWidth == 0)
                bitmapWidth = context.binding.previewBackground.getDrawable().getIntrinsicWidth();
            if (previewWidth == 0) previewWidth = context.binding.previewBackground.getWidth();
            float factor = (float) bitmapWidth / previewWidth;
            return (int) (draggedPosition * factor);
        }

        public int scalePositionToView(int draggedPosition) {
            if (context == null && previewWidth == 0) return draggedPosition;
            if (context != null && bitmapWidth == 0)
                bitmapWidth = context.binding.previewBackground.getDrawable().getIntrinsicWidth();
            if (previewWidth == 0) previewWidth = context.binding.previewBackground.getWidth();
            float factor = (float) bitmapWidth / previewWidth;
            return (int) (draggedPosition / factor);
        }
    }

    private class BottomSheetActionHandler extends BottomSheetBehavior.BottomSheetCallback {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                onSelect(null);
                if (selectedView != null)
                    selectedView.requestDeselect();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    }

}