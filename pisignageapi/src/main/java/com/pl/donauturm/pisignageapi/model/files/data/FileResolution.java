package com.pl.donauturm.pisignageapi.model.files.data;

public class FileResolution {

  public String width;
  public String height;

  public FileResolution(String width, String height) {
    this.width = width;
    this.height = height;
  }

  public FileResolution() {
  }

  public String getWidth() {
    return width;
  }

  public String getHeight() {
    return height;
  }

  @Override
  public String toString() {
    return "FileResolution{" +
           "width='" + width + '\'' +
           ", height='" + height + '\'' +
           '}';
  }
}
