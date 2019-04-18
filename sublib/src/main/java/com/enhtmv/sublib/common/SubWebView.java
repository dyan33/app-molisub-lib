package com.enhtmv.sublib.common;

import android.webkit.WebSettings;
import android.webkit.WebView;

public abstract class SubWebView extends SubCall {


    protected WebView webView;


    public SubWebView(WebView webView, SubEvent subEvent, String host) {

        super(host);
        this.webView = webView;
        this.event = subEvent;

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);


    }
}
