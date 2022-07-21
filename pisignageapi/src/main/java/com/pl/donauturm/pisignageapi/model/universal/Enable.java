package com.pl.donauturm.pisignageapi.model.universal;

public class Enable {

  public boolean enable;

  public Enable(boolean enable) {
    this.enable = enable;
  }

  public boolean isEnable() {
    return enable;
  }

  @Override
  public String toString() {
    return "Enable{" +
           "enable=" + enable +
           '}';
  }
}
