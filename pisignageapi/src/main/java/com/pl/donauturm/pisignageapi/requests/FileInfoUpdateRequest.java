package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.files.messages.FileInfoUpdateMessage;
import com.pl.donauturm.pisignageapi.model.files.messages.FileInfoUpdatedMessage;

public class FileInfoUpdateRequest extends Request<FileInfoUpdateMessage, FileInfoUpdatedMessage> {

  private final String filename;

  public FileInfoUpdateRequest(FileInfoUpdateMessage requestMessage) {
    super(requestMessage);
    this.filename = requestMessage.getDbdata().getName();
  }

  @Override
  protected RequestMethod requestMethod() {
    return RequestMethod.POST;
  }

  @Override
  protected String requestPath() {
    return "files/" + filename;
  }

  @Override
  protected Class<? extends FileInfoUpdatedMessage> provideResultClass(String body) {
    return FileInfoUpdatedMessage.class;
  }
}
