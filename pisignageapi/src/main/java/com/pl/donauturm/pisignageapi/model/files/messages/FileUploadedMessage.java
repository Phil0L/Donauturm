package com.pl.donauturm.pisignageapi.model.files.messages;

import com.pl.donauturm.pisignageapi.model.universal.ResponseMessage;
import com.pl.donauturm.pisignageapi.model.files.data.FileUpload;

import java.util.List;

public class FileUploadedMessage implements ResponseMessage<List<FileUpload>> {

  public String stat_message;
  public List<FileUpload> data;
  public boolean success;

  public FileUploadedMessage(String stat_message, List<FileUpload> data, boolean success) {
    this.stat_message = stat_message;
    this.data = data;
    this.success = success;
  }

  public FileUploadedMessage() {
  }

  public String getStatusMessage() {
    return stat_message;
  }

  public List<FileUpload> getData() {
    return data;
  }

  public boolean isSuccess() {
    return success;
  }

  @Override
  public String toString() {
    return "FileUploadedMessage{" +
           "stat_message='" + stat_message + '\'' +
           ", data=" + data +
           ", success=" + success +
           '}';
  }
}
