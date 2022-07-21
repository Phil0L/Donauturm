package com.pl.donauturm.pisignageapi.model.files.messages;

import com.pl.donauturm.pisignageapi.model.files.data.FileUpdate;
import com.pl.donauturm.pisignageapi.model.universal.ResponseMessage;

public class FileInfoUpdatedMessage implements ResponseMessage<FileUpdate> {

  public String stat_message;
  public FileUpdate data;
  public boolean success;

  public FileInfoUpdatedMessage(String stat_message, FileUpdate data, boolean success) {
    this.stat_message = stat_message;
    this.data = data;
    this.success = success;
  }

  public FileInfoUpdatedMessage() {
  }

  public String getStatusMessage() {
    return stat_message;
  }

  public FileUpdate getData() {
    return data;
  }

  public boolean isSuccess() {
    return success;
  }

  @Override
  public String toString() {
    return "FileDetailsUpdatedMessage{" +
           "stat_message='" + stat_message + '\'' +
           ", data=" + data +
           ", success=" + success +
           '}';
  }
}
