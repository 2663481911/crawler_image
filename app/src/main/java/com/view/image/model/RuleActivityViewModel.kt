package com.view.image.model


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RuleActivityViewModel : ViewModel() {
    val ruleLiveData = MutableLiveData<Rule>()
    val isGetRuleLive = MutableLiveData(0)
}