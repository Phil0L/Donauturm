package com.pl.donauturm.drinksmenu.controller.drinks.routines;

import com.google.gson.Gson;
import com.pl.donauturm.drinksmenu.controller.util.AsyncRoutine;
import com.pl.donauturm.drinksmenu.controller.util.api.DrinksMenuAPI;
import com.pl.donauturm.drinksmenu.model.Drink;
import com.pl.donauturm.drinksmenu.model.DrinkList;
import com.pl.donauturm.pisignageapi.model.files.messages.NoticeUploadMessage;

import java.util.List;
import java.util.stream.Collectors;

public class SaveAllDrinksRoutine extends AsyncRoutine<Void> {

    private final List<Drink> drinks;
    private final DrinksMenuAPI api;

    public SaveAllDrinksRoutine(List<Drink> drinks, DrinksMenuAPI api) {
        this.drinks = drinks;
        this.api = api;
    }

    @Override
    public void startTask(int index, Object... args) {
        final String DRINKS_FILENAME = "Drinks";
        switch (index) {
            case 0:
                List<String> noticeNames0 = api.getAllNoticeNames();
                nextTask(noticeNames0);
                break;
            case 1:
                Object o = args[0];
                if (o instanceof List) {
                    List<?> nn = (List<?>) o;
                    List<String> noticeNames = nn.stream().filter(s -> s instanceof String).map(s -> (String) s).collect(Collectors.toList());
                    if (noticeNames.contains(DRINKS_FILENAME + ".html")) {
                        nextTask();
                        break;
                    }
                    String oldName = noticeNames.stream().filter(s -> s.startsWith(DRINKS_FILENAME)).findFirst().orElse(null);
                    if (oldName == null){
                        nextTask();
                        break;
                    }
                    api.deleteNotice(oldName);
                    nextTask();
                } else {
                    error();
                }
                break;
            case 2:
                Gson gson = new Gson();
                DrinkList drinkList = new DrinkList(drinks);
                String json = gson.toJson(drinkList);
                NoticeUploadMessage num = new NoticeUploadMessage(DRINKS_FILENAME, json, "This is a list of all drinks as a database", "generated", "drink-list");
                api.editNotice(DRINKS_FILENAME + ".html", num);
                finish(null);
                break;
            default:
                error();
                break;
        }
    }
}
