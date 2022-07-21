package com.pl.donauturm.pisignageapi.model.session.data;

public class UserZoneAliases {

  public String side;
  public String bottom;
  public String zone4;
  public String zone5;
  public String zone6;

  public UserZoneAliases(String side, String bottom, String zone4, String zone5, String zone6) {
    this.side = side;
    this.bottom = bottom;
    this.zone4 = zone4;
    this.zone5 = zone5;
    this.zone6 = zone6;
  }

  public UserZoneAliases() {
  }

  public String getSide() {
    return side;
  }

  public String getBottom() {
    return bottom;
  }

  public String getZone4() {
    return zone4;
  }

  public String getZone5() {
    return zone5;
  }

  public String getZone6() {
    return zone6;
  }

  @Override
  public String toString() {
    return "UserSettingZoneAliases{" +
           "side='" + side + '\'' +
           ", bottom='" + bottom + '\'' +
           ", zone4='" + zone4 + '\'' +
           ", zone5='" + zone5 + '\'' +
           ", zone6='" + zone6 + '\'' +
           '}';
  }
}
