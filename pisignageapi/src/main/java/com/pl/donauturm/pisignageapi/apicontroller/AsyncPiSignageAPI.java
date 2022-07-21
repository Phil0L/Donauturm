package com.pl.donauturm.pisignageapi.apicontroller;

import com.pl.donauturm.pisignageapi.model.Asset;
import com.pl.donauturm.pisignageapi.model.session.messages.LoginMessage;
import com.pl.donauturm.pisignageapi.requests.*;
import com.pl.donauturm.pisignageapi.util.ConnectionManager;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class AsyncPiSignageAPI {

  public PiSignageAPI synchronous;

  AsyncPiSignageAPI(PiSignageAPI origin){
    this.synchronous = origin;
  }

  public void login(){
    new SessionRequest(new LoginMessage(synchronous.username, synchronous.password)).requestAsync(null);
  }

  public boolean isLoggedIn(){
    return ConnectionManager.single().hasToken();
  }

  public void logout(){
    new SessionRemoveRequest().requestAsync(null);
  }

  public void getAllAssets(APICallback<List<Asset>> cb) {
    new FileListRequest().requestAsync(response -> {
      List<Asset> assets = response.getData().getDbdata().stream().map(a -> (Asset) a).collect(Collectors.toList());
      cb.onData(assets);
    });
  }

  public void getAsset(String assetName, APICallback<Asset> cb) {
    new FileInfoRequest(assetName).requestAsync(response -> {
      Asset asset = response.getData().getDbdata();
      cb.onData(asset);
    });
  }

  public FileUploadAction uploadAsset(File asset){
    return new FileUploadAction(asset, true);
  }

  public FileUpdateAction updateAsset(String assetName){
    return new FileUpdateAction(assetName, true);
  }

  public void deleteAsset(String assetName){
    new FileDeleteRequest(assetName).requestAsync(null);
  }

  public interface APICallback<T>{
    void onData(T data);
  }
}
