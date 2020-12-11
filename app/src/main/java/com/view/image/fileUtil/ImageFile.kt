@file:Suppress("IMPLICIT_CAST_TO_ANY")

package com.view.image.fileUtil

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.view.image.R
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


/**
 * 图片处理
 */
object ImageFile {


    /**
     * @param
     * @param
     * @param
     */
    fun saveImg(context: Context, url: String, name: String = ""): String {
        //获取内部存储状态
        val state = Environment.getExternalStorageState()
        //如果状态不是mounted，无法读写
        if (state != Environment.MEDIA_MOUNTED) return ""
        val absolutePath = Environment.getExternalStorageDirectory().absolutePath
        Log.d("imgUrl", url)
        val path = "$absolutePath/${context.resources.getString(R.string.app_name)}/$name/"
        val fileName: String = File(url).name.replace(Regex("\\?.*"), "")
        try {
            if (!File(path).exists()) {
                File(path).mkdirs()
                Log.d("download_img", "创建文件夹$path")
            }
            val file = File("$path$fileName")
            val bitmap = Glide.with(context)
                .asBitmap()
                .load(url)
                .submit().get()
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()

            val uri: Uri = Uri.fromFile(file)
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "$path$fileName"
    }


    /**
     * 处理微信分享资源获取失败问题
     */
    private fun insertImageToSystem(context: Context, imagePath: String, name: String): String? {
        var url: String? = ""
        try {
            url = MediaStore.Images.Media.insertImage(
                context.contentResolver,
                imagePath,
                name,
                "有图了"
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return url
    }

    /**
     * 分享图片
     */
    fun shareImg(content: Context, imgPath: String, name: String) {
        val url = insertImageToSystem(content, imgPath, name)
        val imgUri = Uri.parse(url)
        var shareIntent = Intent()
        shareIntent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent
                .FLAG_GRANT_WRITE_URI_PERMISSION
        )
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, imgUri)

        shareIntent.type = "image/*"
        //切记需要使用Intent.createChooser，否则会出现别样的应用选择框，您可以试试
        shareIntent = Intent.createChooser(shareIntent, "分享图片")
        content.startActivity(shareIntent)
    }

    // 设置壁纸
    fun makeImg(context: Context, imgUrl: String) {
        Glide.with(context).asBitmap().load(imgUrl)
            .into(object : SimpleTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?,
                ) {
//                        WallpaperManager.getInstance(baseContext).setBitmap(resource)
//                        runOnUiThread {
//                            Toast.makeText(baseContext, "设置成功", Toast.LENGTH_SHORT).show()
//                        }
                    val intent = Intent(Intent.ACTION_ATTACH_DATA)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.putExtra("mimeType", "image/*")
                    val uri: Uri =
                        Uri.parse(MediaStore.Images.Media.insertImage(context.contentResolver,
                            resource,
                            null,
                            null
                        )
                        )
                    intent.data = uri
                    context.startActivity(intent)
                }
            })


    }

    fun showImg(
        view: View,
        imageView: ImageView,
        imgUrl: String,
        referer: String = "",
        textView: TextView? = null,
    ) {
        val glideUrl = when (referer) {
            "" -> GlideUrl(imgUrl) {
                val header: MutableMap<String, String> = HashMap()
                //不一定都要添加，具体看原站的请求信息
                header["Referer"] = referer
                header
            }
            else -> imgUrl
        }

        Glide
            .with(view)
            .load(glideUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(imageView)

//        val request: Request = Request.Builder()
//            .url(imgUrl)
//            .header("Referer", referer)
//            .build()
//
//        val progressListener: Progress.ProgressListener = object : Progress.ProgressListener {
//            var firstUpdate = true
//
//            // 更新进度条
//            @SuppressLint("SetTextI18n")
//            override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
//                if (done) {
//                    println("completed")
//                } else {
//                    if (firstUpdate) {
//                        firstUpdate = false
//                        if (contentLength == -1L) {
//                            println("content-length: unknown")
//                        } else {
//                            // 获取长度
////                            System.out.format("content-length: %d\n", contentLength)
//
//                        }
//                    }
//                    // 更新进度条
//                    if (contentLength != -1L) {
////                        System.out.format("%d%% done\n", 100 * bytesRead / contentLength)
//                        textView?.text = "${100 * bytesRead / contentLength}%"
//                    }
//                }
//            }
//        }
//
//        val client: OkHttpClient = OkHttpClient.Builder()
//            // 添加网络拦截器
//            .addNetworkInterceptor(Interceptor { chain: Interceptor.Chain ->
//                val originalResponse = chain.proceed(chain.request())
//                // 原始回应
//                originalResponse.newBuilder()
//                    .body(Progress.ProgressResponseBody(originalResponse.body, progressListener))
//                    .build()
//            })
//            .build()
//
//        client.newCall(request).enqueue(object  :Callback{
//            override fun onFailure(call: Call, e: IOException) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                val body = response.body
//                val bytes = body!!.bytes()
//
//                view.post {
//                    textView?.visibility = View.GONE
//                    Glide
//                        .with(view)
//                        .load(bytes)
//                        .placeholder(R.drawable.ic_launcher_foreground)
//                        .into(imageView)
//                }
//            }
//
//        })


    }
}

class Progress {
    // 进度响应
    class ProgressResponseBody(
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
                    progressListener.update(totalBytesRead, responseBody!!.contentLength(),
                        bytesRead == -1L)
                    return bytesRead
                }
            }
        }
    }

    interface ProgressListener {
        fun update(bytesRead: Long, contentLength: Long, done: Boolean)
    }
}