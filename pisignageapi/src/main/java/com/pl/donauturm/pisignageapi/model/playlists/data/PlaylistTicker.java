package com.pl.donauturm.pisignageapi.model.playlists.data;

public class PlaylistTicker {

  public boolean enable;
  public String behavior;
  public int textSpeed;
  public PlaylistRss rss;

  public PlaylistTicker(boolean enable, String behavior, int textSpeed, PlaylistRss rss) {
    this.enable = enable;
    this.behavior = behavior;
    this.textSpeed = textSpeed;
    this.rss = rss;
  }

  public PlaylistTicker() {
  }

  public boolean isEnable() {
    return enable;
  }

  public String getBehavior() {
    return behavior;
  }

  public int getTextSpeed() {
    return textSpeed;
  }

  public PlaylistRss getRss() {
    return rss;
  }

  @Override
  public String toString() {
    return "PlaylistTicker{" +
           "enable=" + enable +
           ", behavior='" + behavior + '\'' +
           ", textSpeed=" + textSpeed +
           ", rss=" + rss +
           '}';
  }
}
