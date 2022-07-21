package com.pl.donauturm.pisignageapi.requests;

import com.google.gson.Gson;
import com.pl.donauturm.pisignageapi.util.ConnectionManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.*;
import java.util.function.BiPredicate;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class Request<Q, S>{

  public static final String PROTOCOL = "https";
  public static       String HOST = "philippletschka.pisignage.com";
  public static final List<String> PATH = Collections.singletonList("api");
  public static final BiPredicate<String,String> ACCEPT_ALL = (x, y) -> true;

  private final Q requestMessage;
  private final Gson gson = new Gson();

  public Request(Q requestMessage) {
    this.requestMessage = requestMessage;
    if (requiresToken()) ConnectionManager.single().getToken();
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

  public Headers headers() {
    Map<String, String> map = new HashMap<>();
    map.put("accept", "application/json");
    map.put("Content-type", "application/json");
    if (requiresToken()) {
      map.put("x-access-token", ConnectionManager.single().getToken());
    }
    return Headers.of(map);
  }

  protected boolean requiresToken(){
    return true;
  }

  protected abstract Class<? extends S> provideResultClass(String body);

  public S request() {
    logOutgoing();
    OkHttpClient client = ConnectionManager.single().getClient();
    okhttp3.Request request = new okhttp3.Request.Builder()
        .url(uri())
        .method(method(), bodyPublisher())
        .headers(headers())
        .build();
    try (Response response = client.newCall(request).execute()){
      if (response.code() < 200 || response.code() >= 300)
        throw new IOException("Unsupported status code: " + response.code());
      final ResponseBody body = response.body();
      if (body == null)
        throw new IOException("Response body is null");
      final String responseBody = body.string();
      Class<? extends S> resultClass = provideResultClass(responseBody);
      logIncoming(responseBody);
      if (resultClass != null) {
        S re = gson.fromJson(responseBody, resultClass);
        onResponse(re);
        return re;
      } else {
        onResponse(null);
        return null;
      }
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
            .headers(headers())
            .build();
    new Thread(() -> client.newCall(request).enqueue(new okhttp3.Callback() {

      @Override
      public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        if (response.code() < 200 || response.code() >= 300)
          throw new IOException("Unsupported status code: " + response.code());
        final ResponseBody body = response.body();
        if (body == null)
          throw new IOException("Response body is null");
        final String responseBody = body.string();
        Class<? extends S> resultClass = provideResultClass(responseBody);
        logIncoming(responseBody);
        if (resultClass != null) {
          S re = gson.fromJson(responseBody, resultClass);
          Request.this.onResponse(re);
          if (callback != null)
            callback.onResponse(re);
        }
      }

      @Override
      public void onFailure(@NotNull Call call, @NotNull IOException e) {
        e.printStackTrace();
      }

    })).start();
  }

  protected void onResponse(S response){

  }

  private void logOutgoing(){
    System.out.println("-# " + uri() + " <- " + method());
    if (requestMessage != null)
      System.out.println("-> " + gson.toJson(requestMessage));
  }

  private void logIncoming(String body){
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
