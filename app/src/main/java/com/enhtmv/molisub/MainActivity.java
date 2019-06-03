package com.enhtmv.molisub;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.cp.plugin.Plugin;
import com.cp.plugin.event.SubEvent;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubProxy;
import com.enhtmv.sublib.common.sub.Sub;
import com.enhtmv.sublib.common.sub.SubContext;
import com.enhtmv.sublib.common.util.HostUtil;
import com.enhtmv.sublib.work.WebSocketWorker;

import java.io.InputStream;

import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        WebView webView = findViewById(R.id.webview);

//        webView.getSettings().setJavaScriptEnabled(true);
//
//        webView.clearHistory();
//
//
//        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
//
//                LogUtils.i("console", consoleMessage.message());
//
//                return super.onConsoleMessage(consoleMessage);
//            }
//
//            @Override
//            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//
//                LogUtils.i("alert", url, message);
//
//                return super.onJsAlert(view, url, message, result);
//            }
//
//            @Override
//            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
//                return super.onJsConfirm(view, url, message, result);
//            }
//
//        });
//
//        webView.setWebViewClient(new WebViewClient() {
//
//            @Nullable
//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//                WebResourceResponse response = super.shouldInterceptRequest(view, request);
//
//
//                String url = request.getUrl().toString();
//
//                LogUtils.i(url);
//
//
//                if (url.endsWith("jquery.min.js")) {
//
//
//                    String orangeJs = ResourceUtils.readAssets2String("orange.js");
//
//                    InputStream inputStream = ConvertUtils.string2InputStream(orangeJs, "utf-8");
//
//
//                    return new WebResourceResponse("application/javascript; charset=utf-8", "utf-8", inputStream);
//
//
//                }
//
//                return response;
//            }
//        });
//
//        webView.loadUrl("http://go-es.allcpx.com/start/sub");

        SubEvent event = new TestSubEvent();

        if (BuildConfig.DEBUG) {


            SubProxy proxy = HostUtil.proxy();
            //        this.setProxy(new SubProxy("37.48.98.160", "engineer@foxseek.com", "0c4263", 11285));


            Plugin.proxy("37.48.98.160", "engineer@foxseek.com", "0c4263", 10223);

//            Plugin.proxy(proxy.getHost(), proxy.getUsername(), proxy.getPassword(), proxy.getPort());
            Plugin.log(true);
            Plugin.closeWifi(false);
            Plugin.setHiden(false);

            Plugin.operator(Sub.UK_VODAFONE);

        }

        Plugin.init(this, event, (RelativeLayout) findViewById(R.id.relative_layout));

//
//        buildNotificationAlert("通知设置", "a", "yes", "no").show();
//
        Plugin.call();


    }

    @Override
    protected void onResume() {
        super.onResume();


//        Plugin.call();

    }

    public AlertDialog buildNotificationAlert(String title, String content, String yes, String no) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(content);
        alertDialogBuilder.setPositiveButton(yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                        //go to set permision
                    }
                });
        alertDialogBuilder.setNegativeButton(no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //cancel
                    }
                });
        return (alertDialogBuilder.create());
    }


    private class TestSubEvent implements SubEvent {
        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onMessage(String tag, String content) {

        }
    }
}

//adb shell settings put global http_proxy 192.168.31.112:8090
