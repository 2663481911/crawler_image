package com.view.image.fileUtil

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity

/**
 * 剪贴板
 */
object ClipBoar {

    /**
     * 复制到剪贴板
     * @param context
     * @param text
     */
    fun putTextIntoClip(context: Context, text: String?) {
        val clipboardManager =
            context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
        //创建ClipData对象
        val clipData = ClipData.newPlainText("HSFAppDemoClip", text)
        //添加ClipData对象到剪切板中
        clipboardManager.setPrimaryClip(clipData)
    }

    /**
     * 获取剪切板内容
     */
    fun getTextFromClip(context: Context): String {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

//    if (!clipboardManager.hasPrimaryClip()) return
        val clipData = clipboardManager.primaryClip
        //获取 ClipDescription
//    val clipDescription = clipboardManager.primaryClipDescription
//    //获取 lable
//    val lable = clipDescription!!.label.toString()
        //获取 text
        return clipData!!.getItemAt(0).text.toString()
    }
}