package com.pl.donauturm.pisignageapi.model.files.messages;

import com.pl.donauturm.pisignageapi.model.files.data.FileInfo;
import com.pl.donauturm.pisignageapi.model.universal.ResponseMessage;

public class FileInfoMessage implements ResponseMessage<FileInfo> {

  public String stat_message;
  public FileInfo data;
  public boolean success;

  public FileInfoMessage(String stat_message, FileInfo data, boolean success) {
    this.stat_message = stat_message;
    this.data = data;
    this.success = success;
  }

  public FileInfoMessage() {
  }

  public String getStatusMessage() {
    return stat_message;
  }

  public FileInfo getData() {
    return data;
  }

  public boolean isSuccess() {
    return success;
  }

  @Override
  public String toString() {
    return "FileInfoMessage{" +
           "stat_message='" + stat_message + '\'' +
           ", data=" + data +
           ", success=" + success +
           '}';
  }
}
