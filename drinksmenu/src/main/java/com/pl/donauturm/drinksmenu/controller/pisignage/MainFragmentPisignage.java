package com.pl.donauturm.drinksmenu.controller.pisignage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
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

import com.pl.donauturm.drinksmenu.databinding.FragmentPisignageBinding;
import com.pl.donauturm.pisignageapi.model.session.messages.LoginMessage;
import com.pl.donauturm.pisignageapi.requests.SessionRequest;

public class MainFragmentPisignage extends Fragment {

    private FragmentPisignageBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPisignageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(binding.webview, true);
        binding.webview.setWebViewClient(client);
        binding.webview.getSettings().setJavaScriptEnabled(true);
        SessionRequest sessionRequest = new SessionRequest(new LoginMessage("philippletschka", "S4T2x9F@yEKYnA3"));
        binding.webview.postUrl(sessionRequest.uri(), "Replace this with the bodyPublisher()".getBytes());
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            //TODO end of shift
            binding.webview.loadUrl("https://philippletschka.pisignage.com/assets");
        }, 1000 * 10);
    }



    private final WebViewClient client = new WebViewClient(){
        private final SessionRequest sessionRequest = new SessionRequest(new LoginMessage("philippletschka", "S4T2x9F@yEKYnA3"));

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest wrr) {
            if (wrr.getUrl().toString().equals(sessionRequest.uri())){
                WebkitCookieManagerProxy wcmp = new WebkitCookieManagerProxy();
                /*Response response = */sessionRequest.requestResponse(wcmp);
//                if (response == null) return null;
//                List<String> cookieList = response.headers().values("Set-Cookie");
//                ResponseBody body = response.body();
//                if (body == null) return null;
//                return new WebResourceResponse("","", body.byteStream());
            }
            return super.shouldInterceptRequest(view, wrr);
        }
    };

}
