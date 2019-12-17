package com.mos.lib.common.persenter

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView

import com.blankj.utilcode.util.LogUtils

abstract class WebViewAnyFieldCall(protected var webView: WebView) : AnyFieldCall() {


    init {

        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true)
        } else {
            CookieManager.getInstance().setAcceptCookie(true)
        }

        CookieManager.getInstance().removeAllCookies {
            LogUtils.i("删除cookie")
        }

        this.webView.clearCache(true)

        val settings = webView.settings

        settings.javaScriptEnabled = true
        settings.setGeolocationEnabled(true)
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        //        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //        settings.setDomStorageEnabled(true);
        //        settings.setDatabaseEnabled(false);
        //        settings.setAllowFileAccess(false);

        //        if (Build.VERSION.SDK_INT >= 16) {
        //            settings.setAllowFileAccessFromFileURLs(true);
        //        }
        if (Build.VERSION.SDK_INT >= 21) {
            settings.mixedContentMode = 2
        }
        //        if (Build.VERSION.SDK_INT >= 11) {
        //            settings.setAllowContentAccess(false);
        //        }
        //        if (Build.VERSION.SDK_INT >= 17) {
        //            settings.setUserAgentString(WebSettings.getDefaultUserAgent(this.f128a) + C0028c.m150c("SykKCAFMBh8AHFhKKicm"));
        //        }

        webView.addJavascriptInterface(JavascriptLog(), "LOG")

    }


    override fun onSub(message: String) {

    }

    protected fun ignore(request: WebResourceRequest): Boolean {

        val url = request.url.toString()

        val path = request.url.path
        if (path != null && (path.endsWith(".gif") ||
                        path.endsWith(".css") ||
                        path.endsWith(".png") ||
                        path.endsWith(".ico") ||
                        path.endsWith(".jpg") ||
                        path.endsWith(".jepg") ||
                        path.endsWith(".woff"))) {

            LogUtils.i("ignore", url)
            return true
        }

        return false
    }

    protected fun clearCookies() {
        CookieManager.getInstance().removeAllCookies { }

        webView.clearHistory()
        webView.clearCache(true)

    }


    inner class JavascriptLog {

        @JavascriptInterface
        fun log(tag: String, info: String) {
            report(tag, info)
            LogUtils.i(tag, info)
        }

    }

    companion object {

        protected val handler = Handler(Looper.getMainLooper())
    }

}
