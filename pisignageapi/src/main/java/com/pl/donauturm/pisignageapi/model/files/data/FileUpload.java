package com.pl.donauturm.pisignageapi.model.files.data;

public class FileUpload {

  public String name;
  public String description;
  public int size;
  public String type;

  public FileUpload(String name, String description, int size, String type) {
    this.name = name;
    this.description = description;
    this.size = size;
    this.type = type;
  }

  public FileUpload() {
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public int getSize() {
    return size;
  }

  public String getType() {
    return type;
  }

  @Override
  public String toString() {
    return "FileUploadData{" +
           "name='" + name + '\'' +
           ", description='" + description + '\'' +
           ", size=" + size +
           ", type='" + type + '\'' +
           '}';
  }
}
