package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.files.messages.FileDeletedMessage;

public class PlaylistDeleteRequest extends Request<Void, FileDeletedMessage> {

  private final String playlistName;

  public PlaylistDeleteRequest(String playlistName) {
    super(null);
    this.playlistName = playlistName;
  }

  @Override
  protected RequestMethod requestMethod() {
    return RequestMethod.DELETE;
  }

  @Override
  protected String requestPath() {
    return "files/__<" + playlistName + ">.json";
  }

  @Override
  protected Class<? extends FileDeletedMessage> provideResultClass(String body) {
    return FileDeletedMessage.class;
  }
}
