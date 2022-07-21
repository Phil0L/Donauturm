package com.pl.donauturm.pisignageapi.model.files.data;

public class FileInfo {

  public String name;
  public String size;
  public String ctime;
  public String path;
  public String type;
  public FileData dbdata;


  public FileInfo(String name, String size, String ctime, String path, String type, FileData dbdata) {
    this.name = name;
    this.size = size;
    this.ctime = ctime;
    this.path = path;
    this.type = type;
    this.dbdata = dbdata;
  }

  public FileInfo() {
  }

  public String getName() {
    return name;
  }

  public String getSize() {
    return size;
  }

  public String getCtime() {
    return ctime;
  }

  public String getPath() {
    return path;
  }

  public String getType() {
    return type;
  }

  public FileData getDbdata() {
    return dbdata;
  }

  @Override
  public String toString() {
    return "FileInfo{" +
           "name='" + name + '\'' +
           ", size='" + size + '\'' +
           ", ctime='" + ctime + '\'' +
           ", path='" + path + '\'' +
           ", type='" + type + '\'' +
           ", dbdata=" + dbdata +
           '}';
  }
}

