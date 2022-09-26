package com.pl.donauturm.pisignageapi.apicontroller;

import com.pl.donauturm.pisignageapi.model.Asset;
import com.pl.donauturm.pisignageapi.model.Notice;
import com.pl.donauturm.pisignageapi.model.files.data.NoticeData;
import com.pl.donauturm.pisignageapi.model.files.messages.FileInfoMessage;
import com.pl.donauturm.pisignageapi.model.files.messages.FileListInfoMessage;
import com.pl.donauturm.pisignageapi.model.session.messages.LoginMessage;
import com.pl.donauturm.pisignageapi.requests.*;
import com.pl.donauturm.pisignageapi.util.ConnectionManager;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class PiSignageAPI {

  protected String username;
  protected String password;

  public AsyncPiSignageAPI asynchronous;

  protected PiSignageAPI(String username, String password){
    this.username = username;
    this.password = password;
    this.asynchronous = new AsyncPiSignageAPI(this);
    new ConnectionManager(username, password);
  }

  public static PiSignageAPI simple(String username, String password){
    return new PiSignageAPI(username, password);
  }

  public static Builder builder(){
    return new Builder();
  }

  public String getUsername() {
    return username;
  }

  public void login(){
    new SessionRequest(new LoginMessage(username, password)).request();
  }

  public boolean isLoggedIn(){
    return ConnectionManager.single().hasToken();
  }

  public void logout(){
    new SessionRemoveRequest().request();
  }

  public List<Asset> getAllAssets(){
    FileListInfoMessage flim = new FileListRequest().request();
    return flim.getData().getDbdata().stream().map(a -> (Asset) a).collect(Collectors.toList());
  }

  public Asset getAsset(String assetName){
    FileInfoMessage fim = new FileInfoRequest(assetName).request();
    return fim.getData().getDbdata();
  }

  public FileUploadAction uploadAsset(File asset){
    return new FileUploadAction(asset);
  }

  public FileUpdateAction updateAsset(String assetName){
    return new FileUpdateAction(assetName);
  }

  public void deleteAsset(String assetName){
    new FileDeleteRequest(assetName).request();
  }

  public void getAssetImage(String assetName, File destination){
    String url = Request.PROTOCOL + "://" + Request.HOST + "/media/" + getUsername() + "/" + assetName;
    new FileDownloadRequest(url, destination).request();
  }

  public void createNotice(Notice notice){
    new NoticeUploadRequest((NoticeData) notice).request();
  }

  public static class Builder {

    private String username;
    private String password;

    public Builder username(String username) {
      this.username = username;
      return this;
    }

    public Builder password(String password) {
      this.password = password;
      return this;
    }

    public Builder host(String host){
      Request.HOST = host;
      return this;
    }

    public PiSignageAPI build(){
      return new PiSignageAPI(username, password);
    }
  }

}
