package com.view.image.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.view.image.analyzeRule.AnalyzeRule
import com.view.image.analyzeRule.Rule
import com.view.image.analyzeRule.RuleUtil
import com.view.image.model.NetWork
import com.view.image.setting.Setting.TAG
import okhttp3.Call
import okhttp3.Response
import java.io.IOException
import java.nio.charset.Charset
import java.util.*

const val DATA_STATUS_NETWORK_ERROR = 404    // 网络错误
const val DATA_STATUS_LOAD_NORMAL = 200   // 正常加载
const val DATA_STATUS_NOR_MORE = 204   // 没有数据

class HomeDataViewModel(application: Application) : AndroidViewModel(application),
    NetWork.NetWorkCall {

    val sortMap = MutableLiveData<Map<String, String>>()
    var isRefresh: Boolean = false    // 是不是下拉刷新, 加载把位置调到0
    val curReqName = MutableLiveData<String>()
    var curReqUrl = MutableLiveData<String>()     // 当前请求地址
    val photoListLive = MutableLiveData<List<HomeData>>()    // 数据
    private var ruleUtil: RuleUtil? = null
    val dataStatusLive = MutableLiveData<Int>()   // 状态码, 用于改变底部刷新的状态：网络不好、正在加载
    var isBeGetVale = false   // 正在加载数据，防止多次加载
    var pageNum = MutableLiveData(-1)   // 页码

    fun clearImageUrl() {
        photoListLive.value = null
    }

    fun setPageNum(num: Int) {
        pageNum.value = num
    }

    fun setUrl(name: String) {
        curReqName.value = name
        curReqUrl.value = ruleUtil?.getUrl(name, 1)
    }

    fun getSortNameList(): ArrayList<String>? {
        return ruleUtil?.sortNameList
    }

    fun setRuleUtil(rule: Rule) {
        ruleUtil = RuleUtil(rule, AnalyzeRule())
        if (rule.tabName != "") {
            NetWork.get(ruleUtil!!.getTabFrom(), rule, object : NetWork.NetWorkCall {
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {
                    ruleUtil!!.getTab(
                        String(
                            response.body!!.bytes(),
                            charset = Charset.forName(ruleUtil!!.getCharset())
                        )
                    )
                    sortMap.postValue(ruleUtil!!.sortMap)
                }

            })

        } else {
            sortMap.value = ruleUtil!!.getTab()
        }
    }

    // 获取html后的回调
    private fun responseCall(html: String) {
        // 解析数据
        try {
            val homeDataList = ruleUtil!!.getHomeDataList(html)
            if (homeDataList.isEmpty()) {
                dataStatusLive.postValue(DATA_STATUS_NOR_MORE)
                pageNum.postValue(pageNum.value?.minus(1))
                return
            }
            val values = when {
                !isRefresh -> {       // 不是顶部刷新
                    val values = photoListLive.value?.toMutableList()
                    for (value in homeDataList) {
                        values?.add(value)
                    }
                    values
                }
                else -> homeDataList
            }
            photoListLive.postValue(values)
            // 设置状态码
            dataStatusLive.postValue(DATA_STATUS_LOAD_NORMAL)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 获取数据
    fun getHomeDataList() {
        dataStatusLive.value = DATA_STATUS_LOAD_NORMAL
        Log.d(TAG, "getHomeDataList: ${curReqUrl.value}")
        if (curReqUrl.value == null) return
        // 用于防止加载多次
        if (isBeGetVale) return
        isBeGetVale = true


        ruleUtil?.let {
            when (it.getRuleReqMethod().toLowerCase(Locale.ROOT)) {
                "get" -> {
                    it.getUrl(curReqName.value!!, pageNum.value!!).let { url ->
                        curReqUrl.value = url
                        NetWork.get(url, it.rule, this)
                    }
                }
                else -> {
                    it.getUrl(curReqName.value!!, pageNum.value!!).let { url ->
                        curReqUrl.value = url
                        val data = it.getNewData(curReqName.value!!, pageNum.value!!)
                        NetWork.post(url, data, it.rule, this@HomeDataViewModel)
                    }
                }
            }
        }
    }

    override fun onFailure(call: Call, e: IOException) {
        Log.d("onFailureUrl", call.request().url.toString())
        isBeGetVale = false
        // 设置状态码
        dataStatusLive.postValue(DATA_STATUS_NETWORK_ERROR)
        e.printStackTrace()
    }

    override fun onResponse(call: Call, response: Response) {
        responseCall(String(response.body!!.bytes(), Charset.forName(ruleUtil!!.getCharset())))
        isBeGetVale = false
    }

}