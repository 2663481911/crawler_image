package com.view.image.fragment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.view.image.databinding.FragmentHomeNavigationViewBinding
import com.view.image.model.HomeRuleViewModel
import com.view.image.model.SAVE_RULE_CODE


class HomeNavigationViewFragment : Fragment() {
    lateinit var viewBinding: FragmentHomeNavigationViewBinding
    lateinit var homeRuleViewModel: HomeRuleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        Log.d("HomeNavigationView", "onCreateView")
        viewBinding = FragmentHomeNavigationViewBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeRuleViewModel = ViewModelProvider(activity ?: this).get(HomeRuleViewModel::class.java)
        homeRuleViewModel.loadRuleList()
        viewBinding.navigation.setNavigationItemSelectedListener {
            homeRuleViewModel.setRule(it.itemId)
            true
        }
    }


    override fun onStart() {
        super.onStart()
        Log.d("HomeNavigationView", "onStart")
//        homeRuleViewModel.readRule()
        homeRuleViewModel.ruleListLive.observe(viewLifecycleOwner, {
            val ruleNameList = homeRuleViewModel.getRuleNameList()
            initNavigationView(ruleNameList)
        })
    }

    private fun initNavigationView(ruleNameList: List<String>) {
        viewBinding.navigation.menu.clear()
        viewBinding.navigation.run {
            for (sum in ruleNameList.indices) {
                this.menu.add(1, sum, sum, ruleNameList[sum])
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("Toolbar_code", resultCode.toString())
        when (resultCode) {
            SAVE_RULE_CODE -> homeRuleViewModel.loadRuleList(true)
        }
    }
}