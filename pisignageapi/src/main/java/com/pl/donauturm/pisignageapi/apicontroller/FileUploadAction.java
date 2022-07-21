package com.pl.donauturm.pisignageapi.apicontroller;

import com.pl.donauturm.pisignageapi.requests.FileInfoUploadRequest;
import com.pl.donauturm.pisignageapi.model.files.data.FileUpload;
import com.pl.donauturm.pisignageapi.model.files.messages.FileInfoUploadMessage;
import com.pl.donauturm.pisignageapi.model.files.messages.FileUploadedMessage;
import com.pl.donauturm.pisignageapi.requests.FileUploadRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileUploadAction {

  private final File file;
  private List<String> labels;
  private final boolean async;

  public FileUploadAction(File file) {
    this.file = file;
    this.labels = new ArrayList<>();
    this.async = false;
  }

  public FileUploadAction(File file, boolean async) {
    this.file = file;
    this.labels = new ArrayList<>();
    this.async = async;
  }

  public FileUploadAction withLabels(String... labels){
    this.labels = Arrays.asList(labels.clone());
    return this;
  }

  public void upload(){
    if (async) {
      uploadAsync();
      return;
    }
    uploadSync();
  }

  private void uploadSync() {
    FileUploadedMessage response = new FileUploadRequest(file).request();
    FileUpload fileUpload = response.getData().get(0);
    FileInfoUploadRequest fiur = new FileInfoUploadRequest(new FileInfoUploadMessage(Arrays.asList(fileUpload), labels));
    fiur.request();
  }

  private void uploadAsync(){
    new FileUploadRequest(file).requestAsync(response -> {
      FileUpload fileUpload = response.getData().get(0);
      FileInfoUploadRequest fiur = new FileInfoUploadRequest(new FileInfoUploadMessage(Arrays.asList(fileUpload), labels));
      fiur.requestAsync(null);
    });

  }

}
