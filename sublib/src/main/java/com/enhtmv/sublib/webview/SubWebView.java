package com.enhtmv.sublib.webview;

import android.os.Handler;
import android.os.Looper;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.enhtmv.sublib.common.SubCall;
import com.enhtmv.sublib.common.SubEvent;

public abstract class SubWebView extends SubCall {

    protected static final Handler handler = new Handler(Looper.getMainLooper());

    protected WebView webView;


    public SubWebView(WebView webView, String host, String name, SubEvent subEvent) {

        super(host, name, subEvent);
        this.webView = webView;

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);


    }
}
