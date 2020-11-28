package com.view.image.analyzeRule

import android.util.Log
import com.view.image.analyzeRule.RuleType.*
import com.view.image.model.HomeData
import com.view.image.model.Rule
import org.jsoup.Jsoup
import java.util.*
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

enum class RuleType {
    DEFAULT,
    JS,
    RE,
    JSON,
    JSON_PATH,
    RULE_XPATH
}


class RuleUtil(private val rule: Rule, private val analyzeRuleDao: AnalyzeRuleDao) {
    private var engine: ScriptEngine = ScriptEngineManager().getEngineByName("javascript")

    /**
     * 规则是什么类型,
     */
    private val ruleJs = "@JS"
    private val ruleRe = "@RE"
    private val ruleJson = "@JSON"
    private val ruleXpath = "@XPATH"

    /**
     * 设置请求的地址，获取绝对地址时可能有用
     */
    fun setRequestUrl(url: String) {
        analyzeRuleDao.setRequestUrl(url)
    }

    // 获取请求方法
    fun getRuleReqMethod(): String {
        return rule.reqMethod
    }


    /**
     *  获取下一页请求的data
     * @param url 请求地址
     * @param page 请求页码
     */
    fun getNewData(url: String, page: Int): String {
        val data = getDataToUrl(url)
        val jsStr = rule.jsMethod
        if (rule.js.isNotEmpty()) {
            analyzeRuleDao.addJs(rule.js, engine)
        }
        engine.put("data", data)
        engine.put("page", page)
        engine.eval(jsStr)
        return engine.get("data").toString()
    }

    /**
     * 根据请求地址获取data
     * @param url 请求地址
     */
    private fun getDataToUrl(url: String): String {
        for (sort in rule.sortUrl.split("\n")) {
            sort.split("::", limit = 2).also { sortList ->
                if (sortList.size == 2)
                    sortList[1].split(",", limit = 2).also {
                        if (it.size == 2 && it[0] == url)
                            return it[1]
                    }
            }
        }
        return ""
    }

    /**
     * 获取字符串类型
     * @param methodString 字符串规则
     */
    private fun stringType(methodString: String): RuleType {
        if (methodString.substring(0, 2) == "//") return RULE_XPATH
        val strList = methodString.split(":", limit = 2)
        if (methodString.startsWith("$"))
            return JSON_PATH
        return if (strList.size < 2) DEFAULT
        else {
            when (strList[0].toUpperCase(Locale.ROOT)) {
                ruleJs -> JS
                ruleJson -> JSON
                ruleRe -> RE
                ruleXpath -> RULE_XPATH
                else -> DEFAULT
            }
        }

    }

    /**
     * 获取类型Map
     */
    fun getSortMap(): Map<String, String> {
        val sortMap = HashMap<String, String>()
        if (rule.reqMethod.toLowerCase(Locale.ROOT) == "get") {
            val sortList = rule.sortUrl.split("\n")
            for (sort in sortList) {
                val list = sort.split("::")
                sortMap[list[0]] = list[1]
            }
        } else {
            for (sort in rule.sortUrl.split("\n")) {
                sort.split("::", limit = 2).also { sortList ->
                    if (sortList.size == 2)
                        sortList[1].split(",", limit = 2).also {
                            Log.d("data", it.size.toString() + it.toString())
                            if (it.size == 2) sortMap[sortList[0]] = it[0]
                        }
                }
            }
        }
        return sortMap
    }

    /**
     * 分离规则
     */
    private fun getRuleString(ruleString: String): String {
        if (ruleString.substring(0, 2) == "//") return ruleString
        val strSplit = ruleString.split(":", limit = 2)
        return when (stringType(ruleString)) {
            RULE_XPATH -> strSplit[1]
            DEFAULT -> ruleString
            JSON_PATH -> ruleString
            else -> strSplit[1]
        }
    }

    /**
     * 根据给的规则获取数据
     * @param ruleString 字符串规则
     * @param result 网页源代码
     */
    private fun getDataList(ruleString: String, result: Any?): Any? {
        val ruleStr = getRuleString(ruleString)
        return when (stringType(ruleString)) {
            JS -> {
                if (rule.js.isNotEmpty()) {
                    engine.let { analyzeRuleDao.addJs(rule.js, it) }
                }
                engine.let { analyzeRuleDao.analyzeByJS(ruleStr, result, it) }
            }
            RE -> analyzeRuleDao.analyzeRuleByRe(ruleStr, result)
            JSON -> analyzeRuleDao.analyzeRuleJson(ruleStr, result)
            DEFAULT -> analyzeRuleDao.analyzeRuleByJSoup(ruleStr, result)
            RULE_XPATH -> analyzeRuleDao.analyzeRuleByXpath(ruleStr, result)
            JSON_PATH -> analyzeRuleDao.analyzeRuleJsonPath(ruleStr, result)
        }
    }

    private fun analyzeResult(result: Any?): List<*> {
        return when (result) {
            is String -> result.split(",")
            else -> result as List<*>
        }
    }

    /**
     * 获取首页数据
     * @param html 网页源代码
     */
    fun getHomeDataList(html: String): List<HomeData> {
        val homeList = when (val homeListRule = rule.homeList) {
            "" -> html
            else -> {
                getDataList(homeListRule, html)
            }
        }
        val homeHrefDataList = analyzeResult(getDataList(rule.homeHref, homeList))
        val homeSrcDataList = analyzeResult(getDataList(rule.homeSrc, homeList))
        val homeTitleDataList = analyzeResult(getDataList(rule.homeTitle, homeList))
        val homeDataList: ArrayList<HomeData> = ArrayList()
        if (homeHrefDataList.size == homeSrcDataList.size && homeSrcDataList.size == homeTitleDataList.size) {
            for (i in homeHrefDataList.indices) {
                homeSrcDataList[i].toString().let {
                    val imgSrc =
                        when {
                            rule.homeSrcReplaceByJS.isNotEmpty() -> imgSrcReplaceByJS(rule.imageUrlReplaceByJS,
                                it)
                            else -> it
                        }
                    val homeDAta =
                        HomeData(
                            homeHrefDataList[i].toString(),
                            imgSrc!!,
                            homeTitleDataList[i].toString()
                        )
                    homeDataList.add(homeDAta)
                }


            }
        }
        return homeDataList
    }

    /**
     * 获取图片数据
     * @param html 图片页源代码
     */
    fun getImgList(html: String): MutableList<Any?> {
        val imgPageListStr = when (val homeListRule = rule.imagePageList) {
            "" -> html
            else -> {
                getDataList(homeListRule, html)
            }
        }
        val imgSrcList = analyzeResult(
            getDataList(rule.imagePageSrc, imgPageListStr)
        ).toMutableList()
        if (rule.imageUrlReplaceByJS.isNotEmpty()) {
            for (i in imgSrcList.indices) {
                imgSrcList[i] =
                    imgSrcReplaceByJS(rule.imageUrlReplaceByJS, imgSrcList[i].toString())
            }
        }
        return imgSrcList
    }

    /**
     * 根据规则，图片地址替换
     * @param jsStr 替换规则js
     * @param imgSrc 图片地址
     */
    private fun imgSrcReplaceByJS(jsStr: String, imgSrc: String): String {
        return engine.let { analyzeRuleDao.analyzeByJsReplace(jsStr, imgSrc, it) }
    }

}

fun main() {
    val rule = Rule()
    rule.homeList = ".work-thumbnail"
    rule.homeHref = "a@href"
    rule.homeSrc = "img@abs:src"
    rule.homeTitle = "div.title@text"

    rule.imagePageList = "#imgs_json@text"
    rule.imagePageSrc = "@json:$.[*]..img"
    rule.imageUrlReplaceByJS = "imgSrc = 'http://imgoss.cnu.cc/' + imgSrc;"
    val ruleUtil = RuleUtil(rule, AnalyzeRule())

//    val document = Jsoup.connect("http://www.cnu.cc/inspirationPage/recent-0").get().html()
//    val dataList = ruleUtil.getHomeDataList(document)
//    for(data in dataList){
//        println(data.imgSrc + data.imgTitle + data.href )
//    }
    val imgHtml = Jsoup.connect("http://www.cnu.cc/works/430080").get().html()
    println(ruleUtil.getImgList(imgHtml))
//    val jxDocument = JXDocument.create(elements)
//    println(elements)
//    println(jxDocument.selN("//a/@href").toTypedArray())
//    println(str)
}