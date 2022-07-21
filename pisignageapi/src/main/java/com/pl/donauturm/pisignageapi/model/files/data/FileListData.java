package com.pl.donauturm.pisignageapi.model.files.data;

import java.util.List;

public class FileListData {

  public FileListSize sizes;
  public List<String> files;
  public List<FileData> dbdata;
  public List<String> systemAssets;
  public boolean player;

  public FileListData(FileListSize sizes, List<String> files, List<FileData> dbdata, List<String> systemAssets, boolean player) {
    this.sizes = sizes;
    this.files = files;
    this.dbdata = dbdata;
    this.systemAssets = systemAssets;
    this.player = player;
  }

  public FileListData() {
  }

  public FileListSize getSizes() {
    return sizes;
  }

  public List<String> getFiles() {
    return files;
  }

  public List<FileData> getDbdata() {
    return dbdata;
  }

  public List<String> getSystemAssets() {
    return systemAssets;
  }

  public boolean isPlayer() {
    return player;
  }

  @Override
  public String toString() {
    return "FilesData{" +
           "sizes=" + sizes +
           ", files=" + files +
           ", dbdata=" + dbdata +
           ", systemAssets=" + systemAssets +
           ", player=" + player +
           '}';
  }
}
