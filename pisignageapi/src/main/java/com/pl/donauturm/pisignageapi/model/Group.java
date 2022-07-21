package com.pl.donauturm.pisignageapi.model;

import com.pl.donauturm.pisignageapi.model.universal.Creator;

import java.util.Date;
import java.util.List;

public interface Group {

  String getID();

  String getName();

  List<GroupPlaylist> getPlaylists();

  List<String> getAssetNames();

  Creator getCreatedBy();

  Date getCreationTime();

  List<String> getLabels();

  int getVersion();

}
