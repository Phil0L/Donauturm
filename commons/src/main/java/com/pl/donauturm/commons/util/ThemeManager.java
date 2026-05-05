package com.pl.donauturm.commons.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class ThemeManager {
    private static ThemeManager INSTANCE;

    private static final long DURATION = 1000L;

    private final List<OnThemSwitch> subscriber;

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    private Theme currentTheme;

    public static ThemeManager getInstance() {
        if (INSTANCE == null) return new ThemeManager();
        return INSTANCE;
    }

    private ThemeManager() {
        INSTANCE = this;
        Calendar currentTime = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("Europe/Berlin")));
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        if (hour > 21 || hour < 7) currentTheme = Theme.DARK;
        else currentTheme = Theme.LIGHT;
        subscriber = new ArrayList<>();
    }

    public void instant(AppCompatActivity context, Theme newTheme) {
        this.currentTheme = newTheme;
        for (OnThemSwitch listener : subscriber) listener.onThemeSwitch(newTheme);
    }

    public void circularReveal(AppCompatActivity context, int posX, int posY, ImageView themeSwitchImageView, View drawerLayoutContainer) {
        circularReveal(context, posX, posY, themeSwitchImageView, drawerLayoutContainer, currentTheme == Theme.DARK ? Theme.LIGHT : Theme.DARK);
    }

    public void circularReveal(AppCompatActivity context, int posX, int posY, @NonNull ImageView themeSwitchImageView, View drawerLayoutContainer, Theme newTheme) {
        if (themeSwitchImageView.getVisibility() == View.VISIBLE) return;

        int[] pos = new int[]{posX, posY};
        int w = drawerLayoutContainer.getMeasuredWidth();
        int h = drawerLayoutContainer.getMeasuredHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawerLayoutContainer.draw(canvas);
        themeSwitchImageView.setImageBitmap(bitmap);
        themeSwitchImageView.setVisibility(View.VISIBLE);
        float finalRadius = (float) Math.max(Math.sqrt((w - pos[0]) * (w - pos[0]) + (h - pos[1]) * (h - pos[1])), Math.sqrt(pos[0] * pos[0] + (h - pos[1]) * (h - pos[1])));
        this.currentTheme = newTheme;
        for (OnThemSwitch listener : subscriber) listener.onThemeSwitch(newTheme);
        Animator anim = ViewAnimationUtils.createCircularReveal(drawerLayoutContainer, pos[0], pos[1], 0, finalRadius);
        anim.setDuration(DURATION);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                themeSwitchImageView.setImageDrawable(null);
                themeSwitchImageView.setVisibility(View.GONE);
            }
        });
        anim.start();
    }

    public void addThemeChangeListener(OnThemSwitch listener) {
        if (!subscriber.contains(listener)) {
            this.subscriber.add(listener);
            listener.onThemeSwitch(currentTheme);
        }
    }

    public void removeThemeChangeListener(OnThemSwitch listener) {
        this.subscriber.remove(listener);
    }

    public interface OnThemSwitch {
        void onThemeSwitch(Theme theme);
    }

    public enum Theme {
        LIGHT,
        DARK
    }

    public static class ThemeColor {

        @ColorInt
        public static int getColor(@NonNull Context context, Theme theme, @ColorRes int lightColor, @ColorRes int darkColor) {
            if (theme == Theme.DARK) return context.getResources().getColor(darkColor, context.getTheme());
            return context.getResources().getColor(lightColor, context.getTheme());
        }

        @ColorInt
        public static int getColor(@NonNull Context context, @AttrRes int styleColor) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = context.getTheme();
            theme.resolveAttribute(styleColor, typedValue, true);
            switch (typedValue.type){
                case TypedValue.TYPE_STRING:
                    return context.getResources().getColor(typedValue.resourceId, context.getTheme());
                case TypedValue.TYPE_INT_COLOR_ARGB8:
                case TypedValue.TYPE_INT_COLOR_RGB8:
                default:
                    return typedValue.data;

            }
        }

        @NonNull
        public static ColorStateList getColorStateList(Context context, Theme theme, @ColorRes int lightColor, @ColorRes int darkColor) {
            if (theme == Theme.DARK) return context.getResources().getColorStateList(darkColor, context.getTheme());
            return context.getResources().getColorStateList(lightColor, context.getTheme());
        }
    }

}

