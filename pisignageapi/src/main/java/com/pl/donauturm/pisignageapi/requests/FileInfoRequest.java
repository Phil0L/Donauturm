package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.files.messages.FileInfoMessage;

public class FileInfoRequest extends Request<Void, FileInfoMessage> {

  private final String filename;

  public FileInfoRequest(String filename) {
    super(null);
    this.filename = filename;
  }

  @Override
  protected RequestMethod requestMethod() {
    return RequestMethod.GET;
  }

  @Override
  protected String requestPath() {
    return "files/" + filename;
  }

  @Override
  protected Class<? extends FileInfoMessage> provideResultClass(String body) {
    return FileInfoMessage.class;
  }
}
