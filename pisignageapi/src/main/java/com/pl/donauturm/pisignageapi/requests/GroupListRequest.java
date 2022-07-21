package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.groups.messages.GroupListInfoMessage;

public class GroupListRequest extends Request<Void, GroupListInfoMessage>{


  public GroupListRequest() {
    super(null);
  }

  @Override
  protected RequestMethod requestMethod() {
    return RequestMethod.GET;
  }

  @Override
  protected String requestPath() {
    return "groups";
  }

  @Override
  protected Class<? extends GroupListInfoMessage> provideResultClass(String body) {
    return GroupListInfoMessage.class;
  }
}
