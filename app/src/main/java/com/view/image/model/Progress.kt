package com.view.image.model

import okhttp3.*
import okio.*
import java.io.IOException

class Progress {

    @Throws(java.lang.Exception::class)
    fun run(url: String = "https://www.fantasyfactory.xyz/2018.10/1810_50/FFA07399.jpg") {
        val request: Request = Request.Builder()
            .url(url)
            .build()

        val progressListener: ProgressListener = object : ProgressListener {
            var firstUpdate = true

            // 更新进度条
            override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
                if (done) {
                    println("completed")
                } else {
                    if (firstUpdate) {
                        firstUpdate = false
                        if (contentLength == -1L) {
                            println("content-length: unknown")
                        } else {
                            // 获取长度
                            System.out.format("content-length: %d\n", contentLength)
                        }
                    }
                    // 更新进度条
                    if (contentLength != -1L) {
                        System.out.format("%d%% done\n", 100 * bytesRead / contentLength)
                    }
                }
            }
        }

        val client: OkHttpClient = OkHttpClient.Builder()
            // 添加网络拦截器
            .addNetworkInterceptor(Interceptor { chain: Interceptor.Chain ->
                val originalResponse = chain.proceed(chain.request())
                // 原始回应
                originalResponse.newBuilder()
                    .body(ProgressResponseBody(originalResponse.body, progressListener))
                    .build()
            })
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val body = response.body
            body!!.bytes()

        }
    }

    // 进度响应
    private class ProgressResponseBody(
        private val responseBody: ResponseBody?,
        private val progressListener: ProgressListener,
    ) :
        ResponseBody() {
        private var bufferedSource: BufferedSource? = null
        override fun contentType(): MediaType? {
            return responseBody!!.contentType()
        }


        override fun contentLength(): Long {
            return responseBody!!.contentLength()
        }


        override fun source(): BufferedSource {
            if (bufferedSource == null) {
                bufferedSource = source(responseBody!!.source()).buffer()
            }
            return bufferedSource!!
        }

        private fun source(source: Source): Source {
            return object : ForwardingSource(source) {
                var totalBytesRead = 0L

                @Throws(IOException::class)
                override fun read(sink: Buffer, byteCount: Long): Long {
                    val bytesRead = super.read(sink, byteCount)
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                    progressListener.update(
                        totalBytesRead, responseBody!!.contentLength(),
                        bytesRead == -1L
                    )

                    return bytesRead
                }
            }
        }
    }

    internal interface ProgressListener {
        fun update(bytesRead: Long, contentLength: Long, done: Boolean)
    }

    companion object {
        @Throws(java.lang.Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            Progress().run()
        }
    }
}