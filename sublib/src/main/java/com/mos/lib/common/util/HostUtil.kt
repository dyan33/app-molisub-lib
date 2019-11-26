package com.mos.lib.common.util

import com.mos.lib.common.http.SubProxy

object HostUtil {

    private val chars = charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ':', '/', '_', '.', '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9')


    //http://ec2-54-153-76-222.us-west-1.compute.amazonaws.com:8081
    private val HOST = intArrayOf(7, 19, 19, 15, 52, 53, 53, 4, 2, 59, 56, 62, 61, 56, 58, 62, 60, 56, 64, 63, 56, 59, 59, 59, 55, 20, 18, 56, 22, 4, 18, 19, 56, 58, 55, 2, 14, 12, 15, 20, 19, 4, 55, 0, 12, 0, 25, 14, 13, 0, 22, 18, 55, 2, 14, 12, 52, 65, 57, 65, 58)


    private val PROXY_HOST = intArrayOf(66, 58, 55, 59, 59, 57, 55, 64, 64, 55, 58, 62, 61)
    private val PROXY_USER = intArrayOf(12, 0, 20, 17, 8, 19, 8, 20, 18)
    private val PROXY_PWD = intArrayOf(46, 23, 62, 21, 48, 62, 16, 22)


    private fun toArr(string: String): IntArray {
        val nums = IntArray(string.length)

        for (i in 0 until string.length) {
            val c = string[i]

            for (j in chars.indices) {

                if (chars[j] == c) {
                    nums[i] = j
                }

            }

        }
        return nums
    }

    private fun toStr(nums: IntArray): String {
        val array = CharArray(nums.size)

        for (i in nums.indices) {

            array[i] = chars[nums[i]]

        }

        return String(array)
    }

    fun host(): String {
        return toStr(HOST)
    }

    fun proxy(): SubProxy {

        return SubProxy(toStr(PROXY_HOST), toStr(PROXY_USER), toStr(PROXY_PWD), 8090)


    }

    @JvmStatic
    fun main(args: Array<String>) {

        println(proxy())


    }


}
