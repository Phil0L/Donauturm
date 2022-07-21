package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.session.messages.LoggedinMessage;
import com.pl.donauturm.pisignageapi.model.session.messages.LoginMessage;
import com.pl.donauturm.pisignageapi.util.ConnectionManager;

public class SessionRequest extends Request<LoginMessage, LoggedinMessage> {

  public SessionRequest(LoginMessage requestMessage) {
    super(requestMessage);
  }

  @Override
  protected RequestMethod requestMethod() {
    return RequestMethod.POST;
  }

  @Override
  protected String requestPath() {
    return "session";
  }

  @Override
  protected Class<? extends LoggedinMessage> provideResultClass(String body) {
    return LoggedinMessage.class;
  }

  @Override
  protected boolean requiresToken() {
    return false;
  }

  @Override
  protected void onResponse(LoggedinMessage response) {
    ConnectionManager.single().setToken(response.getToken());
  }
}
