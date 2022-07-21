package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.playlists.messages.PlaylistUploadMessage;
import com.pl.donauturm.pisignageapi.model.playlists.messages.PlaylistUploadedMessage;

public class PlaylistUploadRequest extends Request<PlaylistUploadMessage, PlaylistUploadedMessage> {


  public PlaylistUploadRequest(PlaylistUploadMessage requestMessage) {
    super(requestMessage);
  }

  @Override
  protected RequestMethod requestMethod() {
    return RequestMethod.POST;
  }

  @Override
  protected String requestPath() {
    return "playlists";
  }

  @Override
  protected Class<? extends PlaylistUploadedMessage> provideResultClass(String body) {
    return PlaylistUploadedMessage.class;
  }
}
