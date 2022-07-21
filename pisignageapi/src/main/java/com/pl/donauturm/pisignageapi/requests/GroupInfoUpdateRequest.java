package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.groups.messages.GroupInfoMessage;
import com.pl.donauturm.pisignageapi.model.groups.data.GroupData;

public class GroupInfoUpdateRequest extends Request<GroupData, GroupInfoMessage> {

  private final String id;

  public GroupInfoUpdateRequest(GroupData requestMessage) {
    super(requestMessage);
    this.id = requestMessage.getID();
  }

  @Override
  protected RequestMethod requestMethod() {
    return RequestMethod.POST;
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
