package com.mos.lib.common.util

import java.io.PrintWriter
import java.io.StringWriter
import java.util.regex.Matcher
import java.util.regex.Pattern

object StringUtil {

    fun join(str: String, iterable: Iterable<*>): String {
        val stringBuilder = StringBuilder()

        val iterator = iterable.iterator()

        while (iterator.hasNext()) {

            val `object` = iterator.next()

            stringBuilder.append(`object`.toString())

            if (iterator.hasNext()) {
                stringBuilder.append(str)
            }
        }
        return stringBuilder.toString()
    }

    fun getStackTrace(throwable: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)

        try {
            throwable.printStackTrace(pw)
            return sw.toString()
        } finally {
            pw.close()
        }
    }

    fun isEmpty(string: String?): Boolean {
        return string == null || "" == string.trim { it <= ' ' }
    }

    fun findByReg(patternStr: String, content: String): String? {
        val pattern = Pattern.compile(patternStr)

        val matcher = pattern.matcher(content)

        return if (matcher.find()) {
            matcher.group(1)
        } else null
    }

}

