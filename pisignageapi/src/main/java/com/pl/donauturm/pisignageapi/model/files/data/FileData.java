package com.pl.donauturm.pisignageapi.model.files.data;

import com.pl.donauturm.pisignageapi.model.Asset;
import com.pl.donauturm.pisignageapi.model.universal.Creator;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class FileData implements Asset {

  public FileResolution resolution;
  public Creator createdBy;
  public List<String> labels;
  public List<String> playlists;
  public List<String> groupIds;
  public String installation;
  public String _id;
  public String createdAt;
  public String name;
  public String type;
  public String duration;
  public String size;
  public String thumbnail;
  public String __v;

  public FileData(FileResolution resolution, Creator createdBy, List<String> labels, List<String> playlists, List<String> groupIds, String installation, String _id, String createdAt, String name, String type, String duration, String size, String thumbnail, String __v) {
    this.resolution = resolution;
    this.createdBy = createdBy;
    this.labels = labels;
    this.playlists = playlists;
    this.groupIds = groupIds;
    this.installation = installation;
    this._id = _id;
    this.createdAt = createdAt;
    this.name = name;
    this.type = type;
    this.duration = duration;
    this.size = size;
    this.thumbnail = thumbnail;
    this.__v = __v;
  }

  public FileData() {
  }

  public FileResolution getResolution() {
    return resolution;
  }

  public Creator getCreatedBy() {
    return createdBy;
  }

  public List<String> getLabels() {
    return labels;
  }

  public List<String> getPlaylists() {
    return playlists;
  }

  public List<String> getGroupIds() {
    return groupIds;
  }

  public String getInstallation() {
    return installation;
  }

  public String getID() {
    return _id;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public String getName() {
    return name;
  }

  @Override
  public String getPath() {
    return null;
  }

  public String getType() {
    return type;
  }

  public String getDuration() {
    return duration;
  }

  public String getSize() {
    return size;
  }

  @Override
  public Date getCreationTime() {
    try {
      return DateFormat.getDateTimeInstance().parse(createdAt);
    } catch (ParseException parseException) {
      return null;
    }
  }

  public String getThumbnail() {
    return thumbnail;
  }

  public int getVersion() {
    return Integer.parseInt(__v);
  }

  public String get__v() {
    return __v;
  }

  @Override
  public String toString() {
    return "FileData{" +
            "resolution=" + resolution +
            ", createdBy=" + createdBy +
            ", labels=" + labels +
            ", playlists=" + playlists +
            ", groupIds=" + groupIds +
            ", installation='" + installation + '\'' +
            ", _id='" + _id + '\'' +
            ", createdAt='" + createdAt + '\'' +
            ", name='" + name + '\'' +
            ", type='" + type + '\'' +
            ", duration='" + duration + '\'' +
            ", size='" + size + '\'' +
            ", thumbnail='" + thumbnail + '\'' +
            ", __v='" + __v + '\'' +
            '}';
  }
}
