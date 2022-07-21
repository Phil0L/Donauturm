package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.universal.EmptyResponseMessage;

public class GroupDeleteRequest extends Request<Void, EmptyResponseMessage> {

  private final String id;

  public GroupDeleteRequest(String id) {
    super(null);
    this.id = id;
  }

  @Override
  protected RequestMethod requestMethod() {
    return RequestMethod.DELETE;
  }

  @Override
  protected String requestPath() {
    return "groups/" + id;
  }

  @Override
  protected Class<? extends EmptyResponseMessage> provideResultClass(String body) {
    return EmptyResponseMessage.class;
  }
}
