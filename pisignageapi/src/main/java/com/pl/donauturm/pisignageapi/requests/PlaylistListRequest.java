package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.playlists.messages.PlaylistListInfoMessage;

public class PlaylistListRequest extends Request<Void, PlaylistListInfoMessage> {


  public PlaylistListRequest() {
    super(null);
  }

  @Override
  protected RequestMethod requestMethod() {
    return RequestMethod.GET;
  }

  @Override
  protected String requestPath() {
    return "playlists";
  }

  @Override
  protected Class<? extends PlaylistListInfoMessage> provideResultClass(String body) {
    return PlaylistListInfoMessage.class;
  }
}
