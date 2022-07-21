package com.pl.donauturm.pisignageapi.model.session.messages;

import com.pl.donauturm.pisignageapi.model.session.data.UserInfo;

public class LoggedinMessage {

  public UserInfo userInfo;
  public String token;

  public LoggedinMessage(UserInfo userInfo, String token) {
    this.userInfo = userInfo;
    this.token = token;
  }

  public LoggedinMessage() {
  }

  public UserInfo getUserInfo() {
    return userInfo;
  }

  public String getToken() {
    return token;
  }

  @Override
  public String toString() {
    return "LoggedinMessage{" +
           "userinfo=" + userInfo +
           ", token='" + token + '\'' +
           '}';
  }
}
