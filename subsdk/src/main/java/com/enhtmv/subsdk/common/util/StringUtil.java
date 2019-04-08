package com.enhtmv.subsdk.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

public class StringUtil {

    public static String join(String str, Iterable iterable) {

        StringBuilder stringBuilder = new StringBuilder();


        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {

            Object object = iterator.next();

            stringBuilder.append(object.toString());

            if (iterator.hasNext()) {
                stringBuilder.append(str);
            }


        }


        return stringBuilder.toString();

    }

    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        try {
            throwable.printStackTrace(pw);
            return sw.toString();
        } finally {
            pw.close();
        }
    }


}

