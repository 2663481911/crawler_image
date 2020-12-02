package com.view.image.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.view.image.analyzeRule.RuleUtil
import okhttp3.Call
import okhttp3.Response
import java.io.IOException

class GalleryViewModel : ViewModel() {
    private val _imgUrlListLive = MutableLiveData<ArrayList<String>>()
    val imgUrlListLive: LiveData<ArrayList<String>>
        get() = _imgUrlListLive

    private val _hrefLive = MutableLiveData<String>()
    val hrefLive: LiveData<String>
        get() = _hrefLive

    private val _ruleLive = MutableLiveData<Rule>()
    val ruleLive: LiveData<Rule>
        get() = _ruleLive

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
        NetWork.get(href, ruleLive.value!!.cookie, object : NetWork.NetWorkCall {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                ruleUtil?.setRequestUrl(href)
                _imgUrlListLive.postValue(response.body?.string()
                    ?.let { ruleUtil?.getImgList(it) } as ArrayList<String>)
            }


        })
    }


}