package com.enhtmv.molisub;

import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.enhtmv.sublib.common.SubCall;
import com.enhtmv.sublib.common.SubCallBack;
import com.enhtmv.sublib.common.SubContext;
import com.enhtmv.sublib.common.SubEvent;
import com.enhtmv.sublib.common.http.SubProxy;
import com.enhtmv.sublib.common.util.HostUtil;
import com.enhtmv.sublib.common.util.SubLog;
import com.enhtmv.sublib.work.aodili.AodiliH3g;
import com.enhtmv.sublib.work.putaoya.MeoSub;
import com.enhtmv.sublib.work.putaoya.NosSub;
import com.enhtmv.sublib.work.yidali.TIMSub;

public class MainActivity extends AppCompatActivity {

    private SubContext subContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        WebView webView = findViewById(R.id.webview);


        webView.setWebChromeClient(new WebChromeClient());

        SubEvent event = new TestSubEvent();

        final SubCall subCall;


//        subCall = new MeoSub(webView, event);

        subCall = new TIMSub(event);

//        subCall = new AodiliH3g(event);

//        subCall = new TIMSubW(webView, event);


        subContext = new SubContext(this, subCall);


//        subContext.state(new SubCallBack<String>() {
//            @Override
//            public void callback(String s) {
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        subContext.buildNotificationAlert("打开通知", "获取通知权限", "yes", "no").show();
//
//
//                    }
//                });
//
//            }
//        });


        if (BuildConfig.DEBUG) {


//            subCall.setProxy(new SubProxy("37.48.98.160","engineer@foxseek.com","0c4263",11310));
            subCall.setProxy(HostUtil.proxy());
            subCall.setLog(true);
            subContext.setCloseWifi(false);
        }

        subContext.call();


    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (subContext.isNotificationServiceEnabled()) {
//            subContext.call();
//        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        subContext.destroy();
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
