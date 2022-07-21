package com.pl.donauturm.pisignageapi.model.playlists.data;

public class PlaylistAudio {

  public boolean enable;
  public boolean random;
  public int volume;

  public PlaylistAudio(boolean enable, boolean random, int volume) {
    this.enable = enable;
    this.random = random;
    this.volume = volume;
  }

  public PlaylistAudio() {
  }

  public boolean isEnable() {
    return enable;
  }

  public boolean isRandom() {
    return random;
  }

  public int getVolume() {
    return volume;
  }

  @Override
  public String toString() {
    return "PlaylistAudio{" +
           "enable=" + enable +
           ", random=" + random +
           ", volume=" + volume +
           '}';
  }
}
