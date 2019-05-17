package com.enhtmv.sublib.work.austria;

import com.blankj.utilcode.util.LogUtils;
import com.enhtmv.sublib.common.http.SubResponse;
import com.enhtmv.sublib.common.sub.SubCall;

import java.util.HashMap;
import java.util.Map;


public class AustriaA1 extends SubCall {

    private Map<String, String> header = new HashMap<>();


    public AustriaA1() {

        header.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3329.0 Mobile Safari/537.36");

    }

    @Override
    public void sub(String text) {

        final Info info = parseInfo(text);

        if (info != null) {

            report(SUB_REQEUST);

            try {
                SubResponse s = http().get(info.getSubUrl() + androidId, header);

                r.i("sub_reqeust", s);

            } catch (Exception e) {
                LogUtils.e(e);

                r.w("sub_request_error", e);
            }

        }
    }
}
