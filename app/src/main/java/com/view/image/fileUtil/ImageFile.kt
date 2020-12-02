package com.view.image.fileUtil

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.view.image.R
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream


/**
 * 图片处理
 */
object ImageFile {


    fun saveImage(context: Context, bitmap: Bitmap) {
        MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "", "")
    }

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
        //其中imgUri为图片的标识符
//    shareIntent.setPackage("com.tencent.mm");
//    val cop = ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI")
//    shareIntent.setPackage("com.tencent.mobileqq")
//    shareIntent.component = cop
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
}