package com.pl.donauturm.pisignageapi.model.universal;

public class EmptyResponseMessage {

  public String stat_message;
  public boolean success;

  public EmptyResponseMessage(String stat_message, boolean success) {
    this.stat_message = stat_message;

    this.success = success;
  }

  public EmptyResponseMessage() {
  }

  public String getStat_message() {
    return stat_message;
  }


  public boolean isSuccess() {
    return success;
  }

  @Override
  public String toString() {
    return "ResponseMessage{" +
           "stat_message='" + stat_message + '\'' +
           ", success=" + success +
           '}';
  }
}
