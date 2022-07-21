package com.pl.donauturm.pisignageapi.model.files.messages;

import com.pl.donauturm.pisignageapi.model.files.data.FileUpload;

import java.util.List;

public class FileInfoUploadMessage {

  public List<FileUpload> files;
  public List<String> categories;

  public FileInfoUploadMessage(List<FileUpload> files, List<String> categories) {
    this.files = files;
    this.categories = categories;
  }

  public FileInfoUploadMessage() {
  }

  public List<FileUpload> getFiles() {
    return files;
  }

  public void setFiles(List<FileUpload> files) {
    this.files = files;
  }

  public List<String> getCategories() {
    return categories;
  }

  public void setCategories(List<String> categories) {
    this.categories = categories;
  }

  @Override
  public String toString() {
    return "FileDetailsUploadMessage{" +
           "files=" + files +
           ", categories=" + categories +
           '}';
  }
}
