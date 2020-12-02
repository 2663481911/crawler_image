package com.view.image.setting

import android.content.Context
import com.view.image.fileUtil.RuleFile.saveFile
import java.io.File
import java.io.IOException

object Setting {
    const val RULE_FILE_NAME = "rule.json"
    const val INIT_FILE_NAME = "init.json"

    fun moveSettingFile(context: Context, name: String = "rule.json") {
        val path = context.getExternalFilesDir(null)?.path
        if (!File(path, name).exists()) {
            val initString = readAssetsFile(context, name)
            saveFile(context, initString, name)
        }
    }

    private fun readAssetsFile(context: Context, name: String): String {
        val str = StringBuffer()
        try {
            with(context.assets.open(name)) {
                val buffer = ByteArray(1024)
                var length: Int
                while (this.read(buffer).also { length = it } != -1) {
                    str.append(String(buffer, 0, length))
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return str.toString()
    }


}

