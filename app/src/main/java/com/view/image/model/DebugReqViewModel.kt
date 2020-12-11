package com.view.image.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.view.image.analyzeRule.AnalyzeRule
import com.view.image.analyzeRule.Rule
import com.view.image.analyzeRule.RuleUtil
import okhttp3.Call
import okhttp3.Response
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread

class DebugReqViewModel : NetWork.NetWorkCall, ViewModel() {
    private val startPage = MutableLiveData(1)
    private lateinit var ruleUtil: RuleUtil
    private val homeStatusCode = MutableLiveData<Int>()
    val oneSortUrl = MutableLiveData<String>()
    val homeListData = MutableLiveData<String>()
    val homeHref = MutableLiveData<List<*>>()
    val homeTitle = MutableLiveData<List<*>>()
    val homeSrc = MutableLiveData<List<*>>()
    val imageListData = MutableLiveData<String>()
    var homeReqSrc = MutableLiveData<String>()
    var nextPage = MutableLiveData<String>()


    private fun getOneSort(): String {
        if (ruleUtil.sortHrefList.isNotEmpty())
            return ruleUtil.sortHrefList[0]
        return ""
    }

    fun setRuleUtil(rule: Rule) {
        ruleUtil = RuleUtil(rule, AnalyzeRule())
        oneSortUrl.value = getOneSort()
    }

    fun getHomeHtml() {
        val url = oneSortUrl.value!!
        ruleUtil.setRequestUrl(url)
        if (ruleUtil.getRuleReqMethod().toLowerCase(Locale.ROOT) == "get") {
            homeStatusCode.postValue(1)
            homeReqSrc.postValue(url.replace("@page", startPage.value.toString()))
            NetWork.get(url.replace("@page", startPage.value.toString()),
                ruleUtil.rule.cookie,
                this)
        } else {
            homeStatusCode.postValue(1)
            thread {
                NetWork.post(url,
                    ruleUtil.getNewData(url, startPage.value!!),
                    ruleUtil.rule.cookie,
                    this)
            }

        }
    }

    override fun onFailure(call: Call, e: IOException) {
        when (homeStatusCode.value) {
            1 -> homeListData.postValue(e.toString())

            2 -> {
                imageListData.postValue(e.toString())
            }
        }
    }

    fun getImagePage() {
        if (homeHref.value?.isNotEmpty() == true) {
            val href = homeHref.value?.get(0)
            Log.d("href", href.toString())
            homeStatusCode.postValue(2)
            NetWork.get(href.toString(), ruleUtil.rule.cookie, this)
        }
    }

    override fun onResponse(call: Call, response: Response) {
        when (homeStatusCode.value) {
            1 -> {
                val html = String(response.body!!.bytes(),
                    charset = Charset.forName(ruleUtil.getCharset()))
                val homeDataList = ruleUtil.getHomeList(html)

                if (homeDataList.toString().isNotEmpty()) {
                    homeListData.postValue(homeDataList.toString())
                    homeHref.postValue(ruleUtil.getHomeHref(homeDataList) as List<*>)
                    homeSrc.postValue(ruleUtil.getHomeSrc(homeDataList) as List<*>)
                    homeTitle.postValue(ruleUtil.getHomeTitle(homeDataList) as List<*>)
                } else {
                    homeListData.postValue("null, html:$html")
                }
            }

            2 -> {
                val header = response.headers
                header.let { Log.d("Content-Charset", it.toString()) }
                String(response.body!!.bytes(),
                    charset = Charset.forName(ruleUtil.getCharset())).let {
                    val imgList = ruleUtil.getImgList(it)
                    imageListData.postValue(imgList.toString())
                    val imageNextPageHref = ruleUtil.getImageNextPageHref(it,
                        homeHref.value?.get(0).toString())
                    nextPage.postValue(imageNextPageHref)
                }

            }
        }
    }


}