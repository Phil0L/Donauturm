package com.pl.donauturm.spotify.customrequests;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class Request<Q, S>{

  public static final String PROTOCOL = "https";
  public static       String HOST = "philippletschka.pisignage.com";
  public static final String SUBDOMAIN = "philippletschka";
  public static final List<String> PATH = Collections.singletonList("api");
  public static       boolean LAZY_ASYNC_LOGIN = false;
  public static       String lazyLoginUsername, lazyLoginPassword;
  public static final BiPredicate<String,String> ACCEPT_ALL = (x, y) -> true;

  private final Q requestMessage;
  private final Gson gson = new Gson();

  public Request(Q requestMessage) {
    this.requestMessage = requestMessage;
  }

  public static void enableLazyAsyncLogin(String username, String password) {
    lazyLoginUsername = username;
    lazyLoginPassword = password;
    LAZY_ASYNC_LOGIN = true;
  }

  public RequestBody bodyPublisher() {
    if (requestMessage == null) return null;
    return RequestBody.create(gson.toJson(requestMessage), MediaType.get("application/json; charset=utf-8"));
  }

  public String method() {
    return requestMethod().asString();
  }

  protected abstract RequestMethod requestMethod();

  @Deprecated
  public Optional<Duration> timeout() {
    return Optional.of(Duration.ofSeconds(20));
  }

  public String uri() {
    List<String> newList = new ArrayList<>(PATH);
    if (requestPath() != null && !requestPath().isEmpty())
      newList.add(requestPath());
    String[] fullPath = newList.toArray(new String[0]);
    String pathString = "/" + String.join("/", fullPath);
    return URI.create(PROTOCOL + "://" + HOST + pathString).toString();
  }

  protected abstract String requestPath();

  protected abstract Class<? extends S> provideResultClass(String body);

  public S request() {
    logOutgoing();
    OkHttpClient client = ConnectionManager.single().getClient();
    okhttp3.Request request = new okhttp3.Request.Builder()
        .url(uri())
        .method(method(), bodyPublisher())
        .build();
    try (Response response = client.newCall(request).execute()){
      if (response.code() < 200 || response.code() >= 300)
        throw new IOException("Unsupported status code: " + response.code());
      final ResponseBody body = response.body();
      if (body == null)
        throw new IOException("Response body is null");
      S re = parseResult(body);
      Request.this.onResponse(re);
      body.close();
      return re;
    } catch (IOException ioException) {
      ioException.printStackTrace();
      return null;
    }
  }

  public void requestAsync(ResponseCallback<S> callback){
    logOutgoing();
    OkHttpClient client = ConnectionManager.single().getClient();
    okhttp3.Request request = new okhttp3.Request.Builder()
            .url(uri())
            .method(method(), bodyPublisher())
            .build();
    new Thread(() -> client.newCall(request).enqueue(new okhttp3.Callback() {

      @Override
      public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        if (response.code() < 200 || response.code() >= 300)
          throw new IOException("Unsupported status code: " + response.code());
        final ResponseBody body = response.body();
        if (body == null)
          throw new IOException("Response body is null");
        S re = parseResult(body);
        Request.this.onResponse(re);
        body.close();
        if (callback != null)
          callback.onResponse(re);
      }

      @Override
      public void onFailure(@NotNull Call call, @NotNull IOException e) {
        e.printStackTrace();
      }

    })).start();
  }

  protected S parseResult(ResponseBody response){
    try {
      String responseBody = response.string();
      logIncoming(responseBody);
      Class<? extends S> resultClass = provideResultClass(responseBody);
      if (resultClass != null) {
        return gson.fromJson(responseBody, resultClass);
      }
    } catch (IOException | JsonSyntaxException e) {
      e.printStackTrace();
    }
    return null;
  }

  protected void onResponse(S response){

  }

  protected void logOutgoing(){
    System.out.println("-# " + uri() + " <- " + method());
    if (requestMessage != null)
      System.out.println("-> " + gson.toJson(requestMessage));
  }

  protected void logIncoming(String body){
    if (body != null && !body.isEmpty())
      System.out.println("<- " + body);
  }

  public enum RequestMethod {
    GET, POST, DELETE;

    public String asString() {
      return this.toString().toUpperCase();
    }
  }

  public interface ResponseCallback<S>{
    void onResponse(S response);
  }
}
