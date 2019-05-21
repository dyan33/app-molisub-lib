package com.enhtmv.sublib.work.uk;

import android.util.ArrayMap;

import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubResponse;
import com.enhtmv.sublib.common.sub.SubCall;

import java.util.Map;

public class UKThree extends SubCall {


    private static final String baseUrl = "http://mguk.foxseek.com/to/aoc?sid=889&f=305&c=";


    @Override
    public void sub(String info) {

        Map<String, String> header = new ArrayMap<>();

        header.put("User-Agent", userAgent);

        try {

            SubHttp subHttp = http();

            SubResponse r = subHttp.get(baseUrl + androidId, header);

            report("three", r.toString());

        } catch (Exception e) {
            report("three_error", e);
        }

    }
}
