package com.pl.donauturm.pisignageapi.model.files.data;

public class NoticeInfo {

    public NoticeData data;
    public FileData dbData;

    public NoticeInfo(NoticeData data, FileData dbData) {
        this.data = data;
        this.dbData = dbData;
    }

    public NoticeInfo() {
    }

    public NoticeData getData() {
        return data;
    }

    public void setData(NoticeData data) {
        this.data = data;
    }

    public FileData getDbData() {
        return dbData;
    }

    public void setDbData(FileData dbData) {
        this.dbData = dbData;
    }

    @Override
    public String toString() {
        return "NoticeInfo{" +
                "data=" + data +
                ", dbData=" + dbData +
                '}';
    }
}
