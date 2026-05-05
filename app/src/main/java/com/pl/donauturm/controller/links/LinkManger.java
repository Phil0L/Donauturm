package com.pl.donauturm.controller.links;

import android.content.Context;
import android.view.MenuItem;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

public class LinkManger {

    private static final List<Class<?>> applicationLinkClasses = Arrays.asList(
            CamerasLink.class,
            AlarmLink.class
    );

    public boolean check(MenuItem item, Context context){
        Link link = getApplicationLinkClassForId(item.getItemId());
        if (link != null){
            link.open(context);
            return true;
        }
        return false;
    }

    private List<Class<?>> getApplicationLinkClasses(){
        return applicationLinkClasses;
    }

    @Nullable
    private Link getApplicationLinkClassForId(int id){
        for (Class<?> clazz : getApplicationLinkClasses()){
            if (clazz == null) continue;
            try {
                Link link = (Link) clazz.newInstance();
                if (link.getNavItemId() == id){
                    return link;
                }
            } catch (IllegalAccessException | InstantiationException | ClassCastException ignored) {
            }
        }
        return null;
    }

}
