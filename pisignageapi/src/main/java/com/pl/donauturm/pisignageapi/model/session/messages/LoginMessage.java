package com.pl.donauturm.pisignageapi.model.session.messages;

public class LoginMessage {

  public String email;
  public String password;
  public boolean getToken;

  public LoginMessage(String email, String password, boolean getToken) {
    this.email = email;
    this.password = password;
    this.getToken = getToken;
  }

  public LoginMessage(String email, String password) {
    this.email = email;
    this.password = password;
    this.getToken = true;
  }

  public LoginMessage() {
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isGetToken() {
    return getToken;
  }

  public void setGetToken(boolean getToken) {
    this.getToken = getToken;
  }

  @Override
  public String toString() {
    return "LoginMessage{" +
           "email='" + email + '\'' +
           ", password='" + password + '\'' +
           ", getToken=" + getToken +
           '}';
  }
}
