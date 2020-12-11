package com.view.image.model

import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class NetWork {

    companion object {
        var user_agent = "Mozilla/5.0"
        fun get(url: String, cookie: String, netWorkCall: NetWorkCall) {
            try {
                val okHttpClient = OkHttpClient()
                val request: Request =
                    Request.Builder()
                        .url(url)
                        .header("Cookie", cookie)
                        .header("User-Agent", user_agent)
//                        .header("Referer", url)
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

        fun post(url: String, data: String, cookie: String, netWorkCall: NetWorkCall) {
            try {
                val okHttpClient = OkHttpClient().newBuilder().build()
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
                    .header("Cookie", cookie)
                    .header("User-Agent", user_agent)
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
                Log.i("getHtml", e.toString())
            }
        }
    }


    interface NetWorkCall {
        fun onFailure(call: Call, e: IOException)
        fun onResponse(call: Call, response: Response)
    }
}

