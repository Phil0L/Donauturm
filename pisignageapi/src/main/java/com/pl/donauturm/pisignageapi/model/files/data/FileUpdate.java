package com.pl.donauturm.pisignageapi.model.files.data;

import java.util.List;

public class FileUpdate {

  public FileResolution resolution;
  public List<String> labels;
  public List<String> playlists;
  public List<String> groupIds;
  public String name;
  public String type;
  public int duration;
  public String size;
  public String thumbnail;
  public FileValidity validity;

  public FileUpdate(String name, String type, int duration, String size, String thumbnail, FileValidity validity, List<String> groupIds, List<String> playlists, List<String> labels, FileResolution resolution) {
    this.name = name;
    this.type = type;
    this.duration = duration;
    this.size = size;
    this.thumbnail = thumbnail;
    this.validity = validity;
    this.groupIds = groupIds;
    this.playlists = playlists;
    this.labels = labels;
    this.resolution = resolution;
  }

  public FileUpdate() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public String getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }

  public FileValidity getValidity() {
    return validity;
  }

  public void setValidity(FileValidity validity) {
    this.validity = validity;
  }

  public List<String> getGroupIds() {
    return groupIds;
  }

  public void setGroupIds(List<String> groupIds) {
    this.groupIds = groupIds;
  }

  public List<String> getPlaylists() {
    return playlists;
  }

  public void setPlaylists(List<String> playlists) {
    this.playlists = playlists;
  }

  public List<String> getLabels() {
    return labels;
  }

  public void setLabels(List<String> labels) {
    this.labels = labels;
  }

  public FileResolution getResolution() {
    return resolution;
  }

  public void setResolution(FileResolution resolution) {
    this.resolution = resolution;
  }

  @Override
  public String toString() {
    return "FileDetails{" +
           "name='" + name + '\'' +
           ", type='" + type + '\'' +
           ", duration=" + duration +
           ", size='" + size + '\'' +
           ", thumbnail='" + thumbnail + '\'' +
           ", validity=" + validity +
           ", groupIds=" + groupIds +
           ", playlists=" + playlists +
           ", labels=" + labels +
           ", resolution=" + resolution +
           '}';
  }
}
