package com.view.image.analyzeRule

import com.jayway.jsonpath.JsonPath
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.seimicrawler.xpath.JXDocument
import org.seimicrawler.xpath.JXNode
import javax.script.ScriptEngine


class AnalyzeRule : AnalyzeRuleDao {
    var url: String? = null

    /**
     * 设置请求地址
     */
    override fun setRequestUrl(url: String) {
        this.url = url
    }

    override fun analyzeRuleByXpath(xpathString: String, doc: Any?): MutableList<JXNode>? {
        val document = Jsoup.parse(doc.toString(), url)
        val jxDocument = JXDocument.create(document)
        return jxDocument.selN(xpathString)
    }

    override fun analyzeRuleByJSoup(jSoupStr: String, doc: Any?): Any? {
        if (jSoupStr == "") return doc

        jSoupStr.split("@", limit = 2).also {
            return when (it.size) {
                2 -> {
                    val arrayList = ArrayList<Any?>()
                    when {
                        it[0] != "" -> {
                            val document = Jsoup.parse(doc.toString(), url)
                            val elements = document.select(it[0])
                            for (element in elements) {
                                if (it[1] != "text") arrayList.add(element.attr(it[1]))
                                else arrayList.add(element.text())
                            }
                        }
                        else -> {
                            val element = when (doc) {
                                is Elements -> {
                                    for (element in doc) {
                                        when {
                                            it[1] != "text" -> arrayList.add(element.attr(it[1]))
                                            else -> arrayList.add(element.text())
                                        }
                                    }
                                    return arrayList
                                }

                                is MutableList<*> -> {
                                    for (node in doc) {
                                        val element = (node as JXNode).asElement()
                                        when {
                                            it[1] != "text" -> arrayList.add(element.attr(it[1]))
                                            else -> arrayList.add(element.text())
                                        }
                                    }
                                    return arrayList
                                }
                                is Element -> doc
                                is Document -> doc
                                is JXNode -> doc.asElement()
                                else -> Jsoup.parse(doc.toString(), url)
                            }
                            if (it[1] != "text") arrayList.add(element.attr(it[1]))
                            else arrayList.add(element.text())
                        }
                    }
                    arrayList
                }
                else -> Jsoup.parse(doc.toString(), url).select(jSoupStr)
            }
        }
    }

    override fun analyzeRuleByRe(reStr: String, doc: Any?): Any? {
        return null
    }

    override fun analyzeRuleJson(jsonStr: String, doc: Any?): Any? {
        return JsonPath.read<String>(doc.toString(), jsonStr)
    }

    override fun analyzeRuleJsonPath(jsonStr: String, doc: Any?): Any? {
        return JsonPath.read<String>(doc.toString(), jsonStr)
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