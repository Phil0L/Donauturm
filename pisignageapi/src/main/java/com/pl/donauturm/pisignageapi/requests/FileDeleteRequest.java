package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.files.messages.FileDeletedMessage;

public class FileDeleteRequest extends Request<Void, FileDeletedMessage> {

  private final String filename;

  public FileDeleteRequest(String filename) {
    super(null);
    this.filename = filename;
  }

  @Override
  protected RequestMethod requestMethod() {
    return RequestMethod.DELETE;
  }

  @Override
  protected String requestPath() {
    return "files/" + filename;
  }

  @Override
  protected Class<? extends FileDeletedMessage> provideResultClass(String body) {
    return FileDeletedMessage.class;
  }
}
