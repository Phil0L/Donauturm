package com.pl.donauturm.pisignageapi.model.session.data;

public class UserInfo {

  public String username;
  public String email;
  public String role;
  public String provider;
  public UserSettings settings;
  public String _id;

  public UserInfo(String username, String email, String role, String provider, UserSettings settings, String _id) {
    this.username = username;
    this.email = email;
    this.role = role;
    this.provider = provider;
    this.settings = settings;
    this._id = _id;
  }

  public UserInfo() {
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public String getRole() {
    return role;
  }

  public String getProvider() {
    return provider;
  }

  public UserSettings getSettings() {
    return settings;
  }

  public String getId() {
    return _id;
  }

  @Override
  public String toString() {
    return "UserInfo{" +
           "username='" + username + '\'' +
           ", email='" + email + '\'' +
           ", role='" + role + '\'' +
           ", provider='" + provider + '\'' +
           ", settings=" + settings +
           ", _id='" + _id + '\'' +
           '}';
  }
}
