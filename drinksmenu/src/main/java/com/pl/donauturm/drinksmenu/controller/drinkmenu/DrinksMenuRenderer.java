package com.pl.donauturm.drinksmenu.controller.drinkmenu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.generator.DrinkGenerator;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.generator.DrinkGroupGenerator;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.generator.ShapeGenerator;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.generator.TextGenerator;
import com.pl.donauturm.drinksmenu.model.DrinksMenu;
import com.pl.donauturm.drinksmenu.model.content.DrinksMenuItem;
import com.pl.donauturm.drinksmenu.model.content.DrinkItem;
import com.pl.donauturm.drinksmenu.model.content.DrinkGroupItem;
import com.pl.donauturm.drinksmenu.model.content.ShapeItem;
import com.pl.donauturm.drinksmenu.model.content.TextItem;
import com.pl.donauturm.drinksmenu.util.DrinksMenuCanvas;
import com.pl.donauturm.drinksmenu.view.layouts.PreviewHolder;
import com.pl.donauturm.drinksmenu.view.views.DrinkGroupView;
import com.pl.donauturm.drinksmenu.view.views.DrinkView;
import com.pl.donauturm.drinksmenu.view.views.ShapeView;
import com.pl.donauturm.drinksmenu.view.views.TextView;


public class DrinksMenuRenderer {

    @Deprecated
    public Bitmap renderSyncFromMenu(Context context, @NonNull DrinksMenu menu) {
        FrameLayout rootLayout = ((AppCompatActivity) context).findViewById(android.R.id.content);
        PreviewHolder frameLayout = new PreviewHolder(context);
        frameLayout.setLayoutParams(new ConstraintLayout.LayoutParams(menu.getWidth(), menu.getHeight()));
        frameLayout.forceLayout();
        frameLayout.requestLayout();
        frameLayout.invalidate();
        rootLayout.addView(frameLayout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ImageView bg = addBackground(frameLayout, menu.getBackGround());
        addContent(frameLayout, menu, bg);
        frameLayout.forceLayout();
        frameLayout.requestLayout();
        frameLayout.invalidate();
        frameLayout.setVisibility(View.VISIBLE);
        Bitmap image = renderFromView(frameLayout, menu.getWidth(), menu.getHeight());

        menu.provideMenuImage(image);
        return image;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void renderFromMenu(Context context, @NonNull DrinksMenu menu, Rendered rendered) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            // create frame
            PreviewHolder frameLayout = new PreviewHolder(context);
            frameLayout.setVisibility(View.INVISIBLE);
            frameLayout.setLayoutParams(new ConstraintLayout.LayoutParams(menu.getWidth(), menu.getHeight()));
            FrameLayout rootLayout = ((AppCompatActivity) context).findViewById(android.R.id.content);

            rootLayout.addView(frameLayout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            rootLayout.invalidate();
            frameLayout.invalidate();
            frameLayout.post(() -> {

                // wait a tick to place the frame before adding content
                ImageView bg = addBackground(frameLayout, menu.getBackGround());
                frameLayout.post(() -> {
                    addContent(frameLayout, menu, bg);
                    frameLayout.setVisibility(View.INVISIBLE);
                    frameLayout.post(() -> {

                        // save frame a tick later
                        Bitmap image = renderFromView(frameLayout);
                        frameLayout.setOnTouchListener((v, event) -> {
                            rootLayout.removeView(v);
                            return true;
                        });
                        rootLayout.removeView(frameLayout);
                        menu.provideMenuImage(image);
                        rendered.renderFinished(image);
                    });
                });
            });
        });
    }

    @SuppressWarnings("unused")
    public void renderAsyncFromMenu(Context context, @NonNull DrinksMenu menu, Rendered rendered) {
        //TODO implement async
    }

    public Bitmap renderFromView(@NonNull PreviewHolder frame) {
        Bitmap b = Bitmap.createBitmap(frame.getWidth(), frame.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new DrinksMenuCanvas(b);
        frame.draw(c);
        return b;
    }

    @Deprecated
    public Bitmap renderFromView(@NonNull PreviewHolder frame, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        frame.drawNoGuides(c);
        c.save();
        return b;
    }

    public void renderAsyncFromView(PreviewHolder frame, Rendered rendered) {
        new Thread(() -> rendered.renderFinished(renderFromView(frame))).start();
    }

    private void addContent(FrameLayout frame, @NonNull DrinksMenu menu, ImageView bg) {
        for (DrinksMenuItem item : menu.getItems()) {
            if (item instanceof DrinkGroupItem)
                addDrinkGroup(frame, (DrinkGroupItem) item, bg);
            if (item instanceof DrinkItem)
                addDrink(frame, (DrinkItem) item, bg);
            if (item instanceof TextItem)
                addText(frame, ((TextItem) item), bg);
            if (item instanceof ShapeItem)
                addShape(frame, (ShapeItem) item, bg);
        }
    }

    private ImageView addBackground(@NonNull FrameLayout frame, Bitmap background) {
        ImageView imageView = new ImageView(frame.getContext());
        imageView.setImageBitmap(background);
        imageView.setAdjustViewBounds(true);
        frame.addView(imageView, frame.getLayoutParams());
        return imageView;
    }

    private void addDrinkGroup(FrameLayout frame, DrinkGroupItem drinkGroupItem, ImageView bg) {
        DrinkGroupGenerator drinkGroupGenerator = new DrinkGroupGenerator();
        DrinkGroupView drinkGroupView = drinkGroupGenerator.generateNewImageDrinkGroup(frame, drinkGroupItem, bg);
        drinkGroupView.setItem(drinkGroupItem);
        DrinkGroupView.GridAdapter adapter = drinkGroupView.newGridAdapter();
        drinkGroupView.setAdapter(adapter);
    }

    private void addDrink(FrameLayout frame, DrinkItem drinkItem, ImageView bg) {
        DrinkGenerator drinkGroupGenerator = new DrinkGenerator();
        DrinkView drinkView = drinkGroupGenerator.generateNewImageDrink(frame, drinkItem, bg);
        drinkView.setItem(drinkItem);
        DrinkView.SingleAdapter adapter = drinkView.newSingleAdapter();
        drinkView.setAdapter(adapter);
    }

    private void addText(FrameLayout frame, TextItem textItem, ImageView bg) {
        TextGenerator textGenerator = new TextGenerator();
        TextView textView = textGenerator.generateNewImageText(frame, textItem, bg);
        textView.setItem(textItem);
        TextView.SingleAdapter adapter = textView.newSingleAdapter();
        textView.setAdapter(adapter);
    }

    private void addShape(FrameLayout frame, ShapeItem shapeItem, ImageView bg) {
        ShapeGenerator shapeGenerator = new ShapeGenerator();
        ShapeView shapeView = shapeGenerator.generateNewImageShape(frame, shapeItem, bg);
        shapeView.setItem(shapeItem);
    }

    public interface Rendered {
        void renderFinished(Bitmap bitmap);
    }
}
