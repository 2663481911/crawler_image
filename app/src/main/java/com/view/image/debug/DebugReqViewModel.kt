package com.view.image.debug

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.view.image.analyzeRule.AnalyzeRule
import com.view.image.analyzeRule.Rule
import com.view.image.analyzeRule.RuleUtil
import com.view.image.model.NetWork
import okhttp3.Call
import okhttp3.Response
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.HashMap

class DebugReqViewModel : NetWork.NetWorkCall, ViewModel() {
    companion object {
        const val TAG = "DebugReqViewModel"
        const val HOME_CODE = 1
        const val PAGE_CODE = 2
    }

    private val startPage = MutableLiveData(1)
    private lateinit var ruleUtil: RuleUtil
    private val homeStatusCode = MutableLiveData<Int>()
    val homeListData = MutableLiveData<String>()
    val homeHref = MutableLiveData<Any>()
    val homeTitle = MutableLiveData<Any>()
    val homeSrc = MutableLiveData<Any>()
    val imageListData = MutableLiveData<String>()
    var homeReqSrc = MutableLiveData<String>()
    var nextPage = MutableLiveData<String>()
    val tabMap = MutableLiveData<Map<String, String>>()

    fun setRuleUtil(rule: Rule) {
        ruleUtil = RuleUtil(rule, AnalyzeRule())
        getTabMap()
    }

    /**
     * 获取tab
     */
    private fun getTabMap() {
        if (ruleUtil.rule.tabHref != "") {

            NetWork.get(ruleUtil.getTabFrom(), ruleUtil.rule, object : NetWork.NetWorkCall {
                override fun onFailure(call: Call, e: IOException) {
                    HashMap<String, String>().run {
                        put("e", e.stackTraceToString())
                        tabMap.postValue(this)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    response.body?.let {
                        val tab =
                            ruleUtil.getTab(String(it.bytes(), charset(ruleUtil.getCharset())))
                        tabMap.postValue(tab)
                    }
                }
            })
        } else {
            tabMap.postValue(ruleUtil.sortMap)
        }
    }

    fun getUrl() {
        getHomeHtml(tabMap.value!!.values.random())
    }

    private fun getHomeHtml(url: String) {
        Log.d(TAG, url)
        ruleUtil.setRequestUrl(url)
        if (ruleUtil.getRuleReqMethod().toLowerCase(Locale.ROOT) == "get") {
            homeStatusCode.postValue(HOME_CODE)
            homeReqSrc.postValue(url.replace("@page", startPage.value.toString()))
            NetWork.get(
                url.replace("@page", startPage.value.toString()),
                ruleUtil.rule,
                this
            )
        } else {
            homeStatusCode.postValue(HOME_CODE)
            NetWork.post(
                url,
                ruleUtil.getNewData(url, startPage.value!!),
                ruleUtil.rule, this@DebugReqViewModel
            )

        }
    }

    fun getImagePage() {
        homeHref.value?.let {
            if (it is List<*> && it.size > 0) {
                val href = it[0]
                homeStatusCode.postValue(PAGE_CODE)
                NetWork.get(href.toString(), ruleUtil.rule, this)
            }
        }

    }

    override fun onFailure(call: Call, e: IOException) {
        when (homeStatusCode.value) {
            HOME_CODE -> homeListData.postValue(e.toString())
            PAGE_CODE -> imageListData.postValue(e.toString())
        }
    }

    override fun onResponse(call: Call, response: Response) {

        when (homeStatusCode.value) {
            HOME_CODE -> {
                val html = String(
                    response.body!!.bytes(),
                    Charset.forName(ruleUtil.getCharset())
                )
                val homeDataList = ruleUtil.getHomeList(html)

                if (homeDataList.toString().isNotEmpty()) {
                    homeListData.postValue(homeDataList.toString())
                    homeHref.postValue(ruleUtil.getHomeHref(homeDataList))
                    homeSrc.postValue(ruleUtil.getHomeSrc(homeDataList))
                    homeTitle.postValue(ruleUtil.getHomeTitle(homeDataList))
                } else {
                    homeListData.postValue("null, html:$html")
                }
            }

            PAGE_CODE -> {
                val html = String(response.body!!.bytes(), Charset.forName(ruleUtil.getCharset()))
                imageListData.postValue(ruleUtil.getImgList(html).toString())
                homeHref.value?.let {
                    if (it is List<*>) {
                        val imageNextPageHref =
                            ruleUtil.getImageNextPageHref(html, (it[0].toString()))
                        nextPage.postValue(imageNextPageHref)
                    }
                }

            }
        }

    }

}