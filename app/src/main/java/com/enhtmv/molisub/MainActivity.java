package com.enhtmv.molisub;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cp.plugin.Plugin;
import com.cp.plugin.event.SubEvent;
import com.enhtmv.sublib.common.http.SubProxy;
import com.enhtmv.sublib.common.sub.Sub;
import com.enhtmv.sublib.common.sub.SubContext;
import com.enhtmv.sublib.common.util.HostUtil;

import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SubEvent event = new TestSubEvent();

        if (BuildConfig.DEBUG) {

            SubProxy proxy = HostUtil.proxy();

            Plugin.proxy(proxy.getHost(), proxy.getUsername(), proxy.getPassword(), proxy.getPort());
            Plugin.log(true);
            Plugin.closeWifi(false);
            Plugin.operator(Sub.OPERATOR_H3G);
        }


        buildNotificationAlert("消息", "打开通知权限", "设置", "取消").show();

        Plugin.init(this, event);


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


    private class TestSubEvent implements SubEvent {
        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onMessage(String tag, String content) {

        }
    }
}
