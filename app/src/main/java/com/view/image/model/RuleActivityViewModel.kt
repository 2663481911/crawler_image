package com.view.image.model


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.view.image.analyzeRule.Rule

class RuleActivityViewModel : ViewModel() {
    val ruleLiveData = MutableLiveData<Rule>()
    val isGetRuleLive = MutableLiveData(false)
}