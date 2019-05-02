package com.enhtmv.molisub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.cp.plugin.Plugin;
import com.cp.plugin.event.SubEvent;
import com.enhtmv.sublib.common.http.SubProxy;
import com.enhtmv.sublib.common.util.HostUtil;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        WebView webView = findViewById(R.id.webview);


        webView.setWebChromeClient(new WebChromeClient());

        SubEvent event = new TestSubEvent();

        Plugin.init(this, event);

        if (BuildConfig.DEBUG) {

            SubProxy proxy = HostUtil.proxy();

            Plugin.proxy(proxy.getHost(), proxy.getUsername(), proxy.getPassword(), proxy.getPort());
            Plugin.log(true);
//            Plugin.closeWifi(false);
        }

        Plugin.call();


    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
