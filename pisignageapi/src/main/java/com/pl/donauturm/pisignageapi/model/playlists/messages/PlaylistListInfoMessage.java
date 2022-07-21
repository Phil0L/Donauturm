package com.pl.donauturm.pisignageapi.model.playlists.messages;

import com.pl.donauturm.pisignageapi.model.playlists.data.PlaylistData;
import com.pl.donauturm.pisignageapi.model.universal.ResponseMessage;

import java.util.List;

public class PlaylistListInfoMessage implements ResponseMessage<List<PlaylistData>> {

  public String stat_message;
  public List<PlaylistData> data;
  public boolean success;

  public PlaylistListInfoMessage(String stat_message, List<PlaylistData> data, boolean success) {
    this.stat_message = stat_message;
    this.data = data;
    this.success = success;
  }

  public PlaylistListInfoMessage() {
  }

  public String getStatusMessage() {
    return stat_message;
  }

  public List<PlaylistData> getData() {
    return data;
  }

  public boolean isSuccess() {
    return success;
  }

  @Override
  public String toString() {
    return "PlaylistListInfoMessage{" +
           "stat_message='" + stat_message + '\'' +
           ", data=" + data +
           ", success=" + success +
           '}';
  }
}
