package com.enhtmv.sublib.work.putaoya;

import com.alibaba.fastjson.JSON;
import com.enhtmv.sublib.common.SubCall;
import com.cp.plugin.event.SubEvent;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubResponse;
import com.enhtmv.sublib.common.util.StringUtil;
import com.enhtmv.sublib.common.util.SubLog;

import java.util.HashMap;
import java.util.Map;



public class NosSub extends SubCall {


    public NosSub(SubEvent subEvent) {
        super("http://54.153.76.222:8081", "nos", subEvent);
    }

    @Override
    public void sub(String message) {

        try {

            MetaInfo info = JSON.parseObject(message, MetaInfo.class);


            SubHttp subHttp = http();

            Map<String, String> header = new HashMap<>();

            header.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 8.1.0; zh-cn; BLA-AL00 Build/HUAWEIBLA-AL00) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/8.9 Mobile Safari/537.36");


            SubResponse response = subHttp.get(info.nosLoadUrl + "&clickId=" + androidId, header);

            String url = response.response().request().url().toString();
            header.put("Referer", url);


            String requestId = response.doc().select("#not-request-id").first().text();
            String ctkn = response.doc().select("#not-ctkn").first().text();
            String xctkns = response.doc().select("#not-xctkns").first().text();


            String params = StringUtil.findByReg("F\\.set\\('params', \\$F\\.util\\.urlToObj\\('(.*)'\\)\\)", response.body());


            StringBuilder query = new StringBuilder(params.replace("external=false", "external=true"));

//            query.append("&commandname=confirm");
//            query.append("&external=true");
            query.append("&request_id=").append(requestId);
            query.append("&xctkn=").append(ctkn);
            query.append("&xctkns=").append(xctkns);
            query.append("&w=1080");
            query.append("&h=1920");

            String baseurl = "https://m.pagamentos.nos.pt/payment/widget/command?";


            report(SUB_REQEUST);


            for (int i = 0; i < 2; i++) {
                SubLog.i("times", i);
                try {
                    SubResponse response1 = subHttp.get(info.nosBaseUrl + query + "&commandname=check-status", header);
                    Thread.sleep(3000);
                } catch (Exception e) {
                    SubLog.e(e);
                }
            }


            SubResponse response2 = subHttp.get(info.nosBaseUrl + query + "&commandname=confirm", header);

            report.i("nos_reponse", response2);

            //重试

            String contentType = response2.response().header("Content-Type");

            if (contentType != null && contentType.contains("application/json")) {

                String content = response2.body();


            }


        } catch (Exception e) {
            SubLog.e(e);
            event.onError(e);

            report.e("nos_error", e);
        }
    }

    @Override
    public void onSub(String meta) {


    }


}
