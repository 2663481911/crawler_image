package com.view.image.page

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.view.image.analyzeRule.Rule
import com.view.image.analyzeRule.RuleUtil
import com.view.image.model.NetWork
import com.view.image.setting.Setting.TAG
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Response
import java.io.IOException

class PageViewModel : ViewModel(), NetWork.NetWorkCall {

    private val _imgUrlListLive = MutableLiveData<ArrayList<String>>()
    val imgUrlListLive: LiveData<ArrayList<String>>
        get() = _imgUrlListLive

    private val _hrefLive = MutableLiveData<String>()
    val hrefLive: LiveData<String>
        get() = _hrefLive

    private val _ruleLive = MutableLiveData<Rule>()
    val ruleLive: LiveData<Rule>
        get() = _ruleLive

    private val hrefListLive = MutableLiveData<ArrayList<String>>()

    private var ruleUtil: RuleUtil? = null

    fun setRule(rule: Rule) {
        this._ruleLive.value = rule
    }

    fun setRuleUtil(ruleUtil: RuleUtil) {
        this.ruleUtil = ruleUtil
    }

    fun setHref(href: String) {
        this._hrefLive.value = href
    }

    fun getImgList(href: String) {
        if (hrefListLive.value.isNullOrEmpty()) {
            hrefListLive.postValue(arrayListOf(href))
        } else {
            val arrayListOf = hrefListLive.value
            arrayListOf?.add(href)
            hrefListLive.postValue(arrayListOf)
        }
        ruleUtil?.setRequestUrl(href)
        NetWork.get(href, ruleLive.value!!, this)
    }

    override fun onFailure(call: Call, e: IOException) {
        e.printStackTrace()
    }


    override fun onResponse(call: Call, response: Response) {
        String(response.body!!.bytes(), charset(ruleUtil!!.getCharset())).let {
            val arrayList = ruleUtil?.getImgList(it) as ArrayList<String>
            Log.d(TAG, "onResponse: $it")
            if (arrayList.isNotEmpty()) {
                if (!_imgUrlListLive.value.isNullOrEmpty()) {
                    val toMutableList = _imgUrlListLive.value
                    toMutableList?.addAll(arrayList)
                    _imgUrlListLive.postValue(toMutableList)
                } else _imgUrlListLive.postValue(arrayList)

                // 当前viewModel销毁停止
                viewModelScope.launch {
                    // 获取下一页
                    ruleUtil?.getImageNextPageHref(it, hrefLive.value!!)?.let { it1 ->
                        if (hrefListLive.value?.contains(it1) != true && it1.isNotEmpty())
                            getImgList(it1)
                    }
                }
            }
        }
    }


}