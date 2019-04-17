package com.enhtmv.sublib.common;

public interface SubEvent {


    String PING = "ping";

    //开关状态
    String ON_OFF_STATE = "on_off_state";

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


    void onMessage(String tag, String content);

    void onError(Throwable throwable);


}
