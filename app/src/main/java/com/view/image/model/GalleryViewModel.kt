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
        NetWork.get(href, ruleLive.value!!.cookie, object : NetWork.NetWorkCall {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                ruleUtil?.setRequestUrl(href)
                response.body?.string()?.let {
                    val arrayList = ruleUtil?.getImgList(it) as ArrayList<String>
                    if (arrayList.isNotEmpty()) {
                        if (!_imgUrlListLive.value.isNullOrEmpty()) {
                            val toMutableList = _imgUrlListLive.value?.toMutableList()
                            toMutableList?.addAll(arrayList)
                            _imgUrlListLive.postValue(toMutableList as ArrayList<String>?)
//                            _imgUrlListLive.value!!.addAll(arrayList)

                        } else _imgUrlListLive.postValue(arrayList)


                        // 获取下一页
                        ruleUtil?.getImageNextPageHref(it, hrefLive.value!!)?.let { it1 ->
                            if (hrefListLive.value?.contains(it1) != true)
                                getImgList(it1)
                        }
                    }
                }
            }
        })
    }


}