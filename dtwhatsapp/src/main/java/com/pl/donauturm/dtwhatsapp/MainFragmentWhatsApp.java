package com.pl.donauturm.dtwhatsapp;

import static android.app.Activity.RESULT_CANCELED;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Objects;

@SuppressWarnings("unused")
public class MainFragmentWhatsApp extends Fragment {

    private static final String CHROME_FULL = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36";
    private static final String USER_AGENT = CHROME_FULL;

    private static final String STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE; // "android.permission.WRITE_EXTERNAL_STORAGE"
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA; // "android.permission.CAMERA"
    private static final String AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO; // "android.permission.RECORD_AUDIO"
    private static final String[] VIDEO_PERMISSION = {CAMERA_PERMISSION, AUDIO_PERMISSION};

    private static final String WHATSAPP_HOMEPAGE_URL = "https://www.whatsapp.com/";

    private static final String WHATSAPP_WEB_BASE_URL = "web.whatsapp.com";
    private static final String WORLD_ICON = "\uD83C\uDF10";
    @SuppressLint("ConstantLocale")
    private static final String WHATSAPP_WEB_URL = "https://" + WHATSAPP_WEB_BASE_URL + "/" + WORLD_ICON + "/" + Locale.getDefault().getLanguage();

    private static final int FILECHOOSER_RESULTCODE = 200;
    private static final int CAMERA_PERMISSION_RESULTCODE = 201;
    private static final int AUDIO_PERMISSION_RESULTCODE = 202;
    private static final int VIDEO_PERMISSION_RESULTCODE = 203;
    private static final int STORAGE_PERMISSION_RESULTCODE = 204;

    public static final String DEBUG_TAG = "WAWEBTOGO";

    private Activity activity;

    private SharedPreferences mSharedPrefs;

    private WebView mWhatsAppView;
    private ViewGroup mMainView;

    boolean mKeyboardEnabled = false;
    boolean mDarkMode;

    private ValueCallback<Uri[]> mUploadMessage;
    private PermissionRequest mCurrentPermissionRequest;
    private String mCurrentDownloadRequest = null;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        activity = requireActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_whatsapp, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mWhatsAppView = view.findViewById(R.id.whatsapp_view);
        mWhatsAppView.restoreState(savedInstanceState);
        mSharedPrefs = activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE);
        mDarkMode = mSharedPrefs.getBoolean("darkMode", false);

        mMainView = view.findViewById(R.id.layout);

        // webview stuff
        mWhatsAppView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            mCurrentDownloadRequest = url;
            if (checkPermission(STORAGE_PERMISSION)) {
                mWhatsAppView.loadUrl(BlobDownloader.getBase64StringFromBlobUrl(url));
                triggerDownload();
            } else {
                requestPermission(STORAGE_PERMISSION);
            }
        });
        mWhatsAppView.addJavascriptInterface(new BlobDownloader(activity.getApplicationContext()), BlobDownloader.JsInstance);

        mWhatsAppView.getSettings().setJavaScriptEnabled(true); //for wa web
        mWhatsAppView.getSettings().setAllowContentAccess(true); // for camera
        mWhatsAppView.getSettings().setAllowFileAccess(true);
        mWhatsAppView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWhatsAppView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWhatsAppView.getSettings().setMediaPlaybackRequiresUserGesture(false); //for audio messages

        mWhatsAppView.getSettings().setDomStorageEnabled(true); //for html5 app
        mWhatsAppView.getSettings().setDatabaseEnabled(true);
        mWhatsAppView.getSettings().setAppCacheEnabled(false); // deprecated
        mWhatsAppView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        mWhatsAppView.getSettings().setLoadWithOverviewMode(true);
        mWhatsAppView.getSettings().setUseWideViewPort(true);

        mWhatsAppView.getSettings().setSupportZoom(true);
        mWhatsAppView.getSettings().setBuiltInZoomControls(true);
        mWhatsAppView.getSettings().setDisplayZoomControls(false);

        mWhatsAppView.getSettings().setSaveFormData(true);
        mWhatsAppView.getSettings().setLoadsImagesAutomatically(true);
        mWhatsAppView.getSettings().setBlockNetworkImage(false);
        mWhatsAppView.getSettings().setBlockNetworkLoads(false);
        mWhatsAppView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWhatsAppView.getSettings().setNeedInitialFocus(false);
        mWhatsAppView.getSettings().setGeolocationEnabled(true);
        mWhatsAppView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        mWhatsAppView.setVerticalScrollBarEnabled(false);
        mWhatsAppView.setHorizontalScrollBarEnabled(false);

        mWhatsAppView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
                Toast.makeText(activity.getApplicationContext(), "OnCreateWindow", Toast.LENGTH_LONG).show();
                return true;
            }

            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                if (request.getResources()[0].equals(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                    if (ContextCompat.checkSelfPermission(activity, CAMERA_PERMISSION) == PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(activity, AUDIO_PERMISSION) == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(activity, VIDEO_PERMISSION, VIDEO_PERMISSION_RESULTCODE);
                        mCurrentPermissionRequest = request;
                    } else if (ContextCompat.checkSelfPermission(activity, CAMERA_PERMISSION) == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(activity, new String[]{CAMERA_PERMISSION}, CAMERA_PERMISSION_RESULTCODE);
                        mCurrentPermissionRequest = request;
                    } else if (ContextCompat.checkSelfPermission(activity, AUDIO_PERMISSION) == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(activity, new String[]{AUDIO_PERMISSION}, AUDIO_PERMISSION_RESULTCODE);
                        mCurrentPermissionRequest = request;
                    } else {
                        request.grant(request.getResources());
                    }
                } else if (request.getResources()[0].equals(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
                    if (ContextCompat.checkSelfPermission(activity, AUDIO_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                        request.grant(request.getResources());
                    } else {
                        ActivityCompat.requestPermissions(activity, new String[]{AUDIO_PERMISSION}, AUDIO_PERMISSION_RESULTCODE);
                        mCurrentPermissionRequest = request;
                    }
                } else {
                    try {
                        request.grant(request.getResources());
                    } catch (RuntimeException e) {
                        Log.d(DEBUG_TAG, "Granting permissions failed", e);
                    }
                }
            }

            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d(DEBUG_TAG, "WebView console message: " + cm.message());
                return super.onConsoleMessage(cm);
            }

            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                mUploadMessage = filePathCallback;
                Intent chooserIntent = fileChooserParams.createIntent();
                activity.startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
                //TODO: startActivityForResult is deprecated use registerForActivityResult
                return true;
            }
        });

        mWhatsAppView.setWebViewClient(new WebViewClient() {

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                setContentSize(view);
                if (mDarkMode) {
                    addDarkMode(view);
                }
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
                setContentSize(view);
                if (mDarkMode) {
                    addDarkMode(view);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.scrollTo(0, 0);

                setContentSize(view);
                if (mDarkMode) {
                    addDarkMode(view);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri url = request.getUrl();
                Log.d(DEBUG_TAG, url.toString());

                if (url.toString().equals(WHATSAPP_HOMEPAGE_URL)) {
                    // when whatsapp somehow detects that waweb is running on a phone (e.g. trough
                    // the user agent, but apparently somehow else), it automatically redicts to the
                    // WHATSAPP_HOMEPAGE_URL. It's higly unlikely that a user wants to visit the
                    // WHATSAPP_HOMEPAGE_URL from within waweb.
                    // -> block the request and reload waweb
                    showToast("WA Web has to be reloaded to keep the app running");
                    loadWhatsapp();
                    return true;
                } else if (url.getHost().equals(WHATSAPP_WEB_BASE_URL)) {
                    // whatsapp web request -> fine
                    return super.shouldOverrideUrlLoading(view, request);
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, url);
                    startActivity(intent);
                    return true;
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                String msg = String.format("Error: %s - %s", error.getErrorCode(), error.getDescription());
                Log.d(DEBUG_TAG, msg);
            }

            public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
                Log.d(DEBUG_TAG, "Unhandled key event: " + event.toString());
            }
        });

        if (savedInstanceState == null) {
            loadWhatsapp();
        } else {
            Log.d(DEBUG_TAG, "savedInstanceState is present");
        }

        mWhatsAppView.getSettings().setUserAgentString(USER_AGENT);
    }

    @Override
    public void onResume() {
        super.onResume();
        mWhatsAppView.onResume();

        mKeyboardEnabled = mSharedPrefs.getBoolean("keyboardEnabled", true);
        setAppbarEnabled(false);
        setKeyboardEnabled(mKeyboardEnabled);

    }

    @Override
    public void onPause() {
        super.onPause();
        mWhatsAppView.onPause();
    }

    private boolean checkPermission(String permission) {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(activity, permission);
    }

    private void requestPermission(String permission) {
        permissionRequester.launch(permission);
    }

    ActivityResultLauncher<String> permissionRequester = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            if (mCurrentPermissionRequest != null) {
                mCurrentPermissionRequest.grant(mCurrentPermissionRequest.getResources());
            }
        } else {
            if (mCurrentPermissionRequest != null) {
                mCurrentPermissionRequest.deny();
            }
        }
    });


    private void triggerDownload() {
        if (mCurrentDownloadRequest != null) {
            mWhatsAppView.loadUrl(BlobDownloader.getBase64StringFromBlobUrl(mCurrentDownloadRequest));
        }
        mCurrentDownloadRequest = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWhatsAppView.saveState(outState);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILECHOOSER_RESULTCODE:
                if (resultCode == RESULT_CANCELED || data.getData() == null) {
                    mUploadMessage.onReceiveValue(null);
                } else {
                    Uri result = data.getData();
                    Uri[] results = new Uri[1];
                    results[0] = result;
                    mUploadMessage.onReceiveValue(results);
                }
                break;
            default:
                Log.d(DEBUG_TAG, "Got activity result with unknown request code " + requestCode + " - " + data.toString());
        }
    }

    private void showToast(String msg) {
        activity.runOnUiThread(() -> Toast.makeText(activity, msg, Toast.LENGTH_LONG).show());
    }

    private void showSnackbar(String msg) {
        activity.runOnUiThread(() -> {
            final Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), msg, 900);
            snackbar.setAction("dismiss", (View view) -> snackbar.dismiss());
            snackbar.setActionTextColor(Color.parseColor("#075E54"));
            snackbar.show();
        });
    }

    private void toggleKeyboard() {
        setKeyboardEnabled(!mKeyboardEnabled);
    }

    private void setKeyboardEnabled(final boolean enable) {
        mKeyboardEnabled = enable;
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (enable && mMainView.getDescendantFocusability() == ViewGroup.FOCUS_BLOCK_DESCENDANTS) {
            mMainView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            showSnackbar("Unblocking keyboard...");
            //inputMethodManager.showSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 0);
        } else if (!enable) {
            mMainView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            mWhatsAppView.getRootView().requestFocus();
            showSnackbar("Blocking keyboard...");
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
        mSharedPrefs.edit().putBoolean("keyboardEnabled", enable).apply();
    }

    private void setAppbarEnabled(boolean enable) {
        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        if (actionBar != null) {
            if (enable) {
                actionBar.show();
            } else {
                actionBar.hide();
            }
        }
    }

    private void loadWhatsapp() {
        mWhatsAppView.getSettings().setUserAgentString(USER_AGENT);
        mWhatsAppView.loadUrl(WHATSAPP_WEB_URL);
    }

    public void addDarkMode(final WebView mWebView) {
        activity.getWindow().setNavigationBarColor(Color.BLACK);
        activity.getWindow().setStatusBarColor(Color.BLACK);

        Objects.requireNonNull(((AppCompatActivity) activity).getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            WebSettingsCompat.setForceDark(mWebView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK_STRATEGY)) {
                WebSettingsCompat.setForceDarkStrategy(mWebView.getSettings(), WebSettingsCompat.DARK_STRATEGY_PREFER_WEB_THEME_OVER_USER_AGENT_DARKENING);
            }
        } else {
            mWebView.loadUrl("javascript:(" + "function(){ " + "try {  document.body.classList.add('dark') } catch(err) { }" + "})()");
        }
    }

    public void setContentSize(final WebView mWebView){
        if (getContext() == null) return;
        if (getResources().getBoolean(R.bool.isTablet)) {
            // only change content sizes if device has a smaller screen than normally used for
            // whatsapp web
            // see https://stackoverflow.com/questions/9279111/determine-if-the-device-is-a-smartphone-or-tablet

        }
    }

    private String readFromInputStream(InputStream inputStream) {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultStringBuilder.toString();
    }

    public void logout() {
        new AlertDialog.Builder(activity).setTitle("Do you want to log out?").setMessage("When logging out, you will need to scan the QR code again with your phone to connect Whatsapp Web.").setPositiveButton("Yes", (dialog, which) -> {
            showSnackbar("logging out...");
            mWhatsAppView.loadUrl("javascript:localStorage.clear()");
            WebStorage.getInstance().deleteAllData();
            loadWhatsapp();
            dialog.dismiss();
        }).setNegativeButton("No", (dialog, which) -> dialog.dismiss()).show();
    }
}
