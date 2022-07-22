package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit;

import static com.pl.donauturm.drinksmenu.model.DrinksMenu.deserializer;
import static com.pl.donauturm.drinksmenu.model.DrinksMenu.serializer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

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
import com.pl.donauturm.drinksmenu.view.dialogs.AddDrinkDialog;
import com.pl.donauturm.drinksmenu.view.dialogs.AddDrinkGroupDialog;
import com.pl.donauturm.drinksmenu.view.dialogs.AddTextDialog;
import com.pl.donauturm.drinksmenu.view.dialogs.ItemSelectorDialog;
import com.pl.donauturm.drinksmenu.view.layouts.PreviewHolder;
import com.pl.donauturm.drinksmenu.view.views.DrinkGroupView;
import com.pl.donauturm.drinksmenu.view.views.DrinkView;
import com.pl.donauturm.drinksmenu.view.views.ItemView;
import com.pl.donauturm.drinksmenu.view.views.ShapeView;
import com.pl.donauturm.drinksmenu.view.views.TextView;
import com.pl.donauturm.drinksmenu.model.content.Drink;
import com.pl.donauturm.drinksmenu.model.content.DrinkGroup;
import com.pl.donauturm.drinksmenu.model.DrinksMenu;
import com.pl.donauturm.drinksmenu.model.Item;
import com.pl.donauturm.drinksmenu.model.content.Shape;
import com.pl.donauturm.drinksmenu.model.content.Text;
import com.pl.donauturm.drinksmenu.util.KeyboardListener;

@SuppressWarnings("FieldCanBeLocal")
public class DrinksMenuEditorActivity extends AppCompatActivity
        implements ItemView.OnSelect, ItemEventHandler.EditorProvider, ViewTreeObserver.OnGlobalLayoutListener, KeyboardListener.SoftKeyboardToggleListener {

    private DrinksMenu drinksMenu;
    private ItemView selectedView;
    private BottomSheetBehavior<?> sheetBehavior;
    private ItemEventHandler eventHandler;
    private BottomSheetViewHolder bottomViewHolder;
    private KeyboardListener keyboardListener;

    private View mRoot;
    private Button mSave;
    private Button mCancel;
    private CoordinatorLayout mCoordinator;
    private PreviewHolder mFrameLayout;
    private ImageView mBackgroundView;
    private ImageView mGeneratedView;
    private LinearLayout mLayoutBottomSheet;


    @Override
    public Item getCurrentItem() {
        if (selectedView == null) return null;
        return selectedView.item;
    }

    @Override
    public ItemView getSelectedView() {
        return selectedView;
    }

    public ItemView getNextView(){
        int ci = mFrameLayout.indexOfChild(getSelectedView());
        return getNextView(ci+1);
    }

    @NonNull
    private ItemView getNextView(int ci){
        View nv = mFrameLayout.getChildAt(ci);
        if (nv == null) return getNextView(0);
        if (!(nv instanceof ItemView)) return getNextView(ci+1);
        return (ItemView) nv;
    }

    public ItemView getPreviousView(){
        int ci = mFrameLayout.indexOfChild(getSelectedView());
        return getPreviousView(ci-1);
    }

    @NonNull
    private ItemView getPreviousView(int ci){
        View nv = mFrameLayout.getChildAt(ci);
        if (nv == null) return getPreviousView(mFrameLayout.getChildCount()-1);
        if (!(nv instanceof ItemView)) return getPreviousView(ci-1);
        return (ItemView) nv;
    }

    public ItemView getViewFromItem(Item item){
        for (int i = 0; i < mFrameLayout.getChildCount(); i++) {
            View v = mFrameLayout.getChildAt(i);
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
        super.setContentView(R.layout.activity_drinks_menu_editor);
        this.drinksMenu = deserializer(this).fromJson(getIntent().getStringExtra("menu"), DrinksMenu.class);
        this.mSave = findViewById(R.id.save_button);
        this.mSave.setOnClickListener(this::save);
        this.mCancel = findViewById(R.id.cancel_button);
        this.mCancel.setOnClickListener(this::cancel);
        this.mCoordinator = findViewById(R.id.root_coordinator);
        this.mFrameLayout = findViewById(R.id.preview_holder);
        this.mBackgroundView = findViewById(R.id.preview_background);
        this.mBackgroundView.setImageBitmap(drinksMenu.getBackGround());
        this.mGeneratedView = findViewById(R.id.generated_menu);
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
        this.mRoot = getWindow().getDecorView().getRootView();
        this.mRoot.getViewTreeObserver().addOnGlobalLayoutListener(this);
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
    public void onGlobalLayout() {
        mRoot.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        new PreviewRenderer().renderPreview();
        keyboardListener.provideHeightReference(mSave);
        mRoot.post(this::generateImage);
    }


    @Override
    public void onToggleSoftKeyboard(boolean isVisible, int height) {
        if (bottomViewHolder != null) {
            bottomViewHolder.onToggleSoftKeyboard(isVisible);
            int topOffset = (int) (mBackgroundView.getY() + mBackgroundView.getPaddingTop() + mBackgroundView.getHeight() + mBackgroundView.getPaddingBottom() - ((ViewGroup.MarginLayoutParams) mLayoutBottomSheet.getLayoutParams()).topMargin);
            int bottomHeight = this.mCoordinator.getHeight() - topOffset;
            bottomViewHolder.setHeight(bottomHeight);
        }
    }

    @Override
    public void onSelect(ItemView itemView) {
        int topOffset = (int) (mBackgroundView.getY() + mBackgroundView.getPaddingTop() + mBackgroundView.getHeight() + mBackgroundView.getPaddingBottom() - ((ViewGroup.MarginLayoutParams) mLayoutBottomSheet.getLayoutParams()).topMargin);
        int bottomHeight = this.mCoordinator.getHeight() - topOffset;
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
        if (itemView.item instanceof DrinkGroup) {
            eventHandler = new DrinkGroupEventHandler(this);
            bottomViewHolder = new BottomSheetViewHolder(this, mLayoutBottomSheet, itemView.item, DrinkGroupBottomSheet.getEditors());
        } else if (itemView.item instanceof Drink) {
            eventHandler = new DrinkEventHandler(this);
            bottomViewHolder = new BottomSheetViewHolder(this, mLayoutBottomSheet, itemView.item, DrinkBottomSheet.getEditors());
        } else if (itemView.item instanceof Text) {
            eventHandler = new TextEventHandler(this);
            bottomViewHolder = new BottomSheetViewHolder(this, mLayoutBottomSheet, itemView.item, TextBottomSheet.getEditors());
        } else if (itemView.item instanceof Shape) {
            eventHandler = new ShapeEventHandler(this);
            bottomViewHolder = new BottomSheetViewHolder(this, mLayoutBottomSheet, itemView.item, ShapeBottomSheet.getEditors());
        } else {
            eventHandler = new ItemEventHandler(this);
            bottomViewHolder = new BottomSheetViewHolder(this, mLayoutBottomSheet, itemView.item, ItemBottomSheet.getEditors());
        }
        bottomViewHolder.setHeight(bottomHeight);
        bottomViewHolder.update();
        mFrameLayout.bringChildToFront(selectedView);
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

    private boolean onCloneItemClicked(){
        Item item = selectedView.item;
        Item clone = item.clone();
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
                    Shape shape = new Shape("New Shape", Color.WHITE, 100);
                    drinksMenu.addItem(shape);
                    new PreviewRenderer().renderShape(shape);
                    eventHandler.onAdd();
                    mRoot.post(() -> {
                        ItemView newView = getViewFromItem(shape);
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
        mGeneratedView.setImageBitmap(drinksMenu.getBackGround());
        new DrinksMenuRenderer().renderAsyncFromView(mFrameLayout, bm -> {
            drinksMenu.provideMenuImage(bm);
            mGeneratedView.post(() -> mGeneratedView.setImageBitmap(bm));
        });
    }

    public void save(View v) {
        drinksMenu.increaseVersion();
        Intent intent = new Intent();
        String menuString = serializer(this).toJson(drinksMenu);
        intent.putExtra("menu", menuString);
        setResult(RESULT_OK, intent);
        finish();

    }

    public void cancel(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }

    private class PreviewRenderer {

        private void renderPreview() {
            for (Item item : drinksMenu.getItems()) {
                renderItem(item);
            }
        }

        private void renderItem(Item item) {
            if (item instanceof DrinkGroup)
                renderDrinkGroup((DrinkGroup) item);
            if (item instanceof Drink)
                renderDrink((Drink) item);
            if (item instanceof Text)
                renderText(((Text) item));
            if (item instanceof Shape)
                renderShape((Shape) item);
        }

        private void renderDrinkGroup(DrinkGroup drinkGroup) {
            DrinkGroupGenerator drinkGroupGenerator = new DrinkGroupGenerator();
            DrinkGroupView drinkGroupView = drinkGroupGenerator.generateNewPreviewDrinkGroup(mFrameLayout, drinkGroup);
            drinkGroupView.setItem(drinkGroup);
            DrinkGroupView.GridAdapter adapter = drinkGroupView.newGridAdapter();
            drinkGroupView.setAdapter(adapter);
            drinkGroupView.setSelectListener(DrinksMenuEditorActivity.this);
            drinkGroupView.setResizeListener(eventHandler);
            drinkGroupView.setRepositionListener(eventHandler);
        }

        private void renderDrink(Drink drink) {
            DrinkGenerator drinkGroupGenerator = new DrinkGenerator();
            DrinkView drinkView = drinkGroupGenerator.generateNewPreviewDrink(mFrameLayout, drink);
            drinkView.setItem(drink);
            DrinkView.SingleAdapter adapter = drinkView.newSingleAdapter();
            drinkView.setAdapter(adapter);
            drinkView.setSelectListener(DrinksMenuEditorActivity.this);
            drinkView.setResizeListener(eventHandler);
            drinkView.setRepositionListener(eventHandler);
        }

        private void renderText(Text text) {
            TextGenerator textGenerator = new TextGenerator();
            TextView textView = textGenerator.generateNewPreviewText(mFrameLayout, text);
            textView.setItem(text);
            TextView.SingleAdapter adapter = textView.newSingleAdapter();
            textView.setAdapter(adapter);
            textView.setSelectListener(DrinksMenuEditorActivity.this);
            textView.setResizeListener(eventHandler);
            textView.setRepositionListener(eventHandler);
        }

        private void renderShape(Shape shape) {
            ShapeGenerator shapeGenerator = new ShapeGenerator();
            ShapeView shapeView = shapeGenerator.generateNewPreviewShape(mFrameLayout, shape);
            shapeView.setItem(shape);
            shapeView.setSelectListener(DrinksMenuEditorActivity.this);
            shapeView.setResizeListener(eventHandler);
            shapeView.setRepositionListener(eventHandler);
        }

        private void killItem(ItemView itemView){
            mFrameLayout.removeView(itemView);
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
                bitmapWidth = context.mBackgroundView.getDrawable().getIntrinsicWidth();
            if (previewWidth == 0) previewWidth = context.mBackgroundView.getWidth();
            float factor = (float) bitmapWidth / previewWidth;
            return (int) (draggedPosition * factor);
        }

        public int scalePositionToView(int draggedPosition) {
            if (context == null && previewWidth == 0) return draggedPosition;
            if (context != null && bitmapWidth == 0)
                bitmapWidth = context.mBackgroundView.getDrawable().getIntrinsicWidth();
            if (previewWidth == 0) previewWidth = context.mBackgroundView.getWidth();
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