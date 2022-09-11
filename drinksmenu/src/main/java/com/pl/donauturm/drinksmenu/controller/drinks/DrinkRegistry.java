package com.pl.donauturm.drinksmenu.controller.drinks;

import com.pl.donauturm.drinksmenu.model.content.DrinkItem;
import com.pl.donauturm.drinksmenu.util.RegistryMap;

import java.util.List;

public class DrinkRegistry extends RegistryMap<DrinkItem> {

    private static final DrinkRegistry instance = new DrinkRegistry();

    private DrinkRegistry() {
        super();
    }

    public static DrinkRegistry getInstance() {
        if (instance != null) return instance;
        else return new DrinkRegistry();
    }

    @Override
    public List<DrinkItem> defaultValues() {
        return List.of(
                new DrinkItem("Bier", "Gold Ochsen Hell", 2),
                new DrinkItem("Desperados", "Bier mit Tequilla Geschmack", 3),
                new DrinkItem("Spezial", "Gold Ochsen Spezial", 3),
                new DrinkItem("Radler", "Gold Ochsen Natur Keller Radler", 2),
                new DrinkItem("Jägermeister", "Jägermeister Kräuter Schnaps", 2),
                new DrinkItem("Vodka", "billiger Vodka", 2),
                new DrinkItem("Tequilla", "Tequilla Silver", 2),
                new DrinkItem("Joster", "Saurer Joster", 1),
                new DrinkItem("Pfeffi", "Landhaus Pfefferminz Likör", 1),
                new DrinkItem("Schüttler", "Blue Curacao + Zitronensaft", 1),
                new DrinkItem("Waldmeister", "Landhaus Waldmeister Likör", 1)
        );
    }
}
