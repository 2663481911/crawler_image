package com.view.image.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.view.image.analyzeRule.Rule
import com.view.image.fileUtil.RuleFile

class ManageRuleViewModel(application: Application) : AndroidViewModel(application) {
    // 选中状态：全选或不选
    companion object {
        const val NOR_SELECT = 0
        const val ALL_SELECT = 1
        const val REMOVE_SELECT_RULE = 2
        const val SHARE_SELECT_RULE = 3
    }

    val changRule = MutableLiveData(false)    // 标记规则是否改变，用于返回是否重新加载
    val ruleListLiveData = MutableLiveData<ArrayList<Rule>>()     // 保存rule列表
    val editPosition = MutableLiveData<Int>()    // 正在编辑的规则位置
    val selectCheckbox = MutableLiveData<HashMap<Int, Boolean>>()    // 标记选中的规则
    val selectAllOrNor = MutableLiveData<Int>()    // 全选或不选
    val selectShareOrRemove = MutableLiveData<Int>()    // 点击分享和删除规则时响应
    val editChange = MutableLiveData(false)    // 编辑规则后返回重新加载当前规则


    fun editChange(isEditChang: Boolean) {
        editChange.value = isEditChang
    }

    // 编辑规则
    fun editRule(position: Int) {
        editPosition.value = position
    }

    fun changRuleListVale() {
        changRule.value = true
    }

    // 读取规则
    fun setRuleList() {
        ruleListLiveData.value = RuleFile.ruleStrToArrayRule(RuleFile.readRule(getApplication()))
    }

    // 保存规则
    fun saveRuleList() {
        Log.d("ruleSize", ruleListLiveData.value?.size.toString())
        RuleFile.saveRule(getApplication(), ruleListLiveData.value!!)
    }

    fun initSelectShareOrRemove() {
        selectShareOrRemove.value = -1
    }

}