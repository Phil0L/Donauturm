package com.pl.donauturm.pisignageapi.model.files.data;

import com.pl.donauturm.pisignageapi.model.Notice;

public class NoticeData implements Notice {

    public String title;
    public String description;
    public String footer;
    public String image;
    public String imagepath;

    public NoticeData(String title, String description, String footer, String image, String imagepath) {
        this.title = title;
        this.description = description;
        this.footer = footer;
        this.image = image;
        this.imagepath = imagepath;
    }

    public NoticeData() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    @Override
    public String toString() {
        return "NoticeFormData{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", footer='" + footer + '\'' +
                ", image='" + image + '\'' +
                ", imagepath='" + imagepath + '\'' +
                '}';
    }

}
