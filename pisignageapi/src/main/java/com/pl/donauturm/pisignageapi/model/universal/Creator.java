package com.pl.donauturm.pisignageapi.model.universal;

public class Creator {

  public String _id;
  public String name;

  public Creator(String _id, String name) {
    this._id = _id;
    this.name = name;
  }

  public Creator() {
  }

  public String getId() {
    return _id;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "FileCreator{" +
           "_id='" + _id + '\'' +
           ", name='" + name + '\'' +
           '}';
  }
}
