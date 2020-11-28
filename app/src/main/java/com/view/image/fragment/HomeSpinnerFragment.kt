package com.view.image.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.view.image.R
import com.view.image.analyzeRule.AnalyzeRule
import com.view.image.analyzeRule.RuleUtil
import com.view.image.databinding.FragmentHomeSpinnerBinding
import com.view.image.model.HomeDataViewModel
import com.view.image.model.RuleViewModel


class HomeSpinnerFragment : Fragment() {
    lateinit var homeSpinnerBinding: FragmentHomeSpinnerBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        homeSpinnerBinding = FragmentHomeSpinnerBinding.inflate(layoutInflater)
        return homeSpinnerBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 传入activity保证使用同一个HomeDataViewModel，数据同步
        val homeDataViewModel =
            ViewModelProvider(activity ?: this).get(HomeDataViewModel::class.java)

        // 观察pageNum变化改变
        homeDataViewModel.pageNum.observe(this.viewLifecycleOwner, {
            homeSpinnerBinding.setPageNum.setText(it.toString())
        })

        homeSpinnerBinding.setPageNum.setOnEditorActionListener { it, _, _ ->
            val page = homeSpinnerBinding.setPageNum.text.toString().toInt()
            if (homeDataViewModel.pageNum.value != page) {
                homeDataViewModel.isRefresh = true
                homeDataViewModel.setPageNum(page)
            }
            it.clearFocus()
            false
        }

        val ruleViewModel = ViewModelProvider(activity ?: this).get(RuleViewModel::class.java)
        var sortMap: Map<String, String>? = null

        ruleViewModel.ruleLive.observe(this.viewLifecycleOwner, {
            Log.d("rule", it.homeList)
            val ruleUtil = RuleUtil(it, AnalyzeRule())
            homeDataViewModel.setRuleUtil(ruleUtil)
            sortMap = ruleUtil.getSortMap()
            updateSpinner(sortMap!!.keys.toList())

        })
//        val rules = readJson()

        homeSpinnerBinding.spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, positon: Int, id: Long,
            ) {
                val itemString = parent.getItemAtPosition(positon).toString()
                Log.d("sort", itemString)
                sortMap?.get(itemString)?.let { homeDataViewModel.setUrl(it) }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    private fun updateSpinner(sortNameList: List<String>) {
        // 更新下拉选项
        val arrayAdapter = ArrayAdapter(requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            sortNameList)
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        homeSpinnerBinding.spinner.adapter = arrayAdapter
    }

}