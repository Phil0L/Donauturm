package com.pl.donauturm.pisignageapi.model;

import com.pl.donauturm.pisignageapi.model.files.data.NoticeData;

import java.util.ArrayList;
import java.util.List;

public interface Notice {

    String getTitle();

    String getDescription();

    String getFooter();

    static Notice create(String title, String description, String footer) {
        return new NoticeData(new NoticeData.NoticeFormData(title, description, footer, "", ""), new ArrayList<>());
    }

    static Notice create(String title, String description, String footer, String... categories) {
        return new NoticeData(new NoticeData.NoticeFormData(title, description, footer, "", ""), List.of(categories));
    }
}
