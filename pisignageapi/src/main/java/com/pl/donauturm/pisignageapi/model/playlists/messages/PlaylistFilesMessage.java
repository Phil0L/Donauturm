package com.pl.donauturm.pisignageapi.model.playlists.messages;

import java.util.List;

public class PlaylistFilesMessage {

  public String playlist;
  public List<String> assets;


  public PlaylistFilesMessage(String playlist, List<String> assets) {
    this.playlist = playlist;
    this.assets = assets;
  }

  public PlaylistFilesMessage() {
  }

  public String getPlaylist() {
    return playlist;
  }

  public void setPlaylist(String playlist) {
    this.playlist = playlist;
  }

  public List<String> getAssets() {
    return assets;
  }

  public void setAssets(List<String> assets) {
    this.assets = assets;
  }

  @Override
  public String toString() {
    return "PlaylistFilesMessage{" +
           "playlist='" + playlist + '\'' +
           ", assets=" + assets +
           '}';
  }
}
