package com.pl.donauturm.pisignageapi.apicontroller;

import com.pl.donauturm.pisignageapi.model.files.data.FileData;
import com.pl.donauturm.pisignageapi.model.files.data.FileResolution;
import com.pl.donauturm.pisignageapi.model.files.data.FileUpdate;
import com.pl.donauturm.pisignageapi.model.files.data.FileValidity;
import com.pl.donauturm.pisignageapi.model.files.messages.FileInfoUpdateMessage;
import com.pl.donauturm.pisignageapi.requests.FileInfoRequest;
import com.pl.donauturm.pisignageapi.requests.FileInfoUpdateRequest;

import java.util.List;

public class FileUpdateAction {

  private final boolean async;
  private FileData fileData;
  private FileUpdate updateData;
  private boolean send = false;
  private boolean dataHere = false;

  public FileUpdateAction(String name) {
    this.async = false;
    this.fileData = new FileInfoRequest(name).request().getData().getDbdata();
    this.updateData = fromFileData(fileData);
  }

  public FileUpdateAction(String name, boolean async) {
    this.async = async;
    if (async) {
      new FileInfoRequest(name).requestAsync(response -> {
        this.fileData = response.getData().getDbdata();
        this.updateData = fillFileData(fileData);
        this.dataHere = true;
        if (send) update();
      });
      this.updateData = fromFileData(new FileData());
    } else {
      this.fileData = new FileInfoRequest(name).request().getData().getDbdata();
      this.updateData = fromFileData(fileData);
      this.dataHere = true;
    }
  }

  public void update(){
    if (async) {
      send = true;
      if (dataHere)
        new FileInfoUpdateRequest(new FileInfoUpdateMessage(updateData)).requestAsync(null);
      return;
    }
    new FileInfoUpdateRequest(new FileInfoUpdateMessage(updateData)).request();
  }

  public FileUpdateAction setName(String name) {
    updateData.name = name;
    return this;
  }

  public FileUpdateAction setType(String type) {
    updateData.type = type;
    return this;
  }

  public FileUpdateAction setDuration(int duration) {
    updateData.duration = duration;
    return this;
  }

  public FileUpdateAction setSize(String size) {
    updateData.size = size;
    return this;
  }

  public FileUpdateAction setThumbnail(String thumbnail) {
    updateData.thumbnail = thumbnail;
    return this;
  }

  public FileUpdateAction setValidity(FileValidity validity) {
    updateData.validity = validity;
    return this;
  }

  public FileUpdateAction setGroupIds(List<String> groupIds) {
    updateData.groupIds = groupIds;
    return this;
  }

  public FileUpdateAction setPlaylists(List<String> playlists) {
    updateData.playlists = playlists;
    return this;
  }

  public FileUpdateAction setLabels(List<String> labels) {
    updateData.labels = labels;
    return this;
  }

  public FileUpdateAction setResolution(FileResolution resolution) {
    updateData.resolution = resolution;
    return this;
  }

  private FileUpdate fromFileData(FileData data) {
    final FileUpdate update = new FileUpdate();
    update.setName(data.getName());
    update.setSize(data.getSize());
    update.setType(data.getType());
    update.setThumbnail(data.getThumbnail());
    update.setResolution(data.getResolution());
    update.setGroupIds(data.getGroupIds());
    update.setLabels(data.getLabels());
    update.setPlaylists(data.getPlaylists());
    update.setValidity(new FileValidity());
    update.setDuration(Integer.parseInt(data.getDuration() != null && !data.getDuration().isEmpty() ? data.getDuration() : "0"));
    return update;
  }

  private FileUpdate fillFileData(FileData data) {
    FileUpdate current = updateData;
    FileUpdate incoming = fromFileData(data);
    if (current == null) return incoming;
    if (current.getName() == null)
      current.setName(incoming.getName());
    if (current.getSize() == null)
      current.setSize(incoming.getSize());
    if (current.getType() == null)
      current.setType(incoming.getType());
    if (current.getThumbnail() == null)
      current.setThumbnail(incoming.getThumbnail());
    if (current.getResolution() == null)
      current.setResolution(incoming.getResolution());
    if (current.getGroupIds() == null)
      current.setGroupIds(incoming.getGroupIds());
    if (current.getLabels() == null)
      current.setLabels(incoming.getLabels());
    if (current.getPlaylists() == null)
      current.setPlaylists(incoming.getPlaylists());
    if (current.getValidity() == null)
      current.setValidity(incoming.getValidity());
    if (current.getDuration() == 0)
      current.setDuration(incoming.duration);
    return current;
  }

}
