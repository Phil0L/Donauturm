package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.files.messages.FileUploadedMessage;
import com.pl.donauturm.pisignageapi.util.ConnectionManager;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FileUploadRequest extends Request<File, FileUploadedMessage>{

  private final MultipartBody mpb;

  public FileUploadRequest(@NotNull File file) {
    super(null);
    final int i = file.getName().lastIndexOf('.');
    final String extension = file.getName().substring(i + 1);
    this.mpb = new MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("file", file.getName(),
                    RequestBody.create(file, MediaType.parse("image/" + extension)))
            .build();
  }

  @Override
  protected RequestMethod requestMethod() {
    return RequestMethod.POST;
  }

  @Override
  protected String requestPath() {
    return "files";
  }

  @Override
  protected Class<? extends FileUploadedMessage> provideResultClass(String body) {
    return FileUploadedMessage.class;
  }

  @Override
  public RequestBody bodyPublisher() {
    return mpb;
  }

  @Override
  public Headers headers() {
    Map<String, String> map = new HashMap<>();
    map.put("accept", "application/json");
    map.put("Content-type", "multipart/form-data; boundary=" + mpb.boundary());
    map.put("x-access-token", ConnectionManager.single().getToken());
    return Headers.of(map);
  }
}
