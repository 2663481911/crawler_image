package com.view.image.setting

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.view.image.model.Rule
import org.json.JSONArray
import java.io.*

object RuleFile {

    fun readRule(context: Context): String {
        val str = StringBuffer()
        var inputStream: InputStream? = null
        try {
            val path = context.getExternalFilesDir(null)?.path
            inputStream = FileInputStream(File(path, Setting.RULE_FILE_NAME))
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } != -1) {
                str.append(String(buffer, 0, length))
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }
        return str.toString()
    }

    fun saveRule(context: Context, str: String) {
        saveFile(context, str, Setting.RULE_FILE_NAME)
    }

    fun saveRule(context: Context, ruleList: List<Rule>) {
        saveRule(context, Gson().toJson(ruleList).toString())
    }


    fun saveFile(context: Context, str: String, name: String) {
        var outputStream: OutputStream? = null
        try {
            val path = context.getExternalFilesDir(null)?.path
            outputStream = FileOutputStream(File(path, name))
            outputStream.write(str.toByteArray())
            path?.let { Log.d("file", it) }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            outputStream?.close()
        }
    }

    fun addRule(context: Context, rule: Rule) {
        val ruleList = ruleStrToArrayRule(readRule(context)).toMutableList()
        ruleList.add(0, rule)
        saveRule(context, ruleList)
    }

    fun editRule(context: Context, newRule: Rule, oldRulePosition: Int = 0) {
        val ruleList = ruleStrToArrayRule(readRule(context)).toMutableList()
        ruleList.removeAt(oldRulePosition)
        ruleList.add(oldRulePosition, newRule)
        saveRule(context, ruleList)
    }

    /**
     * 把rule字符串转为rule列表
     */
    fun ruleStrToArrayRule(str: String): List<Rule> {
        val ruleStr = when {
            str.startsWith("{") -> "[$str]"
            else -> str
        }
        val typeOf = object : TypeToken<List<Rule>>() {}.type
        return Gson().fromJson(JSONArray(ruleStr).toString(), typeOf)

    }

    /**
     * 当前位置的rule置顶
     * @param ruleList rule列表
     * @param curRulePosition 当前rule位置
     */
    fun moveCutRulePositionIn0(ruleList: List<Rule>, curRulePosition: Int): List<Rule> {
        ruleList.toMutableList().also {
            val curRule = it.removeAt(curRulePosition)
            it.add(0, curRule)
            return it
        }
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
    fun getRule(ruleList: List<Rule>, ruleName: String): Rule {
        for (rule in ruleList) {
            if (rule.sourceName == ruleName) {
                return rule
            }
        }
        return Rule()
    }

}

fun main() {

    val str = "{\n" +
            "    \"sourceUrl\": \"https://www.poco.cn/\",\n" +
            "    \"sourceName\": \"POCO\",\n" +
            "    \"sortUrl\": \"全::https://web-api.poco.cn/v1_1/rank/get_homepage_recommend_list,{\\\"req\\\":{\\\"version\\\":\\\"1.1.0\\\",\\\"app_name\\\":\\\"poco_photography_web\\\",\\\"os_type\\\":\\\"weixin\\\",\\\"is_enc\\\":0,\\\"env\\\":\\\"prod\\\",\\\"ctime\\\":1604831120420,\\\"param\\\":{\\\"start\\\":20,\\\"length\\\":20,\\\"works_category\\\":\\\"0\\\",\\\"time_point\\\":1604831121},\\\"sign_code\\\":\\\"2eff7b74b438fa8bd2c\\\"}}\\n人::https://web-api.poco.cn/v1_1/rank/get_homepage_recommend_list,{\\\"req\\\":{\\\"version\\\":\\\"1.1.0\\\",\\\"app_name\\\":\\\"poco_photography_web\\\",\\\"os_type\\\":\\\"weixin\\\",\\\"is_enc\\\":0,\\\"env\\\":\\\"prod\\\",\\\"ctime\\\":1604831120420,\\\"param\\\":{\\\"start\\\":20,\\\"length\\\":20,\\\"works_category\\\":\\\"1\\\",\\\"time_point\\\":1604831121},\\\"sign_code\\\":\\\"2eff7b74b438fa8bd2c\\\"}}\",\n" +
            "    \"homeList\": \"\",\n" +
            "    \"homeHref\": \"\$.data.list[*].url\",\n" +
            "    \"homeTitle\": \"\$.data.list[*].title\",\n" +
            "    \"homeSrc\": \"\$.data.list[*].img\",\n" +
            "    \"homeSrcReplaceByJS\": \"imgSrc = 'https:/' + imgSrc\",\n" +
            "    \"imagePageList\": \"img[data-src*=pic]\",\n" +
            "    \"imagePageSrc\": \"@data-src\",\n" +
            "    \"imageUrlReplaceByJS\": \"imgSrc = 'https:' + imgSrc\",\n" +
            "    \"reqMethod\": \"post\",\n" +
            "    \"cookie\": \"\",\n" +
            "    \"js\": \"https://cdn.bootcdn.net/ajax/libs/blueimp-md5/2.18.0/js/md5.js\",\n" +
            "    \"jsMethod\": \"var data_js = JSON.parse(data);\\nvar req = data_js['req']\\nreq['ctime'] = new Date().getTime();\\nreq['param']['time_point'] = Math.ceil(req['ctime']/1000);\\nreq['param']['start'] = page * 20;\\nreq['sign_code'] = md5('poco_'+ JSON.stringify(req['param']) + '_app').substring(5, 24);\\ndata_js['req'] = req;\\ndata = JSON.stringify(data_js);\\nJSON.stringify(data_js)\"\n" +
            "  }"
    val rule = RuleFile.ruleStrToArrayRule(str)
    println(rule)
}