package com.pl.donauturm.drinksmenu.controller.drinks.routines;

import com.google.gson.Gson;
import com.pl.donauturm.drinksmenu.controller.util.AsyncRoutine;
import com.pl.donauturm.drinksmenu.controller.util.api.DrinksMenuAPI;
import com.pl.donauturm.drinksmenu.model.Drink;
import com.pl.donauturm.drinksmenu.model.DrinkList;
import com.pl.donauturm.pisignageapi.model.Notice;

import java.util.List;

public class GetAllDrinksRoutine extends AsyncRoutine<List<Drink>> {

    private final DrinksMenuAPI api;

    public GetAllDrinksRoutine(DrinksMenuAPI api) {
        this.api = api;
    }

    @Override
    public void startTask(int index, Object... args) {
        final String DRINKS_FILENAME = "Drinks";
        switch (index) {
            case 0:
                List<String> nn = api.getAllNoticeNames();
                String noticeName = nn.stream().filter(s -> s.startsWith(DRINKS_FILENAME)).findFirst().orElse(DRINKS_FILENAME);
                nextTask(noticeName);
                break;
            case 1:
                Object o = args[0];
                if (o instanceof String) {
                    String noticeName1 = (String) o;
                    Notice notice = api.getNotice(noticeName1);
                    if (notice == null){
                        finish(null);
                        return;
                    }
                    nextTask(notice);
                } else {
                    error();
                }
                break;
            case 2:
                Object o1 = args[0];
                if (o1 instanceof Notice) {
                    Notice notice = (Notice) o1;
                    String json = notice.getDescription();
                    Gson gson = new Gson();
                    DrinkList drinkList = gson.fromJson(json, DrinkList.class);
                    finish(drinkList.getDrinks());
                } else {
                    error();
                }
                break;
            default:
                error();
                break;
        }
    }
}
