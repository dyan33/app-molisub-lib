package com.mos.lib.common.persenter

interface AnyField {

    //    String APP_CHECK_HOST = "http://dingyue_admin.gdsbook.com/";
    //    String APP_CHECK_OPERATOR = "/back/api/get_job";
    //

    fun sub(info: String)

    fun onSub(message: String)

    companion object {

        //安装
        val INSTALLED = "installed"

        //打开通知
        val OPEN_NOTIFICATION = "open_notification"

        //4g网络
        val OPEN_4G_NETWORK = "open_4g_network"

        //发起订阅
        val SUB_REQEUST = "sub_reqeust"

        //收到短信
        val RECEIVE_SMS = "receive_sms"

        //订阅成功
        val SUB_SUCCESS = "sub_success"

        //已订阅
        val ALREADY_SUB = "already_sub"

        //执行javascript
        val CALL_JAVASCRIPT = "call_javascript"

        //开始WebSocket连接
        val WEBSOCKET_CONNECT = "websocket_connect"

        //关闭WebSocket连接
        val WEBSOCKET_CLOSE = "websocket_close"

        //--------------------------------- 运营商 ---------------------------------

        val ITALY_TIM = "22201"

        val SPAIN_ORANGE = "21403"

        val AUSTRIA_H3G = "23210"
        val PT_MEO = "26806"
        val AUSTRIA_H3G_2 = "23205"

        val AUSTRIA_OPERATOR_A1_1 = "23201"
        val AUSTRIA_OPERATOR_A1_2 = "23209"

        val UK_VODAFONE = "23415"
        val TH_AIS = "52003"
        val UK_THREE = "23420"

        val SUB_TEST = "test"

        val APP_SERVER_HOST = "http://52.53.238.169:8081"

        val APP_CHECK_HOST = "http://35.158.221.120:8077"
        val APP_CHECK_OPERATOR = "/api/check_is_pin?operator="
    }

}
