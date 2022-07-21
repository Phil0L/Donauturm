package com.pl.donauturm.pisignageapi.model.groups.data;

import com.pl.donauturm.pisignageapi.model.GroupPlaylist;
import com.pl.donauturm.pisignageapi.model.playlists.data.PlaylistSettings;

public class GroupPlaylistData implements GroupPlaylist {

  public String name;
  public PlaylistSettings settings;
  public boolean skipForSchedule;
  public String plType;

  public GroupPlaylistData(String name, PlaylistSettings settings, boolean skipForSchedule, String plType) {
    this.name = name;
    this.settings = settings;
    this.skipForSchedule = skipForSchedule;
    this.plType = plType;
  }

  public GroupPlaylistData() {
  }

  public String getName() {
    return name;
  }

  public PlaylistSettings getSettings() {
    return settings;
  }

  public boolean isSkipForSchedule() {
    return skipForSchedule;
  }

  public String getPlType() {
    return plType;
  }

  @Override
  public String toString() {
    return "GroupPlaylistData{" +
           "name='" + name + '\'' +
           ", settings=" + settings +
           ", skipForSchedule=" + skipForSchedule +
           ", plType='" + plType + '\'' +
           '}';
  }
}
