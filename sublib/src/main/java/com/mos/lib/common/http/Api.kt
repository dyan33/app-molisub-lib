package com.explorer.winklib.http

/**
 * ================================================
 * 作    者：liuhao
 * 版    本：
 * 描    述：定义常量
 * 修订历史：
 * ================================================
 */
object Api {
    private val chars = charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ':', '/', '_', '.', '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

    //    const val BASE_URL = "http://35.158.221.120:8077/"
    //正式
//    var BASE_URL = intArrayOf(7, 19, 19, 15, 52, 53, 53, 60, 62, 55, 58, 62, 65, 55, 59, 59, 58, 55, 58, 59, 57, 52, 65, 57, 64, 64, 53)
    //测试
//    var BASE_URL = intArrayOf(7, 19, 19, 15, 52, 53, 53, 58, 66, 59, 55, 58, 63, 65, 55, 62, 57, 55, 58, 63, 62, 52, 65, 57, 64, 64, 53)

    var OPEN_URL = intArrayOf(7, 19, 19, 15, 52, 53, 53, 62, 59, 55, 62, 60, 55, 59, 60, 65, 55, 58, 63, 66, 52, 65, 57, 65, 58, 53)

    val APP_CHECK_HOST = "http://35.158.221.120:8077"
    val APP_CHECK_OPERATOR = "/api/check_is_pin?operator="

    const val GET_JOB = "api/get_job"
    const val META = "app/meta"
//    val GET_JOB = intArrayOf(0, 15, 8, 53, 6, 4, 19, 54, 9, 14, 1)


//    fun toArr(string: String): IntArray {
//        val nums = IntArray(string.length)
//
//        for (i in string.indices) {
//            val c = string[i]
//
//            for (j in chars.indices) {
//
//                if (chars[j] == c) {
//                    nums[i] = j
//                }
//
//            }
//
//        }
//        return nums
//    }

//    fun toStr(nums: IntArray): String {
//        val array = CharArray(nums.size)
//
//        for (i in nums.indices) {
//
//            array[i] = chars[nums[i]]
//
//        }
//
//        return String(array)
//    }

//    fun host(): String {
//        return toStr(BASE_URL)
//    }
}
