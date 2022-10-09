package com.pl.donauturm.drinksmenu.controller.drinkmenu;

import androidx.annotation.Nullable;

import com.pl.donauturm.drinksmenu.controller.util.CloudState;
import com.pl.donauturm.drinksmenu.model.DrinksMenu;
import com.pl.donauturm.drinksmenu.model.DrinksMenuCloud;
import com.pl.donauturm.drinksmenu.model.DrinksMenuLocal;
import com.pl.donauturm.drinksmenu.util.RegistryMap;

public class DrinkMenuRegistry extends RegistryMap<DrinksMenu> {

    private static final DrinkMenuRegistry instance = new DrinkMenuRegistry();

    private DrinkMenuRegistry() {
    }

    public static DrinkMenuRegistry getInstance() {
        if (instance != null) return instance;
        else return new DrinkMenuRegistry();
    }


    @Nullable
    public DrinksMenu put(DrinksMenu drinksMenu) {
        if (containsValue(drinksMenu)) {
            DrinksMenu old = get(drinksMenu.getId());
            return checkBeforeReplace(old, drinksMenu);
        } else {
            drinksMenu = checkBeforePut(drinksMenu);
            Data.put(drinksMenu.getId(), drinksMenu);
            notifyAddition(drinksMenu.getId(), drinksMenu);
            return drinksMenu;
        }
    }

    private DrinksMenu checkBeforePut(DrinksMenu drinksMenu) {
        if (drinksMenu instanceof DrinksMenuLocal) {
            drinksMenu.setCloudState(CloudState.PULLING);
        }
        if (drinksMenu instanceof DrinksMenuCloud) {
            drinksMenu.setCloudState(CloudState.UP_TO_DATE);
        }
        return drinksMenu;
    }

    private DrinksMenu checkBeforeReplace(DrinksMenu older, DrinksMenu newer) {
        if (older == null) {
            remove(newer.getId());
            return put(newer.getId(), newer);
        }

        // if the older is a local and the newer is a cloud
        if (older instanceof DrinksMenuLocal && newer instanceof DrinksMenuCloud) {
            if (newer.hasGreaterVersionThan(older.getVersion())){
                newer.setCloudState(CloudState.UP_TO_DATE);
                replace(newer.getId(), newer);
                return newer;
            } else if (newer.hasGreaterVersionThan(((DrinksMenuLocal) older).getCloudVersion())) {
                //local and cloud have some changes, do to cloud update accept the cloud version
                //this behaviour may be changed in the future
                newer.setCloudState(CloudState.UP_TO_DATE);
                replace(newer.getId(), newer);
                return newer;
            } else if (older.hasGreaterVersionThan(newer.getVersion())) {
                //local is newer, set ready to push
                older.setCloudState(CloudState.READY_FOR_PUSH);
                return older;
            } else if (older.getVersion().equals(((DrinksMenuLocal) older).getCloudVersion())) {
                //local was uploaded, replace local with cloud if cloud is newer, set up to date
                if (!older.hasGreaterVersionThan(newer.getVersion())) {
                    //cloud is newer, replace local with cloud
                    newer.setCloudState(CloudState.UP_TO_DATE);
                    ((DrinksMenuLocal) older).setCloudVersion(newer.getVersion());
                    replace(newer.getId(), newer);
                    return newer;
                }
                older.setCloudState(CloudState.UP_TO_DATE);
                ((DrinksMenuLocal) older).setCloudVersion(newer.getVersion());
                return older;
            } else {
                //local is up to date with cloud
                older.setCloudState(CloudState.UP_TO_DATE);
                return older;

            }
        }

        // if the older is a local and the newer is a local
        if (older instanceof DrinksMenuLocal && newer instanceof DrinksMenuLocal) {

            if (newer.hasGreaterVersionThan(older.getVersion())) {
                ((DrinksMenuLocal) newer).setCloudVersion(((DrinksMenuLocal) older).getCloudVersion());
                newer.setCloudState(older.getCloudState());
                replace(newer.getId(), newer);
                return newer;
            }

            if (((DrinksMenuLocal) newer).hasGreaterCloudVersionThan(((DrinksMenuLocal) older).getCloudVersion())) {
                newer.setCloudState(older.getCloudState());
                replace(newer.getId(), newer);
                return newer;
            }

        }

        if (older instanceof DrinksMenuCloud && newer instanceof DrinksMenuLocal) {

            if (newer.hasGreaterVersionThan(older.getVersion())) {
                ((DrinksMenuLocal) newer).setCloudVersion(older.getVersion());
                newer.setCloudState(older.getCloudState());
                replace(newer.getId(), newer);
                return newer;
            }
            // comment this block if you want to keep the cloud version of the local menu
            if (newer.getVersion().equals(older.getVersion())){
                ((DrinksMenuLocal) newer).setCloudVersion(older.getVersion());
                newer.setCloudState(older.getCloudState());
                replace(newer.getId(), newer);
                return newer;
            }

        }

        // if the newer is more recent than the older
        if (newer.hasGreaterVersionThan(older.getVersion())) {
            newer.setCloudState(CloudState.UP_TO_DATE);
            replace(newer.getId(), newer);
            return newer;
        }

        return older;
    }

    @Override
    public boolean containsValue(@Nullable Object o) {
        if (o instanceof DrinksMenu) {
            DrinksMenu d = (DrinksMenu) o;
            return keySet().stream().anyMatch(key -> key.equals(d.getId()));
        }
        return super.containsValue(o);
    }

}
