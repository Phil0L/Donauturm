package com.pl.donauturm.pisignageapi.model.playlists.messages;

import com.pl.donauturm.pisignageapi.model.universal.ResponseMessage;

public class PlaylistUploadedMessage implements ResponseMessage<String> {

  public String stat_message;
  public String data;
  public boolean success;

  public PlaylistUploadedMessage(String stat_message, String data, boolean success) {
    this.stat_message = stat_message;
    this.data = data;
    this.success = success;
  }

  public PlaylistUploadedMessage() {
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
    return "PlaylistUploadedMessage{" +
           "stat_message='" + stat_message + '\'' +
           ", data='" + data + '\'' +
           ", success=" + success +
           '}';
  }
}
