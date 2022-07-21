package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.groups.messages.GroupInfoMessage;

public class GroupInfoRequest extends Request<Void, GroupInfoMessage>{

  private final String id;

  public GroupInfoRequest(String id) {
    super(null);
    this.id = id;
  }

  @Override
  protected RequestMethod requestMethod() {
    return RequestMethod.GET;
  }

  @Override
  protected String requestPath() {
    return "groups/" + id;
  }

  @Override
  protected Class<? extends GroupInfoMessage> provideResultClass(String body) {
    return GroupInfoMessage.class;
  }
}
