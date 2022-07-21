package com.pl.donauturm.pisignageapi.model.playlists.messages;

public class PlaylistUploadMessage {

  public String file;

  public PlaylistUploadMessage(String playlistName) {
    this.file = playlistName;
  }

  public PlaylistUploadMessage() {
  }

  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

  @Override
  public String toString() {
    return "PlaylistUploadMessage{" +
           "file='" + file + '\'' +
           '}';
  }
}
