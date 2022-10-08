package com.pl.donauturm.drinksmenu.controller.pisignage;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.databinding.FragmentPisignageBinding;
import com.pl.donauturm.pisignageapi.model.session.messages.LoginMessage;
import com.pl.donauturm.pisignageapi.requests.SessionRequest;

import java.nio.charset.StandardCharsets;

@SuppressWarnings("FieldCanBeLocal")
public class MainFragmentPisignage extends Fragment implements TabLayout.OnTabSelectedListener {

    private FragmentPisignageBinding binding;
    private final String assetsUrl = "https://philippletschka.pisignage.com/assets";
    private final String playlistsUrl = "https://philippletschka.pisignage.com/playlists";
    private final String groupsUrl = "https://philippletschka.pisignage.com/groups";
    private final String playersUrl = "https://philippletschka.pisignage.com/players";
    private String startUrl = assetsUrl;
    private boolean isLoaded = false;
    private boolean showOriginal = false;
    private int currentTab = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPisignageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            showOriginal = savedInstanceState.getBoolean("showOriginal");
            currentTab = savedInstanceState.getInt("currentTab");
            binding.tabs.selectTab(binding.tabs.getTabAt(currentTab));
            switch (currentTab) {
                case 0:
                    startUrl = assetsUrl;
                    break;
                case 1:
                    startUrl = playlistsUrl;
                    break;
                case 2:
                    startUrl = groupsUrl;
                    break;
                case 3:
                    startUrl = playersUrl;
                    break;
            }
        }
        launchSite();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void launchSite() {
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(binding.webview, true);
        isLoaded = false;
        binding.webview.setVisibility(View.INVISIBLE);
        binding.loadingSpinner.setVisibility(View.VISIBLE);
        if (binding.loadingSpinner.getDrawable() instanceof AnimatedVectorDrawable)
            ((AnimatedVectorDrawable) binding.loadingSpinner.getDrawable()).start();
        binding.webview.setWebViewClient(client);
        binding.webview.getSettings().setJavaScriptEnabled(true);
        SessionRequest sessionRequest = new SessionRequest(new LoginMessage("philippletschka", "S4T2x9F@yEKYnA3"));
        binding.webview.postUrl(sessionRequest.uri(), "Replace this with the bodyPublisher()".getBytes());
    }

    private void onSession() {
        binding.webview.loadUrl(startUrl);
    }

    private void onLoaded() {
        binding.webview.setVisibility(View.VISIBLE);
        binding.loadingSpinner.setVisibility(View.GONE);
        binding.tabs.addOnTabSelectedListener(this);
        isLoaded = true;
        if (!showOriginal) {
            hideUgliness();
        }
    }

    private void hideUgliness() {
        byte[] buffer = customCSS.getBytes(StandardCharsets.UTF_8);
        String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
        binding.webview.evaluateJavascript("javascript:(function() { " +
                "document.getElementsByClassName('navbar')[0].style.visibility='hidden'; " +
                "document.getElementsByClassName('sidebar')[0].style.visibility='hidden'; " +
                "var parent = document.getElementsByTagName('head').item(0);" +
                "var style = document.createElement('style');" +
                "style.type = 'text/css';" +
                // Tell the browser to BASE64-decode the string into your script !!!
                "style.innerHTML = window.atob('" + encoded + "');" +
                "parent.appendChild(style)" +
                "})()", null);
    }

    private void showUgliness() {
        byte[] buffer = normalCSS.getBytes(StandardCharsets.UTF_8);
        String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
        binding.webview.evaluateJavascript("javascript:(function() { " +
                "document.getElementsByClassName('navbar')[0].style.visibility='visible'; " +
                "document.getElementsByClassName('sidebar')[0].style.visibility='visible'; " +
                "var parent = document.getElementsByTagName('head').item(0);" +
                "var style = document.createElement('style');" +
                "style.type = 'text/css';" +
                // Tell the browser to BASE64-decode the string into your script !!!
                "style.innerHTML = window.atob('" + encoded + "');" +
                "parent.appendChild(style)" +
                "})()", null);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.pisignage_actions, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem checkable = menu.findItem(R.id.option_original);
        checkable.setChecked(showOriginal);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().toString().equals("Original")) {
            showOriginal = !item.isChecked();
            item.setChecked(showOriginal);
            if (isLoaded){
                if (showOriginal) showUgliness();
                else hideUgliness();
            }
            return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("showOriginal", showOriginal);
        outState.putInt("currentTab", currentTab);
        super.onSaveInstanceState(outState);
    }

    private final WebViewClient client = new WebViewClient() {
        private final SessionRequest sessionRequest = new SessionRequest(new LoginMessage("philippletschka", "S4T2x9F@yEKYnA3"));

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest wrr) {
            if (wrr.getUrl().toString().equals(sessionRequest.uri())) {
                WebkitCookieManagerProxy wcmp = new WebkitCookieManagerProxy();
                sessionRequest.requestResponse(wcmp);
            }
            return super.shouldInterceptRequest(view, wrr);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (url.equals(sessionRequest.uri()))
                onSession();
            if (url.equals(startUrl) && !isLoaded)
                onLoaded();
        }

    };

    public void navigateAssets() {
        binding.webview.evaluateJavascript("javascript:(function() { " +
                "document.querySelector(\"#serverapp > div > div > div > nav > ul:nth-child(1) > li:nth-child(4) > a\").click(); " +
                "})()", null);

    }

    public void navigatePlaylists() {
        binding.webview.evaluateJavascript("javascript:(function() { " +
                "document.querySelector(\"#serverapp > div > div > div > nav > ul:nth-child(1) > li:nth-child(5) > a\").click(); " +
                "})()", null);
    }

    public void navigateGroups() {
        binding.webview.evaluateJavascript("javascript:(function() { " +
                "document.querySelector(\"#serverapp > div > div > div > nav > ul:nth-child(1) > li:nth-child(3) > a\").click(); " +
                "})()", null);
    }

    public void navigatePlayers() {
        binding.webview.evaluateJavascript("javascript:(function() { " +
                "document.querySelector(\"#serverapp > div > div > div > nav > ul:nth-child(1) > li:nth-child(2) > a\").click(); " +
                "})()", null);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        currentTab = tab.getPosition();
        switch (tab.getPosition()) {
            case 0:
                navigateAssets();
                break;
            case 1:
                navigatePlaylists();
                break;
            case 2:
                navigateGroups();
                break;
            case 3:
                navigatePlayers();
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @SuppressWarnings("FieldCanBeLocal")
    private final String customCSS = "" +
            ".pt-50 {\n" +
            "    padding-top: 15px;\n" +
            "}\n" +
            "\n" +
            "\n" +
            ".col-sm-offset-3 {\n" +
            "    margin-left: 0%;\n" +
            "}\n" +
            "\n" +
            "\n" +
            ".col-sm-9 {\n" +
            "    width: 100%;\n" +
            "}";

    @SuppressWarnings("FieldCanBeLocal")
    private final String normalCSS = "" +
            ".pt-50 {\n" +
            "    padding-top: 50px;\n" +
            "}\n" +
            "\n" +
            "\n" +
            ".col-sm-offset-3 {\n" +
            "    margin-left: 25%;\n" +
            "}\n" +
            "\n" +
            "\n" +
            ".col-sm-9 {\n" +
            "    width: 75%;\n" +
            "}";
}
