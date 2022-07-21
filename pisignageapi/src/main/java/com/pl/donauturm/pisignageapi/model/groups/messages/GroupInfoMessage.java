package com.pl.donauturm.pisignageapi.model.groups.messages;

import com.pl.donauturm.pisignageapi.model.groups.data.GroupData;
import com.pl.donauturm.pisignageapi.model.universal.ResponseMessage;

public class GroupInfoMessage implements ResponseMessage<GroupData> {

  public String stat_message;
  public GroupData data;
  public boolean success;

  public GroupInfoMessage(String stat_message, GroupData data, boolean success) {
    this.stat_message = stat_message;
    this.data = data;
    this.success = success;
  }

  public GroupInfoMessage() {
  }

  public String getStatusMessage() {
    return stat_message;
  }

  public GroupData getData() {
    return data;
  }

  public boolean isSuccess() {
    return success;
  }

  @Override
  public String toString() {
    return "GroupInfoMessage{" +
           "stat_message='" + stat_message + '\'' +
           ", data=" + data +
           ", success=" + success +
           '}';
  }
}
