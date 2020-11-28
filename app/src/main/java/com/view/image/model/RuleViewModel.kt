package com.view.image.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class RuleViewModel : ViewModel() {
    private val _ruleLive = MutableLiveData<Rule>()
    val ruleLive: LiveData<Rule>
        get() = _ruleLive

    fun setRule(rule: Rule) {
        _ruleLive.value = rule
    }

}