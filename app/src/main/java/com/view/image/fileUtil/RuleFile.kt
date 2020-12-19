package com.view.image.fileUtil

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.view.image.analyzeRule.Rule
import com.view.image.setting.Setting
import org.json.JSONArray
import java.io.*

object RuleFile {


    fun readRule(context: Context): String {
        val string = StringBuffer()
        try {
            val path = context.getExternalFilesDir(null)?.path
            FileInputStream(File(path, Setting.RULE_FILE_NAME)).run {
                BufferedReader(InputStreamReader(this, "utf-8")).run {
                    var str: String?
                    while (this.readLine().also { str = it } != null) {
                        string.append(str)
                    }
                    this.close()
                }
                this.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return string.toString()
    }

    fun shareRule(context: Context, name: String? = null) {
        val path = context.getExternalFilesDir(null)?.path
        val ruleFile = File(path, name ?: Setting.RULE_FILE_NAME)
        val uriForFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "com.view.image", ruleFile)
        } else {
            Uri.fromFile(ruleFile)
        }

        Intent().apply {
            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent
                    .FLAG_GRANT_WRITE_URI_PERMISSION
            )
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uriForFile)
            type = "text/*"
            //切记需要使用Intent.createChooser，否则会出现别样的应用选择框，您可以试试
            context.startActivity(Intent.createChooser(this, "分享规则"))
        }
    }

    private fun saveRule(context: Context, str: String) {
        saveFile(context, str, Setting.RULE_FILE_NAME)
    }

    fun saveRule(context: Context, ruleList: List<Rule>) {
        saveRule(context, Gson().toJson(ruleList).toString())
    }

    fun saveFile(context: Context, str: String, name: String) {
        try {
            val path = context.getExternalFilesDir(null)?.path
            FileOutputStream(File(path, name)).run {
                BufferedWriter(OutputStreamWriter(this, "UTF-8")).apply {
                    write(str)
                    flush()
                    close()
                }
                this.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun addRule(context: Context, rule: Rule) {
        val ruleList = ruleStrToArrayRule(readRule(context))
        ruleList.add(0, rule)
        saveRule(context, ruleList)
    }

    fun editRule(context: Context, newRule: Rule, oldRulePosition: Int = 0) {
        val ruleList = ruleStrToArrayRule(readRule(context))
        ruleList.removeAt(oldRulePosition)
        ruleList.add(oldRulePosition, newRule)
        saveRule(context, ruleList)
    }

    /**
     * 把rule字符串转为rule列表
     */
    fun ruleStrToArrayRule(str: String): ArrayList<Rule> {
        val ruleStr = when {
            str.startsWith("{") -> "[$str]"
            else -> str
        }
        val typeOf = object : TypeToken<List<Rule>>() {}.type
        return Gson().fromJson(JSONArray(ruleStr).toString(), typeOf)

    }

    /**
     * 获取全部的rule的名字列表
     * @param ruleList rule列表
     */
    fun getRuleNameList(ruleList: List<Rule>): ArrayList<String> {
        val ruleNameList = ArrayList<String>()
        for (rule in ruleList) {
            ruleNameList.add(rule.sourceName)
        }
        return ruleNameList
    }

    /**
     * 根据名字获取rule
     */
    fun getRule(ruleList: List<Rule>, curRulePosition: Int): Rule {
        return ruleList[curRulePosition]
    }

}