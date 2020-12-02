package com.view.image.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.view.image.fileUtil.RuleFile


class HomeRuleViewModel(application: Application) : AndroidViewModel(application) {
    private val _ruleLive = MutableLiveData<Rule>()
    private val _curRuleNumLive = MutableLiveData<Int>(0)
    private val _ruleListLive = MutableLiveData<List<Rule>>()

    val ruleListLive: LiveData<List<Rule>>
        get() = _ruleListLive

    val ruleLive: LiveData<Rule>
        get() = _ruleLive

    /**
     * @param reLoad 重新加载
     */
    fun loadRuleList(reLoad: Boolean = false) {
        if (_ruleListLive.value.isNullOrEmpty() || reLoad) {
            _ruleListLive.value = RuleFile.ruleStrToArrayRule(RuleFile.readRule(getApplication()))
            _ruleLive.value = _ruleListLive.value!![0]
        }
    }

    fun getRuleNameList(): List<String> {
        return RuleFile.getRuleNameList(_ruleListLive.value!!)
    }

    fun setRule(curRulePosition: Int) {
        _ruleLive.value = RuleFile.getRule(_ruleListLive.value!!, curRulePosition)
        _curRuleNumLive.value = curRulePosition
//        val ruleList = RuleFile.moveCutRulePositionIn0(_ruleListLive.value!!, curRulePosition)
//        RuleFile.saveRule(getApplication(), Gson().toJson(ruleList).toString())

//        _ruleListLive.value =
//            RuleFile.moveCutRulePositionIn0(_ruleListLive.value!!, curRulePosition)
//        RuleFile.saveRule(getApplication(), Gson().toJson(_ruleListLive.value).toString())
    }
}