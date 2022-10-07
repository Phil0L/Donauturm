package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.session.messages.LoggedinMessage;
import com.pl.donauturm.pisignageapi.model.session.messages.LoginMessage;
import com.pl.donauturm.pisignageapi.util.ConnectionManager;

import java.io.IOException;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class SessionRequest extends Request<LoginMessage, LoggedinMessage> {

  public SessionRequest(LoginMessage requestMessage) {
    super(requestMessage);
  }

  @Override
  protected RequestMethod requestMethod() {
    return RequestMethod.POST;
  }

  @Override
  protected String requestPath() {
    return "session";
  }

  @Override
  protected Class<? extends LoggedinMessage> provideResultClass(String body) {
    return LoggedinMessage.class;
  }

  @Override
  protected boolean requiresToken() {
    return false;
  }

  @Override
  protected void onResponse(LoggedinMessage response) {
    ConnectionManager.single().setToken(response.getToken());
  }

  public Response requestResponse(CookieJar cookieJar) {
    logOutgoing();
    OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();
    okhttp3.Request request = new okhttp3.Request.Builder()
            .url(uri())
            .method(method(), bodyPublisher())
            .headers(headers())
            .build();
    try (Response response = client.newCall(request).execute()){
      if (response.code() < 200 || response.code() >= 300)
        throw new IOException("Unsupported status code: " + response.code());
      return response;
    } catch (IOException ioException) {
      ioException.printStackTrace();
      return null;
    }
  }
}
