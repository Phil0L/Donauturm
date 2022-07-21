package com.pl.donauturm.pisignageapi.model.files.data;

public class FileValidity {

  public boolean enable;
  public String startdate;
  public String enddate;
  public int starthour;
  public int endhour;

  public FileValidity(boolean enable, String startdate, String enddate, int starthour, int endhour) {
    this.enable = enable;
    this.startdate = startdate;
    this.enddate = enddate;
    this.starthour = starthour;
    this.endhour = endhour;
  }

  public FileValidity() {
  }

  public boolean isEnable() {
    return enable;
  }

  public void setEnable(boolean enable) {
    this.enable = enable;
  }

  public String getStartdate() {
    return startdate;
  }

  public void setStartdate(String startdate) {
    this.startdate = startdate;
  }

  public String getEnddate() {
    return enddate;
  }

  public void setEnddate(String enddate) {
    this.enddate = enddate;
  }

  public int getStarthour() {
    return starthour;
  }

  public void setStarthour(int starthour) {
    this.starthour = starthour;
  }

  public int getEndhour() {
    return endhour;
  }

  public void setEndhour(int endhour) {
    this.endhour = endhour;
  }

  @Override
  public String toString() {
    return "FileValidity{" +
           "enable=" + enable +
           ", startdate='" + startdate + '\'' +
           ", enddate='" + enddate + '\'' +
           ", starthour=" + starthour +
           ", endhour=" + endhour +
           '}';
  }
}
