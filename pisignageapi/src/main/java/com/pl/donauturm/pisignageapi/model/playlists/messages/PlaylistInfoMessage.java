package com.pl.donauturm.pisignageapi.model.playlists.messages;

import com.pl.donauturm.pisignageapi.model.playlists.data.PlaylistData;
import com.pl.donauturm.pisignageapi.model.universal.ResponseMessage;

public class PlaylistInfoMessage implements ResponseMessage<PlaylistData> {

  public String stat_message;
  public PlaylistData data;
  public boolean success;

  public PlaylistInfoMessage(String stat_message, PlaylistData data, boolean success) {
    this.stat_message = stat_message;
    this.data = data;
    this.success = success;
  }

  public PlaylistInfoMessage() {
  }

  public String getStatusMessage() {
    return stat_message;
  }

  public PlaylistData getData() {
    return data;
  }

  public boolean isSuccess() {
    return success;
  }

  @Override
  public String toString() {
    return "PlaylistInfoMessage{" +
           "stat_message='" + stat_message + '\'' +
           ", data=" + data +
           ", success=" + success +
           '}';
  }
}
