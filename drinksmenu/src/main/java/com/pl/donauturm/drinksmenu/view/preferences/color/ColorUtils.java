package com.pl.donauturm.drinksmenu.view.preferences.color;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Kizito Nwose on 9/28/2016.
 */
public class ColorUtils {

    public static void setColorViewValue(ImageView imageView, int color, ColorPreference.ColorShape shape) {
        Resources res = imageView.getContext().getResources();

        Drawable currentDrawable = imageView.getDrawable();
        GradientDrawable colorChoiceDrawable;
        if (currentDrawable instanceof GradientDrawable) {
            // Reuse drawable
            colorChoiceDrawable = (GradientDrawable) currentDrawable;
        } else {
            colorChoiceDrawable = new GradientDrawable();
            colorChoiceDrawable.setShape(shape == ColorPreference.ColorShape.SQUARE ? GradientDrawable.RECTANGLE : GradientDrawable.OVAL);
        }

        // Set stroke to dark version of color
        int darkenedColor = Color.rgb(
                Color.red(color) * 192 / 256,
                Color.green(color) * 192 / 256,
                Color.blue(color) * 192 / 256);

        colorChoiceDrawable.setColor(color);
        colorChoiceDrawable.setStroke((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, res.getDisplayMetrics()), darkenedColor);

        Drawable drawable = colorChoiceDrawable;
        imageView.setImageDrawable(drawable);
    }

    public static void showDialog(AppCompatActivity context, ColorDialog.OnColorSelectedListener listener, String tag, ColorPreference preference) {
        ColorDialog fragment = ColorDialog.newInstance(preference);
        fragment.setOnColorSelectedListener(listener);
        fragment.show(context.getSupportFragmentManager(), tag);
    }

    public static void attach(AppCompatActivity context, ColorDialog.OnColorSelectedListener listener, String tag) {
            ColorDialog fragment = (ColorDialog) context.getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment != null) {
                // re-bind preference to fragment
                fragment.setOnColorSelectedListener(listener);
            }

    }


}
