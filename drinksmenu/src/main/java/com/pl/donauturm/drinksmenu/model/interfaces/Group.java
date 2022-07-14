package com.pl.donauturm.drinksmenu.model.interfaces;

import com.pl.donauturm.drinksmenu.model.Item;

import java.util.List;

public interface Group<T extends Item> {

    List<T> getItems();

}
