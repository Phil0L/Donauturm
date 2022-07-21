package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.groups.messages.GroupInfoMessage;
import com.pl.donauturm.pisignageapi.model.groups.data.GroupData;

public class GroupUploadRequest extends Request<GroupData, GroupInfoMessage> {


  public GroupUploadRequest(GroupData requestMessage) {
    super(requestMessage);
  }

  @Override
  protected RequestMethod requestMethod() {
    return RequestMethod.POST;
  }

  @Override
  protected String requestPath() {
    return "groups";
  }

  @Override
  protected Class<? extends GroupInfoMessage> provideResultClass(String body) {
    return GroupInfoMessage.class;
  }
}
