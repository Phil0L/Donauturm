package com.pl.donauturm.pisignageapi.model.session.data;

public class UserUIDefaults {

  public String playlistView;

  public UserUIDefaults(String playlistView) {
    this.playlistView = playlistView;
  }

  public UserUIDefaults() {
  }

  public String getPlaylistView() {
    return playlistView;
  }

  @Override
  public String toString() {
    return "UserSettingUIDefaults{" +
           "playlistView='" + playlistView + '\'' +
           '}';
  }
}
