package com.pl.donauturm.drinksmenu.controller.drinkmenu;

import androidx.annotation.Nullable;

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
            notifyAddition(drinksMenu.getName(), drinksMenu);
            return drinksMenu;
        }
    }

    private DrinksMenu checkBeforePut(DrinksMenu drinksMenu) {
        if (drinksMenu instanceof DrinksMenuLocal) {
            drinksMenu.setCloudState(DrinksMenu.CloudState.PULLING);
        }
        if (drinksMenu instanceof DrinksMenuCloud) {
            drinksMenu.setCloudState(DrinksMenu.CloudState.UP_TO_DATE);
        }
        return drinksMenu;
    }

    private DrinksMenu checkBeforeReplace(DrinksMenu older, DrinksMenu newer) {
        if (older == null) {
            remove(newer.getName());
            return put(newer.getName(), newer);
        }

        // if the older is a local and the newer is a cloud
        if (older instanceof DrinksMenuLocal && newer instanceof DrinksMenuCloud) {
            if (older.getVersion().equals(((DrinksMenuLocal) older).getCloudVersion())) {
                //local was uploaded, replace local with cloud if cloud is newer, set up to date
                if (newer.hasGreaterVersionThan(older.getVersion())) {
                    //cloud is newer, replace local with cloud
                    replace(newer.getName(), newer);
                    newer.setCloudState(DrinksMenu.CloudState.UP_TO_DATE);
                    return newer;
                }
                older.setCloudState(DrinksMenu.CloudState.UP_TO_DATE);
                return older;
            } else if (newer.hasGreaterVersionThan(((DrinksMenuLocal) older).getCloudVersion())) {
                //local and cloud have some changes, TODO give user comparison
                return null;

            } else if (older.hasGreaterVersionThan(newer.getVersion())) {
                //local is newer, set ready to push
                older.setCloudState(DrinksMenu.CloudState.READY_FOR_PUSH);
                return older;
            } else {
                //local is up to date with cloud
                older.setCloudState(DrinksMenu.CloudState.UP_TO_DATE);
                return older;

            }
        }

        // if the older is a local and the newer is a local
        if (older instanceof DrinksMenuLocal && newer instanceof DrinksMenuLocal) {
            if (newer.hasGreaterVersionThan(older.getVersion())) {
                replace(newer.getName(), newer);
                newer.setCloudState(older.getCloudState());
                return newer;
            }
            if (((DrinksMenuLocal) newer).hasGreaterCloudVersionThan(((DrinksMenuLocal) older).getCloudVersion())) {
                replace(newer.getName(), newer);
                newer.setCloudState(older.getCloudState());
                return newer;
            }
        }

        // if the newer is more recent than the older
        if (newer.hasGreaterVersionThan(older.getVersion())) {
            replace(newer.getName(), newer);
            newer.setCloudState(DrinksMenu.CloudState.UP_TO_DATE);
            return newer;
        }

        return older;
    }

}
