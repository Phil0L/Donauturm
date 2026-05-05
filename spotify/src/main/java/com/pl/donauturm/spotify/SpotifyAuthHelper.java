package com.pl.donauturm.spotify;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.MainThread;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;


public class SpotifyAuthHelper {

    private static final String[] SCOPES = new String[]
            {"user-read-private", "streaming", "user-top-read", "user-read-recently-played",
                    "app-remote-control", "user-read-playback-state", "user-modify-playback-state",
                    "user-read-currently-playing", "user-read-playback-position", "user-read-email"};
    private final String clientID;
    private final String clientSecret;
    private final String redirectUri;
    private final String username;
    private final SpotifyApi api;
    private final RefreshTokenHelper refreshTokenHelper;
    private final AutomaticTokenRefresh automaticTokenRefresh;
    private final Handler handler;

    private AuthenticationCallback callback;
    private WebView mWebView;

    public SpotifyAuthHelper(AuthData authData){
        this(authData.clientID, authData.clientSecret, authData.redirectUri, authData.username);
    }

    public SpotifyAuthHelper(String clientID, String clientSecret, String redirectUri, String username) {
        this.refreshTokenHelper = RefreshTokenHelper.getInstance();
        this.automaticTokenRefresh = new AutomaticTokenRefresh();
        this.handler = new Handler(Looper.getMainLooper());
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.username = username;
        this.api = SpotifyApi.builder()
                .setClientId(clientID)
                .setClientSecret(clientSecret)
                .setRedirectUri(SpotifyHttpManager.makeUri(redirectUri))
                .build();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @MainThread
    public void setAndInitWebView(WebView webView) {
        setWebView(webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setVisibility(View.GONE);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.setVisibility(View.VISIBLE);
                view.loadUrl(request.getUrl().toString());
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (url == null) return;
//                Log.i(getTag(), "Finished loading URL: " + url);
                if (url.startsWith("https://accounts.spotify.com")) {
                    CookieManager.getInstance().setCookie("https://accounts.spotify.com", "remember=" + getUsername());
                    CookieManager.getInstance().flush();
                }
                if (url.startsWith(getRedirectUri())) {
                    view.loadUrl("about:blank");
                    onCode(url.substring(url.indexOf("code=") + 5));
                }
            }
        });
    }

    public void setWebView(WebView webView) {
        mWebView = webView;
    }

    public void authenticate(AuthenticationCallback callback){
        this.authenticate(() -> {}, callback);
    }

    public void authenticate(LoginCallback loginCallback, AuthenticationCallback callback) {
        this.callback = callback;
        if (refreshTokenHelper.get(mWebView.getContext(), username) != null) {
            api.setRefreshToken(refreshTokenHelper.get(mWebView.getContext(), username));
            api.authorizationCodeRefresh().build().executeAsync().whenComplete(((credentials, throwable) -> {
                if (credentials != null && credentials.getAccessToken() != null) {
                    onCredentialsReceived(credentials);
                } else {
                    authenticateCode();
                    loginCallback.onHasToEnterCredentials();
                }
            }));
            return;
        }
        authenticateCode();
        loginCallback.onHasToEnterCredentials();
    }

    private void authenticateCode() {
        api.authorizationCodeUri().scope(String.join(",", SCOPES)).build().executeAsync().whenComplete(((uri, throwable) -> {
            Log.i(getTag(), "Auth url: " + uri.toString());
            handler.post(() -> mWebView.loadUrl(uri.toString()));
        }));
    }

    private void onCode(String code) {
        Log.i(getTag(), "Auth code is: " + code);
        api.authorizationCode(code).build().executeAsync().whenComplete(((credentials, throwable) -> onCredentialsReceived(credentials)));
        mWebView.setVisibility(View.GONE);
    }

    private void onCredentialsReceived(AuthorizationCodeCredentials credentials) {
        if (credentials == null) return;
        api.setAccessToken(credentials.getAccessToken());
        if (credentials.getRefreshToken() != null && !credentials.getRefreshToken().isEmpty())
            api.setRefreshToken(credentials.getRefreshToken());
        refreshTokenHelper.set(mWebView.getContext(), username, getRefreshToken());
        automaticTokenRefresh.startRefresh(api, credentials.getExpiresIn());
        Log.i(getTag(), "Client ID is: " + getClientID());
        Log.d(getTag(), "Client Secret is: " + getClientSecret());
        Log.i(getTag(), "Token is: " + api.getAccessToken());
        Log.i(getTag(), "Token expires in: " + credentials.getExpiresIn());
        connected();
    }

    private void connected() {
        if (callback != null)
            handler.post(() -> callback.onAuthentication(api));
    }

    private String getTag() {
        return getClass().getSimpleName();
    }

    public String getClientID() {
        return clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUri() {
        if (api != null && api.getRedirectURI() != null) return api.getRedirectURI().toString();
        return redirectUri;
    }

    public String getUsername() {
        return username;
    }

    public String getRefreshToken() {
        if (api != null) return api.getRefreshToken();
        return null;
    }

    public interface AuthenticationCallback {
        void onAuthentication(SpotifyApi api);
    }

    public interface LoginCallback {
        void onHasToEnterCredentials();
    }

    public static class AuthData{

        public AuthData(String clientID, String clientSecret, String redirectUri, String username) {
            this.clientID = clientID;
            this.clientSecret = clientSecret;
            this.redirectUri = redirectUri;
            this.username = username;
        }

        public String clientID;
        public String clientSecret;
        public String redirectUri;
        public String username;
    }
}
