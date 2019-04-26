package com.enhtmv.sublib.common;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.enhtmv.sublib.common.SubCall;
import com.enhtmv.sublib.common.SubEvent;
import com.enhtmv.sublib.common.util.SubLog;

public abstract class WebViewSubCall extends SubCall {

    protected static final Handler handler = new Handler(Looper.getMainLooper());

    protected WebView webView;


    public WebViewSubCall(WebView webView, String host, String name, SubEvent subEvent) {

        super(host, name, subEvent);
        this.webView = webView;

        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }

        this.webView.clearCache(true);

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);

//        if (Build.VERSION.SDK_INT >= 16) {
//            settings.setAllowFileAccessFromFileURLs(true);
//        }
        if (Build.VERSION.SDK_INT >= 21) {
            settings.setMixedContentMode(2);
        }
//        if (Build.VERSION.SDK_INT >= 11) {
//            settings.setAllowContentAccess(false);
//        }
//        if (Build.VERSION.SDK_INT >= 17) {
//            settings.setUserAgentString(WebSettings.getDefaultUserAgent(this.f128a) + C0028c.m150c("SykKCAFMBh8AHFhKKicm"));
//        }


    }


    protected boolean ignore(WebResourceRequest request) {

        String url = request.getUrl().toString();

        String path = request.getUrl().getPath();
        if (path != null && (
                path.endsWith(".gif") ||
                        path.endsWith(".css") ||
                        path.endsWith(".png") ||
                        path.endsWith(".ico") ||
                        path.endsWith(".jpg") ||
                        path.endsWith(".woff"))) {

            SubLog.i("ignore", url);
            return true;
        }

        return false;
    }


}
