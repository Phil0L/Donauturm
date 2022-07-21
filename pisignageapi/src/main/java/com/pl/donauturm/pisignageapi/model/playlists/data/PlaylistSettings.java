package com.pl.donauturm.pisignageapi.model.playlists.data;

public class PlaylistSettings {

  public PlaylistTicker ticker;
  public PlaylistAds ads;
  public PlaylistAudio audio;

  public PlaylistSettings(PlaylistTicker ticker, PlaylistAds ads, PlaylistAudio audio) {
    this.ticker = ticker;
    this.ads = ads;
    this.audio = audio;
  }

  public PlaylistSettings() {
  }

  public PlaylistTicker getTicker() {
    return ticker;
  }

  public PlaylistAds getAds() {
    return ads;
  }

  public PlaylistAudio getAudio() {
    return audio;
  }

  @Override
  public String toString() {
    return "PlaylistSettings{" +
           "ticker=" + ticker +
           ", ads=" + ads +
           ", audio=" + audio +
           '}';
  }
}
