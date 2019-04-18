package com.enhtmv.sublib.common.util;

import java.io.InputStream;

public class IOUtil {

    public static String read(InputStream in) {

        StringBuffer out = new StringBuffer();

        byte[] b = new byte[4096];
        int n;

        try {
            while ((n = in.read(b)) != -1) {
                out.append(new String(b, 0, n));
            }
        } catch (Exception e) {
            SubLog.e(e);
        }


        return out.toString();

    }

}
