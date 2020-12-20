package com.view.image.manage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.view.image.analyzeRule.Rule
import com.view.image.fileUtil.RuleFile

class ManageRuleViewModel(application: Application) : AndroidViewModel(application) {
    // 选中状态：全选或不选
    companion object {
        const val NOR_SELECT = 0
        const val ALL_SELECT = 1
        const val PART_SELECT = 2
        const val REMOVE_SELECT_RULE = 2
        const val SHARE_SELECT_RULE = 3
    }

    val changRule = MutableLiveData(false)    // 标记规则是否改变，用于返回是否重新加载
    val ruleListLiveData = MutableLiveData<ArrayList<Rule>>()     // 保存rule列表
    val editPosition = MutableLiveData<Int>()    // 正在编辑的规则位置
    val selectCheckbox = MutableLiveData(HashMap<Int, Boolean>())   // 标记选中的规则
    val selectCheckBoxSize = MutableLiveData<Int>()

    val selectAllOrNor = MutableLiveData<Int>()    // 全选或不选
    val selectShareOrRemove = MutableLiveData<Int>()    // 点击分享和删除规则时响应
    val editChange = MutableLiveData(false)    // 编辑规则后返回重新加载当前规则

    // 编辑的规则是否改变
    fun editChange(isEditChang: Boolean) {
        editChange.value = isEditChang
    }

    // 向指定位置添加规则
    fun addRule(position: Int, rule: Rule) {
        ruleListLiveData.value?.add(position, rule)
    }

    // 移除指定位置规则
    fun removeRuleAt(position: Int) {
        ruleListLiveData.value?.removeAt(position)
    }

    // 编辑规则
    fun editRule(position: Int) {
        editPosition.value = position
    }

    // 规则是否改变
    fun changRuleListVale() {
        changRule.value = true
    }

    // 读取规则
    fun setRuleList() {
        ruleListLiveData.value = RuleFile.ruleStrToArrayRule(RuleFile.readRule(getApplication()))
    }

    // 保存规则
    fun saveRuleList() {
        RuleFile.saveRule(getApplication(), ruleListLiveData.value!!)
    }

    // 删除选择规则
    fun selectRemoveRule() {
        selectShareOrRemove.value = REMOVE_SELECT_RULE
        selectShareOrRemove.value = -1
    }

    // 分享选择规则
    fun selectShareRule() {
        selectShareOrRemove.value = SHARE_SELECT_RULE
        selectShareOrRemove.value = -1
    }

    // 全部选择
    fun allSelect() {
        selectAllOrNor.value = ALL_SELECT
    }

    // 部分选中
    fun partSelect() {
        selectAllOrNor.value = PART_SELECT
    }

    // 没有选
    fun norSelect() {
        if (selectAllOrNor.value != PART_SELECT) selectAllOrNor.value = NOR_SELECT
    }

}