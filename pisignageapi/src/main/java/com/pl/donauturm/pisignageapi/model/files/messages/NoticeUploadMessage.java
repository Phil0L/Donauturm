package com.pl.donauturm.pisignageapi.model.files.messages;

import com.pl.donauturm.pisignageapi.model.files.data.NoticeData;

import java.util.ArrayList;
import java.util.List;

public class NoticeUploadMessage{
    public NoticeData formdata;
    public List<String> categories;

    public NoticeUploadMessage(String title, String description, String footer) {
        this.formdata = new NoticeData(title, description, footer, "", "");
        this.categories = new ArrayList<>();
    }

    public NoticeUploadMessage(String title, String description, String footer, String... categories) {
        this.formdata = new NoticeData(title, description, footer, "", "");
        this.categories = List.of(categories);
    }

    public NoticeUploadMessage(NoticeData formdata, List<String> categories) {
        this.formdata = formdata;
        this.categories = categories;
    }

    public NoticeUploadMessage() {
    }

    public NoticeData getFormdata() {
        return formdata;
    }

    public void setFormdata(NoticeData formdata) {
        this.formdata = formdata;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "NoticeData{" +
                "formdata=" + formdata +
                ", categories=" + categories +
                '}';
    }
}
