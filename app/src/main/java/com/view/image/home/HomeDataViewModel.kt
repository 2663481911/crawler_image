package com.view.image.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.view.image.analyzeRule.AnalyzeRule
import com.view.image.analyzeRule.Rule
import com.view.image.analyzeRule.RuleUtil
import com.view.image.model.NetWork
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
    val pageNum: LiveData<Int>
        get() = _pageNum
    val dataUrl: LiveData<String>
        get() = _dataUrl
    val dataStatusLive: LiveData<Int>
        get() = _dataStatusLive
    val photoListLive: LiveData<List<HomeData>>
        get() = _photoListLive
    var isRefresh: Boolean = false    // 是不是下拉刷新, 加载把位置调到0
    val curReqUrl: LiveData<String>
        get() = _curReqUrl

    private var _curReqUrl = MutableLiveData<String>()     // 当前请求地址
    private val _dataUrl = MutableLiveData<String>()    // 请求地址
    private val _photoListLive = MutableLiveData<List<HomeData>>()    // 数据
    private var ruleUtil: RuleUtil? = null
    private val _dataStatusLive = MutableLiveData<Int>()   // 状态码, 用于改变底部刷新的状态：网络不好、正在加载
    var isBeGetVale = false   // 正在加载数据，防止多次加载
    private var _pageNum = MutableLiveData(1)   // 页码

    fun clearImageUrl() {
        _photoListLive.value = null
    }

    fun setPageNum(num: Int) {
        _pageNum.value = num
    }

    /**
     * 获取sort请求地址
     */
    fun setUrl(name: String) {
        ruleUtil!!.sortMap[name]?.let {
            val hrefList = ruleUtil!!.getIndexHref(it)
            if (hrefList.size == 2) {
                if (pageNum.value == 1)
                    _dataUrl.value = hrefList[0]
                else {
                    _dataUrl.value = hrefList[1]
                }
            }
        }
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

                _dataStatusLive.postValue(DATA_STATUS_NOR_MORE)
                _pageNum.postValue(pageNum.value?.minus(1))
                return
            }
            val values = when {
                !isRefresh -> {       // 不是顶部刷新
                    val values = _photoListLive.value?.toMutableList()
                    for (value in homeDataList) {
                        values?.add(value)
                    }
                    values
                }
                else -> homeDataList
            }
            _photoListLive.postValue(values)
            // 设置状态码
            _dataStatusLive.postValue(DATA_STATUS_LOAD_NORMAL)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 获取数据
    fun getHomeDataList() {
        _dataStatusLive.value = DATA_STATUS_LOAD_NORMAL
        if (_dataUrl.value == null) return
        // 用于防止加载多次
        if (isBeGetVale) return
        isBeGetVale = true

        when {
            ruleUtil?.getRuleReqMethod()?.toLowerCase(Locale.ROOT) == "get" -> {
                // 获取请求地址
                dataUrl.value?.replace("@page", _pageNum.value.toString())?.let {
                    _curReqUrl.value = it
                    NetWork.get(it, ruleUtil!!.rule, this)
                }
            }
            else -> {
                dataUrl.value?.replace("@page", _pageNum.value.toString())?.let {

                    val data = ruleUtil?.getNewData(_dataUrl.value!!, _pageNum.value!!)
                    NetWork.post(it, data!!, ruleUtil!!.rule, this@HomeDataViewModel)

                }


            }
        }
    }

    override fun onFailure(call: Call, e: IOException) {
        Log.d("onFailureUrl", call.request().url.toString())
        isBeGetVale = false
        // 设置状态码
        _dataStatusLive.postValue(DATA_STATUS_NETWORK_ERROR)
        e.printStackTrace()
    }

    override fun onResponse(call: Call, response: Response) {
        ruleUtil?.setRequestUrl(call.request().url.toString())
        responseCall(String(response.body!!.bytes(),
            charset = Charset.forName(ruleUtil!!.getCharset())))
        isBeGetVale = false
    }

}