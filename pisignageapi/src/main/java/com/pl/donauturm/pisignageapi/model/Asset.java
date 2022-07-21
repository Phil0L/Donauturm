package com.pl.donauturm.pisignageapi.model;

import com.pl.donauturm.pisignageapi.model.files.data.FileResolution;
import com.pl.donauturm.pisignageapi.model.universal.Creator;

import java.util.Date;
import java.util.List;

public interface Asset {

  String getID();

  String getName();

  String getPath();

  String getType();

  String getSize();

  Date getCreationTime();

  List<String> getLabels();

  FileResolution getResolution();

  Creator getCreatedBy();

  int getVersion();

}
