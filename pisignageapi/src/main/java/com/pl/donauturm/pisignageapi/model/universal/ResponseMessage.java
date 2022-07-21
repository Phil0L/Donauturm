package com.pl.donauturm.pisignageapi.model.universal;

public interface ResponseMessage<T> {

  String getStatusMessage();
  T getData();
  boolean isSuccess();

}
