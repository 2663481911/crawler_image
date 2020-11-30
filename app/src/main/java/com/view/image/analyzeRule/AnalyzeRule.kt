package com.view.image.analyzeRule

import android.util.Log
import com.jayway.jsonpath.JsonPath
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.seimicrawler.xpath.JXDocument
import org.seimicrawler.xpath.JXNode
import java.io.IOException
import javax.script.ScriptEngine


class AnalyzeRule : AnalyzeRuleDao {
    lateinit var url: String

    /**
     * 获取js
     */
    private fun getHtml(url: String, userAgent: String = ""): String? {
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .header("user-agent", userAgent)
            .build()
        val call: Call = okHttpClient.newCall(request)

        try {
            val response: Response = call.execute()
            return response.body?.string()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun anyToJXDocument(doc: Any?): JXDocument {
        return when (doc) {
            is JXDocument -> doc
            is Document -> JXDocument.create(doc)
            is Elements -> JXDocument.create(doc)
            else -> JXDocument.create(doc.toString())
        }
    }

    /**
     * 设置请求地址
     */
    override fun setRequestUrl(url: String) {
        this.url = url
    }

    override fun analyzeRuleByXpath(xpathString: String, doc: Any?): Any? {
        return when (doc) {
            is MutableList<*> ->
                return when (doc[0]) {
                    is JXNode -> {
                        val jxNodeList: MutableList<JXNode> = ArrayList()
                        for (jxNode in doc) {
                            jxNodeList.add((jxNode as JXNode).selOne(xpathString))
                        }
                        jxNodeList
                    }
                    else -> anyToJXDocument(doc).selN(xpathString)
                }
            else -> anyToJXDocument(doc).selN(xpathString)
        }
    }

    private fun anyToElements(doc: Any?): Elements {

        return when (doc) {
            is Elements -> doc
            is Document -> Elements(doc)
            is Element -> Elements(doc)
            is List<*> -> {
                when (doc[0]) {
                    is JXNode -> {
                        val elements = Elements()
                        for (jxNode in doc) {
                            elements.add((jxNode as JXNode).asElement())
                        }
                        elements
                    }
                    else -> Elements(Jsoup.parse(doc.toString()))
                }
            }
            else -> Elements(Jsoup.parse(doc.toString()))
        }
    }

    override fun analyzeRuleByJSoup(jSoupStr: String, doc: Any?): Any {
        val elements = anyToElements(doc)
        val arrayList = ArrayList<Any?>()
        jSoupStr.split("@", limit = 2).also {
            return when (it.size) {
                2 -> {
                    for (element in elements) {
                        element.setBaseUri(url)
                        Log.d("element", "${element.baseUri()} url = $url")
                        when {
                            it[0] != "" -> {
                                for (element1 in element.select(it[0])) {

                                    if (it[1] == "text") {
                                        val text = element1.text()
                                        arrayList.add(text)
                                    } else {
//                                        element1.setBaseUri("http://www.zdqx.com/html/hotlist-2.html")
                                        val attr = element1.absUrl(it[1])
                                        arrayList.add(attr)
                                    }
                                }
                            }
                            else -> {
                                if (it[1] == "text") {
                                    val text = element.text()
                                    arrayList.add(text)
                                } else {
                                    val attr = element.attr(it[1])
                                    arrayList.add(attr)
                                }
                            }
                        }
                    }
                    arrayList
                }
                else -> {
                    val elements1 = Elements()
                    for (element in elements) {
                        for (element1 in element.select(jSoupStr)) {
                            elements1.add(element1)
                        }
                    }
                    elements1
                }
            }
        }
    }

    override fun analyzeRuleByRe(reStr: String, doc: Any?): Any {
        return ""
    }

    override fun analyzeRuleJson(jsonStr: String, doc: Any?): Any? {
        return JsonPath.read<String>(doc.toString(), jsonStr)
    }

    override fun analyzeRuleJsonPath(jsonStr: String, doc: Any?): Any? {
        return JsonPath.read<String>(doc.toString(), jsonStr)
    }

    override fun addJs(jsUrl: String, engine: ScriptEngine) {
        val html = getHtml(jsUrl)
        engine.eval(html)
    }

    override fun analyzeByJsReplace(jsStr: String, imgSrc: String, engine: ScriptEngine): String {
        engine.put("imgSrc", imgSrc)
        engine.eval(jsStr)
        return engine.get("imgSrc").toString()
    }

    override fun analyzeByJS(jsStr: String, doc: Any?, engine: ScriptEngine): String {
        val html = doc.toString()
        engine.put("result", html)
        engine.eval(jsStr)
        return engine.get("result").toString()
    }

}


fun main() {
    val document = Jsoup.connect("https://www.hexuexiao.cn/meinv/guzhuang/list-2.html").get()
    println(document)
    val elements =
        AnalyzeRule().analyzeRuleByJSoup("div[class=\"waterfall_warp\"] div[class=\"waterfall_1box\"] dd",
            document)
    println(elements)
//    println(AnalyzeRule().analyzeRuleByJSoup("img@abs:src", elements))

}