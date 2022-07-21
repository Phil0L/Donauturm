package com.pl.donauturm.pisignageapi.model.files.data;

public class FileListSize {

  public int total;
  public int used;

  public FileListSize(int total, int used) {
    this.total = total;
    this.used = used;
  }

  public FileListSize() {
  }

  public int getTotal() {
    return total;
  }

  public int getUsed() {
    return used;
  }

  @Override
  public String toString() {
    return "FilesSizes{" +
           "total=" + total +
           ", used=" + used +
           '}';
  }
}
