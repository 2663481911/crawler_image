package com.view.image.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.view.image.R
import com.view.image.databinding.FragmentHomeSpinnerBinding


class HomeSpinnerFragment : Fragment() {
    lateinit var binding: FragmentHomeSpinnerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeSpinnerBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 传入activity保证使用同一个HomeDataViewModel，数据同步
        // 关于数据的viewModel
        val homeDataViewModel =
            ViewModelProvider(activity ?: this).get(HomeDataViewModel::class.java)

        // 观察页码的变化，请求数据
        homeDataViewModel.pageNum.observe(this.viewLifecycleOwner, {
            binding.setPageNum.setText(it.toString())
        })

        // 设置页码
        binding.setPageNum.setOnEditorActionListener { it, _, _ ->
            val page = binding.setPageNum.text.toString().toInt()
            if (homeDataViewModel.pageNum.value != page) {
                homeDataViewModel.isRefresh = true
                homeDataViewModel.setPageNum(page)
            }
            it.clearFocus()
            false
        }

        // 关于规则的viewModel
        val ruleViewModel = ViewModelProvider(activity ?: this).get(HomeRuleViewModel::class.java)

        // 观察规则变化
        ruleViewModel.ruleLive.observe(this.viewLifecycleOwner, {
            activity?.title = it.sourceName
            homeDataViewModel.setRuleUtil(it)
        })

        homeDataViewModel.sortMap.observe(this.viewLifecycleOwner, {
            val sortNameList = homeDataViewModel.getSortNameList()
            updateSpinner(sortNameList!!.toList())
//            homeDataViewModel.setUrl(sortNameList[0])
        })

        binding.spinnerView.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    positon: Int,
                    id: Long,
                ) {
                    val itemString = parent?.getItemAtPosition(positon).toString()
                    homeDataViewModel.setUrl(itemString)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }


    private fun updateSpinner(sortNameList: List<String>) {
        // 更新下拉选项
        val arrayAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item,
            sortNameList)
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        binding.spinnerView.adapter = arrayAdapter
    }

}