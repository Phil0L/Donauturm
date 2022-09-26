package com.pl.donauturm.pisignageapi.model.files.data;

import com.pl.donauturm.pisignageapi.model.Notice;

import java.util.List;

public class NoticeData implements Notice {

    public NoticeFormData formdata;
    public List<String> categories;

    public NoticeData(NoticeFormData formdata, List<String> categories) {
        this.formdata = formdata;
        this.categories = categories;
    }

    public NoticeData() {
    }

    public NoticeFormData getFormdata() {
        return formdata;
    }

    public void setFormdata(NoticeFormData formdata) {
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

    @Override
    public String getTitle() {
        return getFormdata().getTitle();
    }

    @Override
    public String getDescription() {
        return getFormdata().getDescription();
    }

    @Override
    public String getFooter() {
        return getFormdata().getFooter();
    }

    public static class NoticeFormData {

        public String title;
        public String description;
        public String footer;
        public String image;
        public String imagepath;

        public NoticeFormData(String title, String description, String footer, String image, String imagepath) {
            this.title = title;
            this.description = description;
            this.footer = footer;
            this.image = image;
            this.imagepath = imagepath;
        }

        public NoticeFormData() {
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

}
