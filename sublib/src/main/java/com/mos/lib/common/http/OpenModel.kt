package com.explorer.winklib.model

import com.explorer.winklib.http.AppConstants
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class OpenModel {

    var state = ""
    var client = OkHttpClient()

    fun getState(code: String): Response? {
        val request = Request.Builder().url(AppConstants.APP_CHECK_HOST + AppConstants.APP_CHECK_OPERATOR + code).build()
        val response: Response
        try {
            response = client.newCall(request).execute()
            if (response.isSuccessful) {
                return response
            } else {
                throw Exception("Unexpected code $response")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}