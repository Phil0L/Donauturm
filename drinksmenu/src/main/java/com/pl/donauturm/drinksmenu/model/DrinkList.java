package com.pl.donauturm.drinksmenu.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class DrinkList implements Serializable {

    List<Drink> drinks;

    public DrinkList(List<Drink> drinks) {
        this.drinks = drinks;
    }

    public DrinkList() {
    }

    public List<Drink> getDrinks() {
        return drinks;
    }

    @NonNull
    @Override
    public String toString() {
        return "DrinkList{" +
                "drinks=" + drinks +
                '}';
    }
}
