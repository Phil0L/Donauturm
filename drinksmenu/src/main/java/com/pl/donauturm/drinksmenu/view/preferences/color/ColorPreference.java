package com.pl.donauturm.drinksmenu.view.preferences.color;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.Keep;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.pl.donauturm.drinksmenu.R;

import java.io.Serializable;


public class ColorPreference extends Preference implements ColorDialog.OnColorSelectedListener {
    private int value = 0;
    private ColorShape colorShape = ColorShape.CIRCLE;
    private PreviewSize previewSize = PreviewSize.NORMAL;

    @Keep
    public ColorPreference(Context context) {
        super(context);
        initAttrs(null, 0);
    }

    @Keep
    public ColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs, 0);
    }

    @Keep
    public ColorPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs, defStyle);
    }

    private void initAttrs(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs, R.styleable.ColorPreference, defStyle, defStyle);
        try {
            colorShape = ColorShape.getShape(a.getInteger(R.styleable.ColorPreference_colorShape, 1));
            previewSize = PreviewSize.getSize(a.getInteger(R.styleable.ColorPreference_viewSize, 1));
        } finally {
            a.recycle();
        }

        @LayoutRes int itemLayoutId = R.layout.pref_color_layout;
        @LayoutRes int itemLayoutLargeId = R.layout.pref_color_layout_large;
        setWidgetLayoutResource(previewSize == PreviewSize.NORMAL ? itemLayoutId : itemLayoutLargeId);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        ImageView colorView = (ImageView) holder.findViewById(R.id.color_view);
        if (colorView != null) {
            ColorUtils.setColorViewValue(colorView, value, colorShape);
        }
    }

    public void setColor(@ColorInt int value) {
        this.value = value;
        notifyChanged();
    }

    @Override
    protected void onClick() {
        super.onClick();
        ColorUtils.showDialog((AppCompatActivity) getContext(), this, getFragmentTag(), this);
    }

    @Override
    public void onAttached() {
        super.onAttached();
        //helps during activity re-creation
        ColorUtils.attach((AppCompatActivity) getContext(), this, getFragmentTag());
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, getContext().getColor(R.color.teal_200));
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        setColor(defaultValue == null ? getContext().getColor(R.color.teal_200) : (Integer) defaultValue);
    }

    public String getFragmentTag() {
        return "color_" + getKey();
    }

    public int getValue() {
        return value;
    }

    @Override
    public void onColorSelected(int newColor) {
        setColor(newColor);
        callChangeListener(newColor);
    }

    public enum ColorShape implements Serializable {
        CIRCLE, SQUARE;

        public static ColorShape getShape(int num) {
            if (num == 2)
                return SQUARE;
            return CIRCLE;
        }
    }

    public enum PreviewSize {
        NORMAL, LARGE;

        public static PreviewSize getSize(int num) {
            if (num == 2)
                return LARGE;
            return NORMAL;
        }
    }


}
