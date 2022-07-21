package com.pl.donauturm.pisignageapi.model.playlists.data;

public class PlaylistAds {

  public boolean adPlaylist;
  public int adCount;
  public int adInterval;

  public PlaylistAds(boolean adPlaylist, int adCount, int adInterval) {
    this.adPlaylist = adPlaylist;
    this.adCount = adCount;
    this.adInterval = adInterval;
  }

  public PlaylistAds() {
  }

  public boolean isAdPlaylist() {
    return adPlaylist;
  }

  public int getAdCount() {
    return adCount;
  }

  public int getAdInterval() {
    return adInterval;
  }

  @Override
  public String toString() {
    return "PlaylistAds{" +
           "adPlaylist=" + adPlaylist +
           ", adCount=" + adCount +
           ", adInterval=" + adInterval +
           '}';
  }
}
