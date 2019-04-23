package com.enhtmv.sublib.common;

import android.os.Handler;
import android.os.Looper;
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

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);


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
