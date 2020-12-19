@file:Suppress("IMPLICIT_CAST_TO_ANY", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.view.image.fileUtil

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.view.image.R
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.*
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
        val path = "$absolutePath/${context.resources.getString(R.string.app_name)}/$name/"
        val fileName: String = File(url).name.replace(Regex("\\?.*"), "")
        try {
            if (!File(path).exists()) {
                File(path).mkdirs()
                Log.d("download_img", "创建文件夹$path")
            }
            val file = File(path, fileName)
            glideSaveImg(context, url, file)
            val uri: Uri = Uri.fromFile(file)
            broadcast(context, uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "$path$fileName"
    }

    /**
     * glide保存图片
     * @param context 上下文
     * @param url 图片地址
     * @param file 保存的文件
     */
    private fun glideSaveImg(context: Context, url: String, file: File) {
        Glide.with(context)
            .download(url).listener(object : RequestListener<File> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<File>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: File?,
                    model: Any?,
                    target: Target<File>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    copyFile(resource, file)
                    return false
                }
            }).submit()
    }

    //刷新相册
    private fun broadcast(context: Context, uri: Uri?) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri)
                context.sendBroadcast(mediaScanIntent)
            } else {
                context.sendBroadcast(Intent(Intent.ACTION_MEDIA_MOUNTED, uri))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    /**
     * 保存文件
     * @param oldFile 输入文件
     * @param newFile 输出文件
     */
    fun copyFile(oldFile: File?, newFile: File?) {
        var fileInputStream: FileInputStream? = null
        var fileOutputStream: FileOutputStream? = null
        try {
            fileInputStream = FileInputStream(oldFile)
            fileOutputStream = FileOutputStream(newFile)
            val buffer = ByteArray(1024)
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            try {
                fileInputStream?.close()
                fileOutputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    /**
     * 把图片保存在当前应用的目录下
     * @param context 上下文
     * @param imgUrl 图片地址
     */
    @SuppressLint("CheckResult")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun saveImg(context: Context, imgUrl: String): String? {
        val picturesPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.path
        try {
            val name = File(imgUrl).name
            Log.d("name", name)
            val file = File(picturesPath, name)
            glideSaveImg(context, imgUrl, file)

            return file.path
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    /**
     * 分享图片
     * @param content 上下文
     * @param imgPath 图片地址
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun shareImg(context: Context, imgPath: String) {
        val saveImg = saveImg(context, imgPath)
        val uriForFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "com.view.image", File(saveImg))
        } else {
            Uri.parse(saveImg)
        }
        Intent().apply {
            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uriForFile)
            type = "image/*"
            //切记需要使用Intent.createChooser，否则会出现别样的应用选择框，您可以试试
            context.startActivity(Intent.createChooser(this, "分享图片"))
        }
    }

    // 设置壁纸
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun makeImg(context: Context, imgUrl: String) {

        val saveImg = saveImg(context, imgUrl)
        val uriForFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "com.view.image", File(saveImg))
        } else {
            Uri.parse(saveImg)
        }
        Intent().apply {
            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent
                    .FLAG_GRANT_WRITE_URI_PERMISSION
            )
            action = Intent.ACTION_ATTACH_DATA
            setDataAndType(uriForFile, "image/*")
            context.startActivity(this)
        }
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