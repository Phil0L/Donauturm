package com.pl.donauturm.pisignageapi.model.universal;

public class StringResponseMessage implements ResponseMessage<String>{

  public String stat_message;
  public String data;
  public boolean success;

  public StringResponseMessage(String stat_message, String data, boolean success) {
    this.stat_message = stat_message;
    this.data = data;
    this.success = success;
  }

  public StringResponseMessage() {
  }

  @Override
  public String getStatusMessage() {
    return stat_message;
  }

  @Override
  public String getData() {
    return data;
  }

  public boolean isSuccess() {
    return success;
  }

  @Override
  public String toString() {
    return "StringResponseMessage{" +
            "stat_message='" + stat_message + '\'' +
            ", data='" + data + '\'' +
            ", success=" + success +
            '}';
  }
}
