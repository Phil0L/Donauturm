package com.pl.donauturm.pisignageapi.model.files.messages;

import com.pl.donauturm.pisignageapi.model.files.data.FileListData;
import com.pl.donauturm.pisignageapi.model.universal.ResponseMessage;

public class FileListInfoMessage implements ResponseMessage<FileListData> {

  public String stat_message;
  public FileListData data;
  public boolean success;

  public FileListInfoMessage(String stat_message, FileListData data, boolean success) {
    this.stat_message = stat_message;
    this.data = data;
    this.success = success;
  }

  public FileListInfoMessage() {
  }

  public String getStatusMessage() {
    return stat_message;
  }

  public FileListData getData() {
    return data;
  }

  public boolean isSuccess() {
    return success;
  }

  @Override
  public String toString() {
    return "FilesMessage{" +
           "stat_message='" + stat_message + '\'' +
           ", data=" + data +
           ", success=" + success +
           '}';
  }
}
