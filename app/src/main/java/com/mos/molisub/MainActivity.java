package com.mos.molisub;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

//import com.cp.plugin.Log4js;
//import com.cp.plugin.event.LogEvent;
//import com.enhtmv.sublib.common.http.SubProxy;
//import com.enhtmv.sublib.common.sub.AnyField;
//import com.enhtmv.sublib.common.util.HostUtil;

import com.cp.log.Log4js;
import com.cp.log.event.LogEvent;

public class MainActivity extends AppCompatActivity implements LogEvent {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (BuildConfig.DEBUG) {
//            SubProxy proxy = HostUtil.proxy();
//            this.setProxy(new SubProxy("37.48.98.160", "engineer@foxseek.com", "0c4263", 11246));
//            Log4js.INSTANCE.proxy("91.220.77.154", "mauritius", "precpx123", 8090);
//            Log4js.proxy(proxy.getHost(), proxy.getUsername(), proxy.getPassword(), proxy.getPort());
//            Log4js.proxy("37.48.98.160", "engineer@foxseek.com", "0c4263", 10031);
            Log4js.INSTANCE.log(true);
            Log4js.INSTANCE.closeWifi(false);
//            Log4js.setHiden(false);
            Log4js.INSTANCE.operator("23210");
//            Log4js.operator("26806");
//            Log4js.operator("23201");
            //H3G
//            Log4js.operator("23205");
//            Log4js.operator("26206");
            Log4js.INSTANCE.init(this);
        }


//        Log4js.buildNotificationAlert("标题", "该应用需要授权读取通知权限", "确定", "取消");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log4js.INSTANCE.call();
    }


    @Override
    public void onMessage(String tag, String content) {
        Log.i("tag", content);
    }

    @Override
    public void onError(Throwable throwable) {

    }
}

//adb shell settings put global http_proxy 192.168.31.112:8090
