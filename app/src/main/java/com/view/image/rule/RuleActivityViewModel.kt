package com.view.image.rule


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.view.image.analyzeRule.Rule

class RuleActivityViewModel : ViewModel() {
    val ruleLiveData = MutableLiveData<Rule>()
    val isGetRuleLive = MutableLiveData(false)

    fun isGetRule() {
        isGetRuleLive.value = true
        isGetRuleLive.value = false
    }
}