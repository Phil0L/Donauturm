package com.pl.donauturm.drinksmenu.controller.drinks;

import com.pl.donauturm.drinksmenu.model.content.Drink;

import java.util.List;

public class DrinkRegistry {

    public static final List<Drink> DRINKS = List.of(
            new Drink("Bier", "Gold Ochsen Hell", 2),
            new Drink("Desperados", "Bier mit Tequilla Geschmack", 3),
            new Drink("Spezial", "Gold Ochsen Spezial", 3),
            new Drink("Radler", "Gold Ochsen Natur Keller Radler", 2),
            new Drink("Jägermeister", "Jägermeister Kräuter Schnaps", 2),
            new Drink("Vodka", "billiger Vodka", 2),
            new Drink("Tequilla", "Tequilla Silver", 2),
            new Drink("Joster", "Saurer Joster", 1),
            new Drink("Pfeffi", "Landhaus Pfefferminz Likör", 1),
            new Drink("Schüttler", "Blue Curacao + Zitronensaft", 1),
            new Drink("Waldmeister", "Landhaus Waldmeister Likör", 1)
    );
}
