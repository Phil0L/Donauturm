package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.util.ConnectionManager;

public class SessionRemoveRequest extends Request<Void, Void> {


  public SessionRemoveRequest() {
    super(null);
  }

  @Override
  protected RequestMethod requestMethod() {
    return RequestMethod.DELETE;
  }

  @Override
  protected String requestPath() {
    return "session";
  }

  @Override
  protected Class<? extends Void> provideResultClass(String body) {
    return null;
  }

  @Override
  protected void onResponse(Void response) {
    ConnectionManager.single().setToken(null);
  }
}
