package com.pl.donauturm.drinksmenu.view.preferences.font;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.model.Font;

import java.util.List;

public class FontPreference extends Preference implements FontDialog.OnFontSelectedListener {

    private List<Font> fonts;
    private Font currentFont;

    // Font adaptor responsible for redrawing the item TextView with the appropriate font.
    // We use BaseAdapter since we need both arrays, and the effort is quite small.
    public FontPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWidgetLayoutResource(R.layout.pref_font_layout);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        TextView fontView = (TextView) holder.findViewById(R.id.font_view);
        if (fontView != null && currentFont != null) {
            fontView.setText(currentFont.getFontName());
            fontView.setTypeface(currentFont.getTypeFace());
        }
        if (fontView != null && currentFont == null) {
            fontView.setText("Select font");
        }
    }

    @Override
    protected void onClick() {
        super.onClick();
        FontDialog fragment = FontDialog.newInstance(this);
        fragment.setOnColorSelectedListener(this);
        fragment.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "font_" + getKey());
    }

    public Font getValue() {
        if (currentFont == null) loadDefaultFont();
        return currentFont;
    }

    private void loadDefaultFont() {
        currentFont = getFonts().stream().filter(
                f -> f.getFontNameFast().toLowerCase().contains("roboto") && f.getFontNameFast().toLowerCase().contains("regular"))
                .findFirst().orElse(null);
    }

    public void setValue(Font font) {
        if (font != null)
            currentFont = font;
    }

    public List<Font> getFonts() {
        if (fonts == null) loadFonts();
        return fonts;
    }

    private void loadFonts() {
        fonts = FontUtils.enumerateFonts(getContext());
    }

    @Override
    public void onFontSelected(Font font) {
        currentFont = font;
        notifyChanged();
        callChangeListener(font);
    }

}
