package com.pl.donauturm.pisignageapi.model.playlists.data;

import com.pl.donauturm.pisignageapi.model.Playlist;

import java.util.List;
import java.util.stream.Collectors;

public class PlaylistData implements Playlist {

  public PlaylistSettings settings;
  public List<PlaylistAsset> assets;
  public String name;
  public String layout;
  public String templateName;
  public Object videoWindow;
  public Object zoneVideoWindow;
  public Object schedule;
  public List<String> labels;
  public List<String> groupIds;
  public List<String> belongsTo;
  public int version;

  public PlaylistData(PlaylistSettings settings, List<PlaylistAsset> assets, String name, String layout, String templateName, Object videoWindow, Object zoneVideoWindow, Object schedule, List<String> labels, List<String> groupIds, List<String> belongsTo) {
    this.settings = settings;
    this.assets = assets;
    this.name = name;
    this.layout = layout;
    this.templateName = templateName;
    this.videoWindow = videoWindow;
    this.zoneVideoWindow = zoneVideoWindow;
    this.schedule = schedule;
    this.labels = labels;
    this.groupIds = groupIds;
    this.belongsTo = belongsTo;
  }

  public PlaylistData() {
  }

  public PlaylistSettings getSettings() {
    return settings;
  }

  public List<PlaylistAsset> getAssets() {
    return assets;
  }

  public List<String> getAssetNames(){
    return assets.stream().map(PlaylistAsset::getFilename).collect(Collectors.toList());
  }

  public String getName() {
    return name;
  }

  public String getLayout() {
    return layout;
  }

  public String getTemplateName() {
    return templateName;
  }

  public Object getVideoWindow() {
    return videoWindow;
  }

  public Object getZoneVideoWindow() {
    return zoneVideoWindow;
  }

  public Object getSchedule() {
    return schedule;
  }

  public List<String> getLabels() {
    return labels;
  }

  public Object getGroupIds() {
    return groupIds;
  }

  public List<String> getBelongsTo() {
    return belongsTo;
  }

  public int getVersion() {
    return version;
  }

  @Override
  public String toString() {
    return "PlaylistData{" +
           "settings=" + settings +
           ", assets=" + assets +
           ", name='" + name + '\'' +
           ", layout='" + layout + '\'' +
           ", templateName='" + templateName + '\'' +
           ", videoWindow=" + videoWindow +
           ", zoneVideoWindow=" + zoneVideoWindow +
           ", schedule=" + schedule +
           ", labels=" + labels +
           ", groupIds=" + groupIds +
           ", belongsTo=" + belongsTo +
           ", version=" + version +
           '}';
  }
}
