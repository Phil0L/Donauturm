package com.pl.donauturm.drinksmenu.model;

import android.content.Context;
import android.graphics.Typeface;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.Objects;

public class Font {

    private String fontName;
    private final File file;
    private Typeface loadedTypeFace;
    private final Context context;
    private final int resourceID;

    public Font(File file) {
        this.file = file;
        this.fontName = parseFontName(file);
        this.resourceID = 0;
        this.context = null;
    }

    public Font(File file, int resourceId, Context context) {
        this.file = file;
        this.fontName = parseFontName(file);
        this.resourceID = resourceId;
        this.context = context;
    }

    public boolean isValid() {
        return fontName != null && !fontName.isEmpty() && file != null;
    }

    private String parseFontName(File ttfFile) {
        String fileName = ttfFile.getName();
        if (!fileName.contains(".")) {
            return fileName;
        }
        String fontName = fileName.split("\\.")[0];
        String fileType = fileName.split("\\.")[1];
        if (fileType.equals("ttf"))
            return fontName;
        return null;
    }

    public String getFontName() {
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 0; i < fontName.length(); i++) {
            String letter = String.valueOf(fontName.charAt(i));
            String nextLetter = i + 1 >= fontName.length() ? null : String.valueOf(fontName.charAt(i + 1));
            if (i == 0) {
                nameBuilder.append(letter.toUpperCase());
                continue;
            }
            if (letter.matches("[_\\-\\W]")) {
                nameBuilder.append(" ");
                if (nextLetter != null) {
                    nameBuilder.append(nextLetter.toUpperCase());
                    i++;
                }
                continue;
            }
            nameBuilder.append(letter);
        }
        fontName = nameBuilder.toString();
        return getFontNameFast();
    }

    public String getFontNameFast() {
        return fontName;
    }

    public File getFile() {
        return file;
    }

    public Typeface getTypeFace() {
        if (loadedTypeFace == null) {
            if (resourceID != 0 && context != null) {
                try {
                    loadedTypeFace = context.getResources().getFont(resourceID);
                    return loadedTypeFace;
                } catch (Exception ignored) {
                }
            }
            try {
                loadedTypeFace = Typeface.createFromFile(file);
                return loadedTypeFace;
            } catch (Exception ignored) {
            }
        }
        return loadedTypeFace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Font font = (Font) o;
        return Objects.equals(fontName, font.fontName) &&
                file.equals(font.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file);
    }

    @NonNull
    @Override
    public String toString() {
        return "Font{" +
                "fontName='" + fontName + '\'' +
                '}';
    }
}
