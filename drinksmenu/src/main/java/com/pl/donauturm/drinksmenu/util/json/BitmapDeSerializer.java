package com.pl.donauturm.drinksmenu.util.json;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.pl.donauturm.drinksmenu.controller.util.api.DrinksMenuAPI;
import com.pl.donauturm.pisignageapi.requests.Request;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.UUID;

import static android.util.Base64.NO_WRAP;

public final class BitmapDeSerializer implements JsonSerializer<Bitmap>, JsonDeserializer<Bitmap> {

    private boolean ignored = false;
    private boolean byteArray = false;
    private boolean localFile = false;
    private boolean remoteFile = false;

    @Nullable
    private Object context;


    private BitmapDeSerializer() {
    }

    @Override
    public Bitmap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.getAsString() == null) return null;

        //MODE: ignored
        if (ignored) return null;

        //MODE: byte array
        if (byteArray) {
            byte[] byteArray = Base64.decode(json.getAsString(), Base64.NO_WRAP);
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }

        //MODE: local file
        if (localFile){
            try {
                File file = new File(json.getAsString());
                return BitmapFactory.decodeStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                Log.e(getClass().getSimpleName(), e.getMessage(), e);
            }
        }

        //MODE: remote file
        if (remoteFile) {
            DrinksMenuAPI api = (DrinksMenuAPI) context;
            return api.getBitmap(json.getAsString());
        }

        return null;
    }

    @Override
    public JsonElement serialize(Bitmap src, Type typeOfSrc, JsonSerializationContext context) {

        //MODE: ignored
        if (ignored) return null;

        //MODE: byte array
        if (byteArray) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            src.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            return new JsonPrimitive(Base64.encodeToString(byteArrayOutputStream.toByteArray(), NO_WRAP));
        }

        //MODE: local file
        if (localFile){
            ContextWrapper cw = new ContextWrapper((Context) this.context);

            File directory = cw.getCacheDir();
            File file = new File(directory, UUID.randomUUID().toString() + ".png");

            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
                src.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                return new JsonPrimitive(file.getAbsolutePath());
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), e.getMessage(), e);
            }
        }

        //MODE: remote file
        if (remoteFile){
            ContextWrapper cw = new ContextWrapper((Context) this.context);

            File directory = cw.getCacheDir();
            File file = new File(directory, UUID.randomUUID().toString() + ".png");

            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
                src.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), e.getMessage(), e);
                return null;
            }
            if (!(context instanceof DrinksMenuAPI)) return null;
            DrinksMenuAPI api = (DrinksMenuAPI) this.context;
            assert api != null;
            api.uploadAsset(file);
            return new JsonPrimitive(Request.PROTOCOL + "://" + Request.HOST + "/media/" + api.getUsername() + "/" + file.getName());
        }
        return null;
    }

    @NonNull
    public static BitmapDeSerializer toNull(){
        BitmapDeSerializer bds = new BitmapDeSerializer();
        bds.ignored = true;
        return bds;
    }

    @NonNull
    public static BitmapDeSerializer toByteArray(){
        BitmapDeSerializer bds = new BitmapDeSerializer();
        bds.byteArray = true;
        return bds;
    }

    @NonNull
    public static BitmapDeSerializer toLocalFile(Context context){
        BitmapDeSerializer bds = new BitmapDeSerializer();
        bds.localFile = true;
        bds.context = context;
        return bds;
    }

    @NonNull
    public static BitmapDeSerializer toWeb(DrinksMenuAPI api){
        BitmapDeSerializer bds = new BitmapDeSerializer();
        bds.remoteFile = true;
        bds.context = api;
        return bds;
    }
}
