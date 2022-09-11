package com.pl.donauturm.drinksmenu.model.interfaces;

import com.pl.donauturm.drinksmenu.model.content.DrinksMenuItem;

import java.util.List;

public interface Group<T extends DrinksMenuItem> {

    List<T> getItems();

}
