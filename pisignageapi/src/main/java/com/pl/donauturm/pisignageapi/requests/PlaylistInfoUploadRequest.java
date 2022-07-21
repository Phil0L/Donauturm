package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.playlists.messages.PlaylistInfoMessage;
import com.pl.donauturm.pisignageapi.model.playlists.data.PlaylistData;

public class PlaylistInfoUploadRequest extends Request<PlaylistData, PlaylistInfoMessage> {

  private final String filename;

  public PlaylistInfoUploadRequest(PlaylistData requestMessage) {
    super(requestMessage);
    this.filename = requestMessage.getName();
  }

  @Override
  protected RequestMethod requestMethod() {
    return RequestMethod.POST;
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
