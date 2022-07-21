package com.pl.donauturm.pisignageapi.model.groups.messages;

import com.pl.donauturm.pisignageapi.model.groups.data.GroupData;
import com.pl.donauturm.pisignageapi.model.universal.ResponseMessage;

import java.util.List;

public class GroupListInfoMessage implements ResponseMessage<List<GroupData>> {

  public String stat_message;
  public List<GroupData> data;
  public boolean success;

  public GroupListInfoMessage(String stat_message, List<GroupData> data, boolean success) {
    this.stat_message = stat_message;
    this.data = data;
    this.success = success;
  }

  public GroupListInfoMessage() {
  }

  public String getStatusMessage() {
    return stat_message;
  }

  public List<GroupData> getData() {
    return data;
  }

  public boolean isSuccess() {
    return success;
  }

  @Override
  public String toString() {
    return "GroupListMessage{" +
           "stat_message='" + stat_message + '\'' +
           ", data=" + data +
           ", success=" + success +
           '}';
  }
}
