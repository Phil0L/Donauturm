package com.pl.donauturm.pisignageapi.model.playlists.data;

public class PlaylistRss {

  public boolean enable;
  public Object link;
  public int feedDelay;

  public PlaylistRss(boolean enable, Object link, int feedDelay) {
    this.enable = enable;
    this.link = link;
    this.feedDelay = feedDelay;
  }

  public PlaylistRss() {
  }

  public boolean isEnable() {
    return enable;
  }

  public Object getLink() {
    return link;
  }

  public int getFeedDelay() {
    return feedDelay;
  }

  @Override
  public String toString() {
    return "PlaylistRss{" +
           "enable=" + enable +
           ", link=" + link +
           ", feedDelay=" + feedDelay +
           '}';
  }
}
