package com.pl.donauturm.drinksmenu.view.preferences.font;

import android.content.Context;
import android.util.Xml;

import androidx.annotation.NonNull;

import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.model.Font;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FontUtils {

    private static List<Font> cachedFonts;

    // This function enumerates all fonts on Android system and returns the HashMap with the font
    // absolute file name as key, and the font literal name (embedded into the font) as value.
    @NonNull
    public static List<Font> enumerateFonts(Context context) {
        if (cachedFonts != null && !cachedFonts.isEmpty()) {
            return new ArrayList<>(cachedFonts);
        }

        String[] fontDirs = {"/system/fonts", "/system/font", "/data/fonts", "res/font", "default"};
        List<Font> fonts = new ArrayList<>();
        for (String fontDir : fontDirs) {
            if (fontDir.equals("res/font")) {
                List<Font> assetFonts = loadFontAssets(context);
                assetFonts.forEach(af -> {
                    if (!fonts.contains(af))
                        fonts.add(af);
                });
            }
            if (fontDir.equals("default")) {
                List<Font> systemFonts = FontListParser.safelyGetSystemFonts();
                systemFonts.forEach(af -> {
                    if (!fonts.contains(af))
                        fonts.add(af);
                });
            }
            File dir = new File(fontDir);
            if (!dir.exists()) continue;
            File[] files = dir.listFiles();
            if (files == null) continue;
            for (File file : files) {
                Font font = new Font(file);
                if (font.isValid() && !fonts.contains(font))
                    fonts.add(font);
            }
        }
        fonts.sort((o1, o2) -> o1.getFontNameFast().compareToIgnoreCase(o2.getFontNameFast()));
        cachedFonts = new ArrayList<>(fonts);
        return fonts;
    }

    private static List<Font> loadFontAssets(Context context) {
        final Class<R.font> c = R.font.class;
        final Field[] fields = c.getDeclaredFields();
        final List<Font> fonts = new ArrayList<>();

        for (Field field : fields) {
            final int resourceId;
            try {
                String fieldName = field.getName();
                String fileName = "android.resource://" + context.getApplicationInfo().packageName + "/font/" + fieldName;
                resourceId = field.getInt(null);
                Font font = new Font(new File(fileName), resourceId, context);
                if (!fonts.contains(font))
                    fonts.add(font);
            } catch (Exception ignored) {
            }
        }
        return fonts;
    }


    public static final class FontListParser {

        private static final File FONTS_XML = new File("/system/etc/fonts.xml");
        private static final File SYSTEM_FONTS_XML = new File("/system/etc/system_fonts.xml");

        public static List<com.pl.donauturm.drinksmenu.model.Font> getSystemFonts() throws Exception {
            String fontsXml;
            if (FONTS_XML.exists()) {
                fontsXml = FONTS_XML.getAbsolutePath();
            } else if (SYSTEM_FONTS_XML.exists()) {
                fontsXml = SYSTEM_FONTS_XML.getAbsolutePath();
            } else {
                throw new RuntimeException("fonts.xml does not exist on this system");
            }
            Config parser = parse(new FileInputStream(fontsXml));
            List<String> fonts = new ArrayList<>();

            for (Family family : parser.families) {
                if (family.name != null) {
                    if (family.fonts.isEmpty())
                        continue;
                    Font font = null;
                    for (Font f : family.fonts) {
                        font = f;
                        if (f.weight == 400) {
                            break;
                        }
                    }
                    fonts.add(font.fontName);
                }
            }

            for (Alias alias : parser.aliases) {
                if (alias.name == null || alias.toName == null || alias.weight == 0) {
                    continue;
                }
                for (Family family : parser.families) {
                    if (family.name == null || !family.name.equals(alias.toName)) {
                        continue;
                    }
                    for (Font font : family.fonts) {
                        if (font.weight == alias.weight) {
                            fonts.add(font.fontName);
                            break;
                        }
                    }
                }
            }

            List<com.pl.donauturm.drinksmenu.model.Font> fontS = new ArrayList<>();
            for (String systemFont : fonts) {
                File file = new File(systemFont);
                if (file.exists()) {
                    com.pl.donauturm.drinksmenu.model.Font font = new com.pl.donauturm.drinksmenu.model.Font(file);
                    if (!fontS.contains(font))
                        fontS.add(font);
                }
            }

            return fontS;
        }

        public static List<com.pl.donauturm.drinksmenu.model.Font> safelyGetSystemFonts() {
            try {
                return getSystemFonts();
            } catch (Exception e) {
                String[][] defaultSystemFonts = {
                        {
                                "cursive", "DancingScript-Regular.ttf"
                        }, {
                        "monospace", "DroidSansMono.ttf"
                }, {
                        "sans-serif", "Roboto-Regular.ttf"
                }, {
                        "sans-serif-light", "Roboto-Light.ttf"
                }, {
                        "sans-serif-medium", "Roboto-Medium.ttf"
                }, {
                        "sans-serif-black", "Roboto-Black.ttf"
                }, {
                        "sans-serif-condensed", "RobotoCondensed-Regular.ttf"
                }, {
                        "sans-serif-thin", "Roboto-Thin.ttf"
                }, {
                        "serif", "NotoSerif-Regular.ttf"
                }
                };
                List<com.pl.donauturm.drinksmenu.model.Font> fonts = new ArrayList<>();
                for (String[] names : defaultSystemFonts) {
                    File file = new File("/system/fonts", names[1]);
                    if (file.exists()) {
                        fonts.add(new com.pl.donauturm.drinksmenu.model.Font(file));
                    }
                }
                return fonts;
            }
        }

        /* Parse fallback list (no names) */
        public static Config parse(InputStream in) throws XmlPullParserException, IOException {
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(in, null);
                parser.nextTag();
                return readFamilies(parser);
            } finally {
                in.close();
            }
        }

        private static Alias readAlias(XmlPullParser parser) throws XmlPullParserException, IOException {
            Alias alias = new Alias();
            alias.name = parser.getAttributeValue(null, "name");
            alias.toName = parser.getAttributeValue(null, "to");
            String weightStr = parser.getAttributeValue(null, "weight");
            if (weightStr == null) {
                alias.weight = 0;
            } else {
                alias.weight = Integer.parseInt(weightStr);
            }
            skip(parser); // alias tag is empty, ignore any contents and consume end tag
            return alias;
        }

        private static Config readFamilies(XmlPullParser parser) throws XmlPullParserException, IOException {
            Config config = new Config();
            parser.require(XmlPullParser.START_TAG, null, "familyset");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                if (parser.getName().equals("family")) {
                    config.families.add(readFamily(parser));
                } else if (parser.getName().equals("alias")) {
                    config.aliases.add(readAlias(parser));
                } else {
                    skip(parser);
                }
            }
            return config;
        }

        private static Family readFamily(XmlPullParser parser) throws XmlPullParserException, IOException {
            String name = parser.getAttributeValue(null, "name");
            String lang = parser.getAttributeValue(null, "lang");
            String variant = parser.getAttributeValue(null, "variant");
            List<Font> fonts = new ArrayList<>();
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String tag = parser.getName();
                if (tag.equals("font")) {
                    String weightStr = parser.getAttributeValue(null, "weight");
                    int weight = weightStr == null ? 400 : Integer.parseInt(weightStr);
                    boolean isItalic = "italic".equals(parser.getAttributeValue(null, "style"));
                    String filename = parser.nextText();
                    String fullFilename = "/system/fonts/" + filename;
                    fonts.add(new Font(fullFilename, weight, isItalic));
                } else {
                    skip(parser);
                }
            }
            return new Family(name, fonts, lang, variant);
        }

        private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
            int depth = 1;
            while (depth > 0) {
                switch (parser.next()) {
                    case XmlPullParser.START_TAG:
                        depth++;
                        break;
                    case XmlPullParser.END_TAG:
                        depth--;
                        break;
                }
            }
        }

        private FontListParser() {

        }

        public static class Alias {

            public String name;

            public String toName;

            public int weight;
        }

        public static class Config {

            public List<Alias> aliases;

            public List<Family> families;

            Config() {
                families = new ArrayList<>();
                aliases = new ArrayList<>();
            }

        }

        public static class Family {

            public List<Font> fonts;

            public String lang;

            public String name;

            public String variant;

            public Family(String name, List<Font> fonts, String lang, String variant) {
                this.name = name;
                this.fonts = fonts;
                this.lang = lang;
                this.variant = variant;
            }

        }

        public static class Font {

            public String fontName;

            public boolean isItalic;

            public int weight;

            Font(String fontName, int weight, boolean isItalic) {
                this.fontName = fontName;
                this.weight = weight;
                this.isItalic = isItalic;
            }

        }

    }
}
