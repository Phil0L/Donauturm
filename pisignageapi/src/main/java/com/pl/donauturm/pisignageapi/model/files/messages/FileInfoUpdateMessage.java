package com.pl.donauturm.pisignageapi.model.files.messages;

import com.pl.donauturm.pisignageapi.model.files.data.FileUpdate;

public class FileInfoUpdateMessage {

  public FileUpdate dbdata;

  public FileInfoUpdateMessage(FileUpdate dbdata) {
    this.dbdata = dbdata;
  }

  public FileInfoUpdateMessage() {
  }

  public FileUpdate getDbdata() {
    return dbdata;
  }

  public void setDbdata(FileUpdate dbdata) {
    this.dbdata = dbdata;
  }

  @Override
  public String toString() {
    return "FileDetailsUpdateMessage{" +
           "dbdata=" + dbdata +
           '}';
  }
}
