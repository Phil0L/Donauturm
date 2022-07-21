package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.files.messages.FileInfoUploadMessage;

public class FileInfoUploadRequest extends Request<FileInfoUploadMessage, Void> {


  public FileInfoUploadRequest(FileInfoUploadMessage requestMessage) {
    super(requestMessage);
  }

  @Override
  protected RequestMethod requestMethod() {
    return RequestMethod.POST;
  }

  @Override
  protected String requestPath() {
    return "postupload";
  }

  @Override
  protected Class<? extends Void> provideResultClass(String body) {
    return null;
  }


}
