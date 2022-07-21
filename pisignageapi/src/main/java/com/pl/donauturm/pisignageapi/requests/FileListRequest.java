package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.files.messages.FileListInfoMessage;

public class FileListRequest extends Request<Void, FileListInfoMessage>{


  public FileListRequest() {
    super(null);
  }

  @Override
  protected RequestMethod requestMethod() {
    return RequestMethod.GET;
  }

  @Override
  protected String requestPath() {
    return "files";
  }

  @Override
  protected Class<? extends FileListInfoMessage> provideResultClass(String body) {
    return FileListInfoMessage.class;
  }
}
