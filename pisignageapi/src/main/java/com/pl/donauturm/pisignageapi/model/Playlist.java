package com.pl.donauturm.pisignageapi.model;

import java.util.List;

public interface Playlist {

  String getName();

  List<String> getAssetNames();

  List<String> getLabels();

  int getVersion();

}
