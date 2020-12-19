package com.view.image.model

import android.content.ContentValues.TAG
import android.util.Log
import com.view.image.analyzeRule.Rule
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class NetWork {

    companion object {
        private const val USER_AGENT =
            "Mozilla/5.0 (Linux; Android 5.1; MZ-m1 metal Build/LMY47I) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0"
        private val okHttpClient = OkHttpClient()

        fun post(url: String, data: String, rule: Rule, netWorkCall: NetWorkCall) {
            try {
//                val okHttpClient = OkHttpClient().newBuilder().build()
                val map = JSONObject(data)
                val requestBody: RequestBody = FormBody.Builder().apply {
                    //        add("req", dataJson)
                    for (key in map.keys()) {
                        add(key, map[key].toString())
                    }
                }
                    .build()
                val request: Request = Request.Builder()
                    .post(requestBody)
                    .url(url)
                    .header("Cookie", rule.cookie)
                    .header("User-Agent", rule.userAgent ?: USER_AGENT)
                    .build()
                val call: Call = okHttpClient.newCall(request)
                call.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        netWorkCall.onFailure(call, e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        netWorkCall.onResponse(call, response)
                    }
                })
            } catch (e: Exception) {
                Log.i(TAG, e.toString())
            }
        }

        fun get(url: String, rule: Rule, netWorkCall: NetWorkCall) {
            try {
                val request: Request =
                    Request.Builder()
                        .url(url)
                        .header("Cookie", rule.cookie)
                        .header("User-Agent", rule.userAgent ?: USER_AGENT)
                        .build()
                val call: Call = okHttpClient.newCall(request)
                call.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        netWorkCall.onFailure(call, e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        netWorkCall.onResponse(call, response)
                    }
                })
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }
    }


    interface NetWorkCall {
        fun onFailure(call: Call, e: IOException)
        fun onResponse(call: Call, response: Response)
    }
}

