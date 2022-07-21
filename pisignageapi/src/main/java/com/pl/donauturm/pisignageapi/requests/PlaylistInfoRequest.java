package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.playlists.messages.PlaylistInfoMessage;

public class PlaylistInfoRequest extends Request<Void, PlaylistInfoMessage> {

  private final String filename;

  public PlaylistInfoRequest(String playlistName) {
    super(null);
    this.filename = playlistName;
  }

  @Override
  protected RequestMethod requestMethod() {
    return RequestMethod.GET;
  }

  @Override
  protected String requestPath() {
    return "playlists/" + filename;
  }

  @Override
  protected Class<? extends PlaylistInfoMessage> provideResultClass(String body) {
    return PlaylistInfoMessage.class;
  }
}
