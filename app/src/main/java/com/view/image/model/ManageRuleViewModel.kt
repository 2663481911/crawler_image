package com.view.image.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.view.image.fileUtil.RuleFile

class ManageRuleViewModel(application: Application) : AndroidViewModel(application) {
    private val _changRule = MutableLiveData(false)
    private val _ruleListLiveData = MutableLiveData<List<Rule>>()
    private val _editPosition = MutableLiveData<Int>()
    var toTopPos = 0
    val ruleListLiveData: LiveData<List<Rule>>
        get() = _ruleListLiveData

    val editPosition: LiveData<Int>
        get() = _editPosition

    val changRule: LiveData<Boolean>
        get() = _changRule

    // 删除规则
    fun removeRule(position: Int) {
        toTopPos = position
        _changRule.value = true
        val ruleMutableList = _ruleListLiveData.value?.toMutableList()
        ruleMutableList?.removeAt(position)
        _ruleListLiveData.value = ruleMutableList

    }

    // 置顶规则
    fun ruleToTop(position: Int) {
        toTopPos = position
        _changRule.value = true
        Log.d("position", position.toString())
        val ruleMutableList = _ruleListLiveData.value?.toMutableList()
        ruleMutableList?.add(0, ruleMutableList.removeAt(position))
        _ruleListLiveData.value = ruleMutableList
    }

    // 编辑规则
    fun editRule(position: Int) {
        _editPosition.value = position
    }

    fun changRuleVale() {
        _changRule.value = true
    }

    // 读取规则
    fun setRuleList() {
        _ruleListLiveData.value = RuleFile.ruleStrToArrayRule(RuleFile.readRule(getApplication()))
    }

    // 保存规则
    fun saveRuleList() {
        Log.d("ruleSize", _ruleListLiveData.value?.size.toString())
        RuleFile.saveRule(getApplication(), _ruleListLiveData.value!!)
    }

}