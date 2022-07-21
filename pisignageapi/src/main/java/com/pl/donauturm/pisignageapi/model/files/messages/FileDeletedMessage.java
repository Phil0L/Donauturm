package com.pl.donauturm.pisignageapi.model.files.messages;

import com.pl.donauturm.pisignageapi.model.universal.ResponseMessage;

public class FileDeletedMessage implements ResponseMessage<String> {

  public String stat_message;
  public String data;
  public boolean success;

  public FileDeletedMessage(String stat_message, String data, boolean success) {
    this.stat_message = stat_message;
    this.data = data;
    this.success = success;
  }

  public FileDeletedMessage() {
  }

  public String getStatusMessage() {
    return stat_message;
  }

  public String getData() {
    return data;
  }

  public boolean isSuccess() {
    return success;
  }

  @Override
  public String toString() {
    return "FileDeletedMessage{" +
           "stat_message='" + stat_message + '\'' +
           ", data='" + data + '\'' +
           ", success=" + success +
           '}';
  }
}
