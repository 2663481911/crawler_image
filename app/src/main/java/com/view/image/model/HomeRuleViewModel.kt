package com.view.image.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.view.image.setting.RuleFile


class HomeRuleViewModel(application: Application) : AndroidViewModel(application) {
    private val _ruleLive = MutableLiveData<Rule>()
    private val _curRuleNumLive = MutableLiveData<Int>(0)
    private val _ruleListLive = MutableLiveData<List<Rule>>()

    val ruleListLive: LiveData<List<Rule>>
        get() = _ruleListLive

    val ruleLive: LiveData<Rule>
        get() = _ruleLive

    fun readRule() {
        _ruleListLive.value = RuleFile.ruleStrToArrayRule(RuleFile.readRule(getApplication()))
        _ruleLive.value = _ruleListLive.value!![0]
    }

    fun getRuleNameList(): List<String> {
        return RuleFile.getRuleNameList(_ruleListLive.value!!)
    }

    fun setRule(ruleName: String, curRulePosition: Int) {
        _ruleLive.value = RuleFile.getRule(_ruleListLive.value!!, ruleName)
        _curRuleNumLive.value = curRulePosition
//        val ruleList = RuleFile.moveCutRulePositionIn0(_ruleListLive.value!!, curRulePosition)
//        RuleFile.saveRule(getApplication(), Gson().toJson(ruleList).toString())

        _ruleListLive.value =
            RuleFile.moveCutRulePositionIn0(_ruleListLive.value!!, curRulePosition)
        RuleFile.saveRule(getApplication(), Gson().toJson(_ruleListLive.value).toString())
    }
}