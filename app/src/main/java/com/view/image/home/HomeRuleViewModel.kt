package com.view.image.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.view.image.analyzeRule.Rule
import com.view.image.fileUtil.RuleFile


class HomeRuleViewModel(application: Application) : AndroidViewModel(application) {
    private val _ruleLive = MutableLiveData<Rule>()
    private val _curRuleNumLive = MutableLiveData(0)
    private val _ruleListLive = MutableLiveData<List<Rule>>()

    val ruleListLive: LiveData<List<Rule>>
        get() = _ruleListLive

    val ruleLive: LiveData<Rule>
        get() = _ruleLive
    val curRulePosition: LiveData<Int>
        get() = _curRuleNumLive


    /**
     * @param reLoad 重新加载
     */
    fun loadRuleList(reLoad: Boolean = false) {
        if (_ruleListLive.value.isNullOrEmpty() || reLoad) {
            _ruleListLive.value = RuleFile.ruleStrToArrayRule(RuleFile.readRule(getApplication()))
            _ruleLive.value = _ruleListLive.value!![_curRuleNumLive.value!!]
        }
    }

    fun getRuleNameList(): List<String> {
        return RuleFile.getRuleNameList(_ruleListLive.value!!)
    }

    fun setRule(curRulePosition: Int) {
        _ruleLive.value = RuleFile.getRule(_ruleListLive.value!!, curRulePosition)
        _curRuleNumLive.value = curRulePosition
    }
}