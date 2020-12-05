package com.view.image.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.view.image.analyzeRule.AnalyzeRule
import com.view.image.analyzeRule.RuleUtil
import okhttp3.Call
import okhttp3.Response
import java.io.IOException
import java.util.*

class DebugReqViewModel : NetWork.NetWorkCall, ViewModel() {
    private val startPage = MutableLiveData(1)
    private lateinit var ruleUtil: RuleUtil
    private val homeStatusCode = MutableLiveData<Int>()
    val homeListData = MutableLiveData<String>()
    val homeHref = MutableLiveData<List<*>>()
    val homeTitle = MutableLiveData<List<*>>()
    val homeSrc = MutableLiveData<List<*>>()
    val imageListData = MutableLiveData<String>()
    var homeReqSrc = MutableLiveData<String>()
    var nextPage = MutableLiveData<String>()


    private fun getOneSort(): String {
        if (ruleUtil.getSortMap().isNotEmpty())
            return ruleUtil.getSortMap().values.random()
        return ""
    }

    fun setRuleUtil(rule: Rule) {
        ruleUtil = RuleUtil(rule, AnalyzeRule())
    }

    fun getHomeHtml() {
        val url = getOneSort()
        ruleUtil.setRequestUrl(url)
        if (ruleUtil.getRuleReqMethod().toLowerCase(Locale.ROOT) == "get") {
            homeStatusCode.postValue(1)
            homeReqSrc.postValue(url.replace("@page", startPage.value.toString()))
            NetWork.get(url.replace("@page", startPage.value.toString()), ruleUtil.getCooke(), this)
        } else {
            homeStatusCode.postValue(1)
            NetWork.post(url,
                ruleUtil.getNewData(url, startPage.value!!),
                ruleUtil.getCooke(),
                this)
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
            NetWork.get(href.toString(), ruleUtil.getCooke(), this)
        }
    }

    override fun onResponse(call: Call, response: Response) {
        when (homeStatusCode.value) {
            1 -> {
                val html = response.body!!.string()
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
                response.body!!.string().let {
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