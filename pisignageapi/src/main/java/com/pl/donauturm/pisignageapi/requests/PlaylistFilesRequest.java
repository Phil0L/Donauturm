package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.playlists.messages.PlaylistFilesMessage;
import com.pl.donauturm.pisignageapi.model.universal.EmptyResponseMessage;

public class PlaylistFilesRequest extends Request<PlaylistFilesMessage, EmptyResponseMessage> {


  public PlaylistFilesRequest(PlaylistFilesMessage requestMessage) {
    super(requestMessage);
  }

  @Override
  protected RequestMethod requestMethod() {
    return RequestMethod.POST;
  }

  @Override
  protected String requestPath() {
    return "playlistfiles";
  }

  @Override
  protected Class<? extends EmptyResponseMessage> provideResultClass(String body) {
    return EmptyResponseMessage.class;
  }
}
