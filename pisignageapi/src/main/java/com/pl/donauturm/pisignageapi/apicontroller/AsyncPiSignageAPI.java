package com.pl.donauturm.pisignageapi.apicontroller;

import com.pl.donauturm.pisignageapi.model.Asset;
import com.pl.donauturm.pisignageapi.model.Notice;
import com.pl.donauturm.pisignageapi.model.files.messages.NoticeUploadMessage;
import com.pl.donauturm.pisignageapi.model.session.messages.LoginMessage;
import com.pl.donauturm.pisignageapi.requests.FileDeleteRequest;
import com.pl.donauturm.pisignageapi.requests.FileDownloadRequest;
import com.pl.donauturm.pisignageapi.requests.FileInfoRequest;
import com.pl.donauturm.pisignageapi.requests.FileListRequest;
import com.pl.donauturm.pisignageapi.requests.NoticeDeleteRequest;
import com.pl.donauturm.pisignageapi.requests.NoticeInfoRequest;
import com.pl.donauturm.pisignageapi.requests.NoticeRenameRequest;
import com.pl.donauturm.pisignageapi.requests.NoticeUploadRequest;
import com.pl.donauturm.pisignageapi.requests.Request;
import com.pl.donauturm.pisignageapi.requests.SessionRemoveRequest;
import com.pl.donauturm.pisignageapi.requests.SessionRequest;
import com.pl.donauturm.pisignageapi.util.ConnectionManager;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class AsyncPiSignageAPI {

    public PiSignageAPI synchronous;

    protected AsyncPiSignageAPI(PiSignageAPI origin) {
        this.synchronous = origin;
    }

    public void login(APICallback<Void> cb) {
        if (!isLoggedIn())
            new SessionRequest(new LoginMessage(synchronous.username, synchronous.password)).requestAsync(rm -> cb.onData(null));
    }

    public boolean isLoggedIn() {
        return ConnectionManager.single().hasToken();
    }

    public void logout() {
        if (isLoggedIn())
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

    public FileUploadAction uploadAsset(File asset) {
        return new FileUploadAction(asset, true);
    }

    public FileUpdateAction updateAsset(String assetName) {
        return new FileUpdateAction(assetName, true);
    }

    public void deleteAsset(String assetName) {
        new FileDeleteRequest(assetName).requestAsync(null);
    }

    public void getAssetImage(String assetName, File destination, APICallback<File> cb) {
        String url = Request.PROTOCOL + "://" + Request.HOST + "/media/" + synchronous.getUsername() + "/" + assetName;
        new FileDownloadRequest(url, destination).requestAsync((v) -> cb.onData(destination));
    }

    public void getAllNoticeNames(APICallback<List<String>> cb) {
        getAllAssets(al -> {
            List<String> names = al.stream().filter(a -> a.getType().equals("notice")).map(Asset::getName).collect(Collectors.toList());
            cb.onData(names);
        });
    }

    public void createNotice(NoticeUploadMessage notice, APICallback<String> cb) {
        new NoticeUploadRequest(notice).requestAsync(v -> getAllNoticeNames(nn -> {
            for (String noticeName : nn) {
                Notice noticeG = synchronous.getNotice(noticeName);
                if (notice.getFormdata().getTitle().equals(noticeG.getTitle())) {
                    cb.onData(noticeName);
                    return;
                }
            }
        }));
    }

    public void getNotice(String noticeName, APICallback<Notice> cb) {
        new NoticeInfoRequest(noticeName).requestAsync(nim -> {
            if (cb != null) {
                if (nim != null && nim.getData() != null && nim.getData().getData() != null)
                    cb.onData(nim.getData().getData());
                else
                    cb.onData(null);
            }
        });
    }

    public void renameNotice(String noticeName, String newNoticeName, APICallback<Void> cb) {
        new NoticeRenameRequest(noticeName, newNoticeName).requestAsync(v -> cb.onData(null));
    }

    public void deleteNotice(String noticeName, APICallback<Void> cb) {
        new NoticeDeleteRequest(noticeName).requestAsync(s -> cb.onData(null));
    }

    public interface APICallback<T> {
        void onData(T data);
    }
}
