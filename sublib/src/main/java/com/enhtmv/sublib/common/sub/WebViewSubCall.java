package com.enhtmv.sublib.common.sub;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.blankj.utilcode.util.LogUtils;
import com.cp.plugin.event.SubEvent;

import okhttp3.Cookie;

public abstract class WebViewSubCall extends SubCall {

    protected static final Handler handler = new Handler(Looper.getMainLooper());

    protected WebView webView;


    public WebViewSubCall(WebView webView) {

        this.webView = webView;

        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }

        CookieManager.getInstance().removeAllCookie();

        this.webView.clearCache(true);

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
//        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        settings.setDomStorageEnabled(true);
//        settings.setDatabaseEnabled(false);
//        settings.setAllowFileAccess(false);

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

        webView.addJavascriptInterface(new JavascriptLog(), "LOG");

    }


    @Override
    public void onSub(String message) {

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
                        path.endsWith(".jepg") ||
                        path.endsWith(".woff"))) {

            LogUtils.i("ignore", url);
            return true;
        }

        return false;
    }

    public class JavascriptLog {

        @JavascriptInterface
        public void log(String tag, String info) {
            report(tag, info);
            LogUtils.i(tag, info);
        }

    }

}
