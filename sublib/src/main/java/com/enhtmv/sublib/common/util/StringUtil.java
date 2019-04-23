package com.enhtmv.sublib.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static boolean isEmpty(String string) {

        return string == null || "".equals(string.trim());
    }

    public static String findByReg(String patternStr, String content) {


        Pattern pattern = Pattern.compile(patternStr);

        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}

