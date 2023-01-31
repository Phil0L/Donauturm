package com.pl.donauturm.drinksmenu.model;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.AnyThread;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.controller.util.api.DrinksMenuAPI;
import com.pl.donauturm.pisignageapi.model.Asset;
import com.pl.donauturm.pisignageapi.model.Notice;
import com.pl.donauturm.pisignageapi.model.files.messages.NoticeUploadMessage;
import com.pl.donauturm.pisignageapi.requests.Request;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class DrinksMenuCloud extends DrinksMenu implements Serializable {

    public static final String JSON_PREFIX = "data:";
    public static final String ASSET_TYPE_NOTICE = "notice";
    public static final String LABEL_GENERATED = "generated";
    public static final String LABEL_BACKGROUND = "background";
    public static final String LABEL_DRINKS_MENU = "drink-menu";
    public static final String LABEL_DRINKS_MENU_IMAGE = "drink-menu-image";
    public static final String NAMING_DATA_FILETYPE = /*name*/".html";
    public static final String NAMING_BACKGROUND_FILETYPE = /*id*/"bg.png";
    public static final String NAMING_IMAGE_FILETYPE = /*name*/".png";

    protected String backgroundName;
    protected String imageName;

    private transient DrinksMenuAPI api;

    // CONSTRUCTING //

    @Keep // for Gson parsing
    @SuppressWarnings("unused")
    public DrinksMenuCloud() {
        super();
    }

    public DrinksMenuCloud(@NonNull DrinksMenu source) {
        super(source);
        this.backgroundName = friendly(id) + NAMING_BACKGROUND_FILETYPE;
        this.imageName = friendly(name) + NAMING_IMAGE_FILETYPE;
    }

    public DrinksMenuCloud(@NonNull DrinksMenu source, DrinksMenuAPI api) {
        super(source);
        this.provideApi(api);
        this.backgroundName = friendly(id) + NAMING_BACKGROUND_FILETYPE;
        this.imageName = friendly(name) + NAMING_IMAGE_FILETYPE;
    }

    public void provideApi(DrinksMenuAPI api) {
        if (api != null)
            this.api = api;
    }

    // GETTERS AND SETTERS //

    public String getBackgroundName() {
        return backgroundName;
    }

    public String getImageName() {
        return imageName;
    }

    public String getBackgroundUrl() {
        return Request.PROTOCOL + "://" + Request.HOST + "/media/" + Request.SUBDOMAIN + "/" + backgroundName;
    }

    public String getImageUrl() {
        return Request.PROTOCOL + "://" + Request.HOST + "/media/" + Request.SUBDOMAIN + "/" + imageName;
    }

    // MANAGING //

    @Override
    @WorkerThread
    public void save(@NonNull Context context) {
        // do some checks then call #upload()
        if (!hasBackGroundLoaded()) return;
        if (!hasMenuImageLoaded()) return;
        upload(context);
    }

    @WorkerThread
    private void upload(@NonNull Context context) {
        api.asynchronous.createNotice(new NoticeUploadMessage(
                friendly(name),
                JSON_PREFIX + serializer().toJson(this), context.getString(R.string.do_not_remove_notice),
                LABEL_GENERATED, LABEL_DRINKS_MENU), null);
        uploadMenuImage(context);
        uploadBackGround(context);
    }

    @WorkerThread
    private void uploadMenuImage(@NonNull Context context) {
        assert getMenuImage() != null; // checked in #save()
        File f = bitmapToFile(getMenuImage(), context, imageName);
        api.asynchronous.uploadAsset(f).withLabels(LABEL_GENERATED, LABEL_DRINKS_MENU_IMAGE).upload();
    }

    @WorkerThread
    private void uploadBackGround(@NonNull Context context) {
        assert getBackGround() != null; // checked in #save()
        File f = bitmapToFile(getBackGround(), context, backgroundName);
        api.asynchronous.uploadAsset(f).withLabels(LABEL_GENERATED, LABEL_BACKGROUND).upload();
    }

    @Override
    @AnyThread
    public void delete(@Nullable Context context) {
        api.asynchronous.deleteNotice(friendly(name) + NAMING_DATA_FILETYPE, null);
        api.asynchronous.deleteAsset(friendly(id) + NAMING_BACKGROUND_FILETYPE, null);
        api.asynchronous.deleteAsset(friendly(name) + NAMING_IMAGE_FILETYPE, null);
    }

    @Override
    @AnyThread
    public void requireBackGround(@NonNull Context context, @Nullable OnBitmapLoadedListener callback) {
        final Bitmap bg;
        if ((bg = getBackGround()) != null && bg.getHeight() > 1 && bg.getWidth() > 1 && callback != null){
            callback.onBitmapLoaded(bg);
            return;
        }
        api.asynchronous.getBitmap(getBackgroundUrl(), (bitmap) -> {
            provideBackGround(bitmap);
            if (callback != null)
                callback.onBitmapLoaded(bitmap);
        });
    }

    @Override
    @AnyThread
    public void requireMenuImage(@NonNull Context context, @Nullable OnBitmapLoadedListener callback) {
        final Bitmap im;
        if ((im = getMenuImage()) != null && im.getHeight() > 1 && im.getWidth() > 1 && callback != null){
            callback.onBitmapLoaded(im);
            return;
        }
        api.asynchronous.getBitmap(getImageUrl(), (bitmap) -> {
            provideMenuImage(bitmap);
            if (callback != null)
                callback.onBitmapLoaded(bitmap);
        });
    }

    @NonNull
    public static List<String> findDMJsonInAssets(@NonNull List<Asset> assets, @NonNull DrinksMenuAPI api) {
        List<Asset> notices = assets.stream().filter(a -> a.getType().equals(ASSET_TYPE_NOTICE) && a.getLabels().contains(LABEL_DRINKS_MENU)).collect(Collectors.toList());
        List<String> json = new ArrayList<>();
        for (Asset noticeA : notices) {
            Notice notice = api.getNotice(noticeA.getName());
            String description = notice.getDescription();
            if (description.startsWith(JSON_PREFIX)) {
                json.add(description.substring(5));
            }
        }
        return json;
    }

    // PRIVATE HELPERS //

    @Nullable
    @WorkerThread
    private File bitmapToFile(@NonNull Bitmap bitmap, @NonNull Context context, @NonNull String filename) {
        try {
            File f = new File(context.getCacheDir(), filename);
            boolean created = f.createNewFile();

            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapData = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
            return f;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    @NonNull
    @AnyThread
    private String friendly(@NonNull String s) {
        return s.replace("ä", "ae")
                .replace("ö", "oe")
                .replace("ü", "ue")
                .replace("ß", "ss")
                .replace("Ä", "Ae")
                .replace("Ö", "Oe")
                .replace("Ü", "Ue")
                .replace(" ", "_");
    }
}
