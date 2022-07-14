package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.editors;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.view.preferences.color.ColorPreference;
import com.pl.donauturm.drinksmenu.view.preferences.font.FontPreference;
import com.pl.donauturm.drinksmenu.model.Drink;
import com.pl.donauturm.drinksmenu.model.DrinkGroup;
import com.pl.donauturm.drinksmenu.model.Font;
import com.pl.donauturm.drinksmenu.model.Item;
import com.pl.donauturm.drinksmenu.model.interfaces.Backgroundable;
import com.pl.donauturm.drinksmenu.model.interfaces.Drinktextable;
import com.pl.donauturm.drinksmenu.model.interfaces.Group;
import com.pl.donauturm.drinksmenu.model.interfaces.Textable;
import com.pl.donauturm.drinksmenu.view.preferences.drink.DrinkPreference;

public class OptionsEditorFragment extends Fragment {

    public static final String KEY_ITEM_NAME = "item_name";
    public static final String KEY_DRINK = "item_drink";
    public static final String KEY_ITEM_BACKGROUND = "item_back_color";
    public static final String KEY_CORNER_RADIUS = "item_corner_radius";
    public static final String KEY_COLUMN_COUNT = "item_columns";
    public static final String KEY_COLUMN_SPACING = "item_column_spacing";
    public static final String KEY_ROW_SPACING = "item_row_spacing";
    public static final String KEY_ITEM_NAME_COLOR = "item_name_color";
    public static final String KEY_ITEM_NAME_SIZE = "item_name_font_size";
    public static final String KEY_ITEM_NAME_FONT = "item_name_font";
    public static final String KEY_ITEM_DESC_COLOR = "item_desc_color";
    public static final String KEY_ITEM_DESC_SIZE = "item_desc_font_size";
    public static final String KEY_ITEM_DESC_FONT = "item_desc_font";
    public static final String KEY_ITEM_PRICE_VISIBLE = "item_price_visible";
    public static final String KEY_ITEM_PRICE_COLOR = "item_price_color";
    public static final String KEY_ITEM_PRICE_SIZE = "item_price_font_size";
    public static final String KEY_ITEM_PRICE_FONT = "item_price_font";

    private Item item;
    private OnOptionsChanged callback;

    public OptionsEditorFragment() {
        // Required empty public constructor
    }

    public OptionsEditorFragment(Item item, OnOptionsChanged callback){
        this.item = item;
        this.callback = callback;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DrinksMenuFragment.
     */
    public static OptionsEditorFragment newInstance(Item item, OnOptionsChanged callback) {
        return new OptionsEditorFragment(item, callback);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.options_holder, new SettingsFragment(this))
                    .commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_options, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public String prepareItemName(){
        return item.getName();
    }

    public boolean onItemNameChange(Preference preference, Object newValue){
        if (newValue instanceof String){
            String itemName = ((String) newValue);
            if (itemName.isEmpty()) return false;
            callback.onNameChanged(itemName);
            ((EditTextPreference) preference).setText(itemName);
        }
        return false;
    }

    public Drink prepareDrink(){
        if (item instanceof Drink)
            return (Drink) item;
        return null;
    }

    public boolean onDrinkChange(Preference preference, Object newValue){
        if (newValue instanceof Drink){
            Drink drink = ((Drink) newValue);
            if (callback instanceof OnDrinkChanged)
                ((OnDrinkChanged) callback).onDrinkChanged(drink);
            ((DrinkPreference) preference).setValue(drink);
        }
        return false;
    }

    @ColorInt
    public int prepareItemBackground(){
        if (item instanceof Backgroundable)
            return ((Backgroundable) item).getBackgroundColor();
        return 0;
    }

    public boolean onItemBackgroundChange(Preference preference, Object newValue){
        if (newValue instanceof Integer){
            int itemBackground = ((int) newValue);
            if (itemBackground == 0) return false;
            if (callback instanceof OnBackgroundOptionsChanged)
                ((OnBackgroundOptionsChanged) callback).onBackgroundChanged(itemBackground);
            ((ColorPreference) preference).setColor(itemBackground);
        }
        return false;
    }

    public int prepareCornerRadius(){
        if (item instanceof Backgroundable)
            return ((Backgroundable) item).getCornerRadius();
        return 0;
    }

    public boolean onCornerRadiusChange(Preference preference, Object newValue){
        if (newValue instanceof Integer){
            int radius = ((int) newValue);
            if (callback instanceof OnBackgroundOptionsChanged)
                ((OnBackgroundOptionsChanged) callback).onCornerRadiusChanged(radius);
            ((SeekBarPreference) preference).setValue(radius);
        }
        return false;
    }

    public int prepareColumnCount(){
        if (item instanceof DrinkGroup)
            return ((DrinkGroup) item).getColumnCount();
        return 1;
    }

    public boolean onColumnCountChange(Preference preference, Object newValue){
        if (newValue instanceof Integer){
            int columnCount = ((int) newValue);
            if (columnCount == 0) return false;
            if (callback instanceof OnGridOptionsChanged)
                ((OnGridOptionsChanged) callback).onColumnCountChange(columnCount);
            ((SeekBarPreference) preference).setValue(columnCount);
        }
        return false;
    }

    public int prepareColumnSpacing(){
        if (item instanceof DrinkGroup)
            return ((DrinkGroup) item).getColumnSpacing();
        return 0;
    }

    public boolean onColumnSpacingChange(Preference preference, Object newValue){
        if (newValue instanceof Integer){
            int columnSpacing = ((int) newValue);
            if (callback instanceof OnGridOptionsChanged)
                ((OnGridOptionsChanged) callback).onColumnSpacingChange(columnSpacing);
            ((SeekBarPreference) preference).setValue(columnSpacing);
        }
        return false;
    }

    public int prepareRowSpacing(){
        if (item instanceof DrinkGroup)
            return ((DrinkGroup) item).getRowSpacing();
        return 0;
    }

    public boolean onRowSpacingChange(Preference preference, Object newValue){
        if (newValue instanceof Integer){
            int rowSpacing = ((int) newValue);
            if (callback instanceof OnGridOptionsChanged)
                ((OnGridOptionsChanged) callback).onRowSpacingChange(rowSpacing);
            ((SeekBarPreference) preference).setValue(rowSpacing);
        }
        return false;
    }

    @ColorInt
    public int prepareItemNameColor(){
        if (item instanceof Drinktextable)
            return ((Drinktextable) item).getNameColor();
        if (item instanceof Textable)
            return ((Textable) item).getColor();
        return 0;
    }

    public boolean onItemNameColorChange(Preference preference, Object newValue){
        if (newValue instanceof Integer){
            int itemNameColor = ((int) newValue);
            if (itemNameColor == 0) return false;
            if (callback instanceof OnDrinkTextOptionsChanged)
                ((OnDrinkTextOptionsChanged) callback).onNameColorChange(itemNameColor);
            if (callback instanceof OnTextOptionsChanged)
                ((OnTextOptionsChanged) callback).onColorChange(itemNameColor);
            ((ColorPreference) preference).setColor(itemNameColor);
        }
        return false;
    }

    public int prepareItemNameSize(){
        if (item instanceof Drinktextable)
            return ((Drinktextable) item).getNameFontSize();
        if (item instanceof Textable)
            return ((Textable) item).getFontSize();
        return 0;
    }

    public boolean onItemNameSizeChange(Preference preference, Object newValue){
        if (newValue instanceof Integer){
            int itemNameSize = ((int) newValue);
            if (itemNameSize == 0) return false;
            if (callback instanceof OnDrinkTextOptionsChanged)
                ((OnDrinkTextOptionsChanged) callback).onNameSizeChange(itemNameSize);
            if (callback instanceof OnTextOptionsChanged)
                ((OnTextOptionsChanged) callback).onSizeChange(itemNameSize);
            ((SeekBarPreference) preference).setValue(itemNameSize);
        }
        return false;
    }

    public Font prepareItemNameFont(){
        if (item instanceof Drinktextable)
            return ((Drinktextable) item).getNameFont();
        if (item instanceof Textable)
            return ((Textable) item).getFont();
        return null;
    }

    public boolean onItemNameFontChange(Preference preference, Object newValue){
        if (newValue instanceof Font){
            Font itemNameFont = ((Font) newValue);
            if (!itemNameFont.isValid()) return false;
            if (callback instanceof OnDrinkTextOptionsChanged)
                ((OnDrinkTextOptionsChanged) callback).onNameFontChange(itemNameFont);
            if (callback instanceof OnTextOptionsChanged)
                ((OnTextOptionsChanged) callback).onFontChange(itemNameFont);
            ((FontPreference) preference).setValue(itemNameFont);
        }
        return false;
    }

    @ColorInt
    public int prepareItemDescColor(){
        if (item instanceof Drinktextable)
            return ((Drinktextable) item).getDescriptionColor();
        return 0;
    }

    public boolean onItemDescColorChange(Preference preference, Object newValue){
        if (newValue instanceof Integer){
            int itemDescColor = ((int) newValue);
            if (itemDescColor == 0) return false;
            if (callback instanceof OnDrinkTextOptionsChanged)
                ((OnDrinkTextOptionsChanged) callback).onDescColorChange(itemDescColor);
            ((ColorPreference) preference).setColor(itemDescColor);
        }
        return false;
    }

    public int prepareItemDescSize(){
        if (item instanceof Drinktextable)
            return ((Drinktextable) item).getDescriptionFontSize();
        return 0;
    }

    public boolean onItemDescSizeChange(Preference preference, Object newValue){
        if (newValue instanceof Integer){
            int itemDescSize = ((int) newValue);
            if (itemDescSize == 0) return false;
            if (callback instanceof OnDrinkTextOptionsChanged)
                ((OnDrinkTextOptionsChanged) callback).onDescSizeChange(itemDescSize);
            ((SeekBarPreference) preference).setValue(itemDescSize);
        }
        return false;
    }

    public Font prepareItemDescFont(){
        if (item instanceof Drinktextable)
            return ((Drinktextable) item).getDescriptionFont();
        return null;
    }

    public boolean onItemDescFontChange(Preference preference, Object newValue){
        if (newValue instanceof Font){
            Font itemDescFont = ((Font) newValue);
            if (!itemDescFont.isValid()) return false;
            if (callback instanceof OnDrinkTextOptionsChanged)
                ((OnDrinkTextOptionsChanged) callback).onDescFontChange(itemDescFont);
            ((FontPreference) preference).setValue(itemDescFont);
        }
        return false;
    }

    public boolean prepareItemPriceVisibility(){
        if (item instanceof Drinktextable)
            return ((Drinktextable) item).isPriceVisible();
        return true;
    }

    public boolean onItemPriceVisibilityChange(Preference preference, Object newValue){
        if (newValue instanceof Boolean){
            boolean itemPriceVisible = ((boolean) newValue);
            if (callback instanceof OnDrinkTextOptionsChanged)
                ((OnDrinkTextOptionsChanged) callback).onPriceVisibleChange(itemPriceVisible);
            ((CheckBoxPreference) preference).setChecked(itemPriceVisible);
        }
        return false;
    }

    @ColorInt
    public int prepareItemPriceColor(){
        if (item instanceof Drinktextable)
            return ((Drinktextable) item).getPriceColor();
        return 0;
    }

    public boolean onItemPriceColorChange(Preference preference, Object newValue){
        if (newValue instanceof Integer){
            int itemPriceColor = ((int) newValue);
            if (itemPriceColor == 0) return false;
            if (callback instanceof OnDrinkTextOptionsChanged)
                ((OnDrinkTextOptionsChanged) callback).onPriceColorChange(itemPriceColor);
            ((ColorPreference) preference).setColor(itemPriceColor);
        }
        return false;
    }

    public int prepareItemPriceSize(){
        if (item instanceof Drinktextable)
            return ((Drinktextable) item).getPriceFontSize();
        return 0;
    }

    public boolean onItemPriceSizeChange(Preference preference, Object newValue){
        if (newValue instanceof Integer){
            int itemPriceSize = ((int) newValue);
            if (itemPriceSize == 0) return false;
            if (callback instanceof OnDrinkTextOptionsChanged)
                ((OnDrinkTextOptionsChanged) callback).onPriceSizeChange(itemPriceSize);
            ((SeekBarPreference) preference).setValue(itemPriceSize);
        }
        return false;
    }

    public Font prepareItemPriceFont(){
        if (item instanceof Drinktextable)
            return ((Drinktextable) item).getPriceFont();
        return null;
    }

    public boolean onItemPriceFontChange(Preference preference, Object newValue){
        if (newValue instanceof Font){
            Font itemPriceFont = ((Font) newValue);
            if (!itemPriceFont.isValid()) return false;
            if (callback instanceof OnDrinkTextOptionsChanged)
                ((OnDrinkTextOptionsChanged) callback).onPriceFontChange(itemPriceFont);
            ((FontPreference) preference).setValue(itemPriceFont);
        }
        return false;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private final OptionsEditorFragment caller;

        public SettingsFragment(OptionsEditorFragment caller) {
            this.caller = caller;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            if (caller.item instanceof Group)
                addPreferencesFromResource(R.xml.group_name_setting);
            if (caller.item instanceof Drink)
                addPreferencesFromResource(R.xml.drink_select_setting);
            if (caller.item instanceof Textable)
                addPreferencesFromResource(R.xml.text_setting);

            if (caller.item instanceof Backgroundable)
                addPreferencesFromResource(R.xml.background_setting);
            if (caller.item instanceof Group)
                addPreferencesFromResource(R.xml.group_settings);
            if (caller.item instanceof Drinktextable)
                addPreferencesFromResource(R.xml.drink_settings);
            if (caller.item instanceof Textable)
                addPreferencesFromResource(R.xml.text_style_settings);
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            EditTextPreference itemNamePreference = findPreference(KEY_ITEM_NAME);
            if (itemNamePreference != null){
                itemNamePreference.setText(caller.prepareItemName());
                itemNamePreference.setOnPreferenceChangeListener(caller::onItemNameChange);
            }

            DrinkPreference drinkPreference = findPreference(KEY_DRINK);
            if (drinkPreference != null){
                drinkPreference.setValue(caller.prepareDrink());
                drinkPreference.setOnPreferenceChangeListener(caller::onDrinkChange);
            }

            ColorPreference backgroundPreference = findPreference(KEY_ITEM_BACKGROUND);
            if (backgroundPreference != null){
                backgroundPreference.setColor(caller.prepareItemBackground());
                backgroundPreference.setOnPreferenceChangeListener(caller::onItemBackgroundChange);
            }

            SeekBarPreference cornerRadiusPreference = findPreference(KEY_CORNER_RADIUS);
            if (cornerRadiusPreference != null){
                cornerRadiusPreference.setUpdatesContinuously(true);
                cornerRadiusPreference.setValue(caller.prepareCornerRadius());
                cornerRadiusPreference.setOnPreferenceChangeListener(caller::onCornerRadiusChange);
            }

            SeekBarPreference columnCount = findPreference(KEY_COLUMN_COUNT);
            if (columnCount != null){
                columnCount.setUpdatesContinuously(true);
                columnCount.setValue(caller.prepareColumnCount());
                columnCount.setOnPreferenceChangeListener(caller::onColumnCountChange);
            }

            SeekBarPreference columnSpacing = findPreference(KEY_COLUMN_SPACING);
            if (columnSpacing != null){
                columnSpacing.setUpdatesContinuously(true);
                columnSpacing.setValue(caller.prepareColumnSpacing());
                columnSpacing.setOnPreferenceChangeListener(caller::onColumnSpacingChange);
            }

            SeekBarPreference rowSpacing = findPreference(KEY_ROW_SPACING);
            if (rowSpacing != null){
                rowSpacing.setUpdatesContinuously(true);
                rowSpacing.setValue(caller.prepareRowSpacing());
                rowSpacing.setOnPreferenceChangeListener(caller::onRowSpacingChange);
            }

            ColorPreference itemNameFontColor = findPreference(KEY_ITEM_NAME_COLOR);
            if (itemNameFontColor != null){
                itemNameFontColor.setColor(caller.prepareItemNameColor());
                itemNameFontColor.setOnPreferenceChangeListener(caller::onItemNameColorChange);
            }

            SeekBarPreference itemNameFontSize = findPreference(KEY_ITEM_NAME_SIZE);
            if (itemNameFontSize != null){
                itemNameFontSize.setUpdatesContinuously(true);
                itemNameFontSize.setValue(caller.prepareItemNameSize());
                itemNameFontSize.setOnPreferenceChangeListener(caller::onItemNameSizeChange);
            }

            FontPreference itemNameFont = findPreference(KEY_ITEM_NAME_FONT);
            if (itemNameFont != null){
                itemNameFont.setValue(caller.prepareItemNameFont());
                itemNameFont.setOnPreferenceChangeListener(caller::onItemNameFontChange);
            }

            ColorPreference itemDescFontColor = findPreference(KEY_ITEM_DESC_COLOR);
            if (itemDescFontColor != null){
                itemDescFontColor.setColor(caller.prepareItemDescColor());
                itemDescFontColor.setOnPreferenceChangeListener(caller::onItemDescColorChange);
            }

            SeekBarPreference itemDescFontSize = findPreference(KEY_ITEM_DESC_SIZE);
            if (itemDescFontSize != null){
                itemDescFontSize.setUpdatesContinuously(true);
                itemDescFontSize.setValue(caller.prepareItemDescSize());
                itemDescFontSize.setOnPreferenceChangeListener(caller::onItemDescSizeChange);
            }

            FontPreference itemDescFont = findPreference(KEY_ITEM_DESC_FONT);
            if (itemDescFont != null){
                itemDescFont.setValue(caller.prepareItemDescFont());
                itemDescFont.setOnPreferenceChangeListener(caller::onItemDescFontChange);
            }

            CheckBoxPreference itemPriceVisible = findPreference(KEY_ITEM_PRICE_VISIBLE);
            if (itemPriceVisible != null){
                itemPriceVisible.setChecked(caller.prepareItemPriceVisibility());
                itemPriceVisible.setOnPreferenceChangeListener(caller::onItemPriceVisibilityChange);
            }

            ColorPreference itemPriceFontColor = findPreference(KEY_ITEM_PRICE_COLOR);
            if (itemPriceFontColor != null){
                itemPriceFontColor.setColor(caller.prepareItemPriceColor());
                itemPriceFontColor.setOnPreferenceChangeListener(caller::onItemPriceColorChange);
            }

            SeekBarPreference itemPriceFontSize = findPreference(KEY_ITEM_PRICE_SIZE);
            if (itemPriceFontSize != null){
                itemPriceFontSize.setUpdatesContinuously(true);
                itemPriceFontSize.setValue(caller.prepareItemPriceSize());
                itemPriceFontSize.setOnPreferenceChangeListener(caller::onItemPriceSizeChange);
            }

            FontPreference itemPriceFont = findPreference(KEY_ITEM_PRICE_FONT);
            if (itemPriceFont != null){
                itemPriceFont.setValue(caller.prepareItemPriceFont());
                itemPriceFont.setOnPreferenceChangeListener(caller::onItemPriceFontChange);
            }
        }
    }

    public interface OnOptionsChanged{
        void onNameChanged(String name);
    }

    public interface OnDrinkChanged extends OnOptionsChanged{
        void onDrinkChanged(Drink newDrink);
    }

    public interface OnBackgroundOptionsChanged extends OnOptionsChanged{
        void onBackgroundChanged(@ColorInt int color);
        void onCornerRadiusChanged(int radius);
    }

    public interface OnGridOptionsChanged extends OnOptionsChanged{
        void onColumnCountChange(int columns);
        void onColumnSpacingChange(int space);
        void onRowSpacingChange(int space);
    }

    public interface OnTextOptionsChanged extends OnOptionsChanged{
        void onColorChange(@ColorInt int color);
        void onSizeChange(int size);
        void onFontChange(Font font);
    }

    public interface OnDrinkTextOptionsChanged extends OnOptionsChanged{
        void onNameColorChange(@ColorInt int color);
        void onNameSizeChange(int size);
        void onNameFontChange(Font font);
        void onDescColorChange(@ColorInt int color);
        void onDescSizeChange(int size);
        void onDescFontChange(Font font);
        void onPriceVisibleChange(boolean visible);
        void onPriceColorChange(@ColorInt int color);
        void onPriceSizeChange(int size);
        void onPriceFontChange(Font font);
    }
}
