package com.pl.donauturm.pisignageapi.model.playlists.data;

public class PlaylistAsset {

  public String filename;
  public int duration;
  public boolean selected;
  public Object option;
  public boolean dragSelected;
  public boolean fullScreen;

  public PlaylistAsset(String filename, int duration, boolean selected, Object option, boolean dragSelected, boolean fullScreen) {
    this.filename = filename;
    this.duration = duration;
    this.selected = selected;
    this.option = option;
    this.dragSelected = dragSelected;
    this.fullScreen = fullScreen;
  }

  public PlaylistAsset() {
  }

  public String getFilename() {
    return filename;
  }

  public int getDuration() {
    return duration;
  }

  public boolean isSelected() {
    return selected;
  }

  public Object getOption() {
    return option;
  }

  public boolean isDragSelected() {
    return dragSelected;
  }

  public boolean isFullScreen() {
    return fullScreen;
  }

  @Override
  public String toString() {
    return "PlaylistAsset{" +
           "filename='" + filename + '\'' +
           ", duration=" + duration +
           ", selected=" + selected +
           ", option=" + option +
           ", dragSelected=" + dragSelected +
           ", fullScreen=" + fullScreen +
           '}';
  }
}
