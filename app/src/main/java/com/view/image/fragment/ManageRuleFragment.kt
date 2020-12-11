package com.view.image.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.view.image.activity.RuleActivity
import com.view.image.adapter.ManageRuleAdapter
import com.view.image.databinding.FragmentManageRuleBinding
import com.view.image.model.EDIT_RULE_CODE
import com.view.image.model.ManageRuleViewModel
import com.view.image.model.ManageRuleViewModel.Companion.ALL_SELECT
import com.view.image.model.ManageRuleViewModel.Companion.NOR_SELECT
import com.view.image.model.ManageRuleViewModel.Companion.REMOVE_SELECT_RULE
import com.view.image.model.ManageRuleViewModel.Companion.SHARE_SELECT_RULE
import com.view.image.model.SAVE_RULE_CODE


class ManageRuleFragment : Fragment() {
    lateinit var binding: FragmentManageRuleBinding
    lateinit var manageRuleViewModel: ManageRuleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentManageRuleBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manageRuleViewModel =
            ViewModelProvider(activity ?: this).get(ManageRuleViewModel::class.java)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())


        manageRuleViewModel.editPosition.observe(viewLifecycleOwner, {
            RuleActivity.actionStart(
                requireActivity(),
                EDIT_RULE_CODE,
                manageRuleViewModel.ruleListLiveData.value!![it], it,
            )
        })

        manageRuleViewModel.setRuleList()

        val editAdapter = ManageRuleAdapter(manageRuleViewModel,
            manageRuleViewModel.ruleListLiveData.value!!)

        binding.recyclerView.adapter = editAdapter

        // 禁用动画，页面更新时闪烁问题
        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        // 添加分割线
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireContext(),
            DividerItemDecoration.VERTICAL))

        // 规则是否改变
        manageRuleViewModel.editChange.observe(viewLifecycleOwner, {
            if (it) {
                manageRuleViewModel.editPosition.value?.let { position ->
                    editAdapter.updateItem(manageRuleViewModel.ruleListLiveData.value!![position],
                        position)
                }
            }
        })


        manageRuleViewModel.selectShareOrRemove.observe(viewLifecycleOwner, {
            when (it) {
                REMOVE_SELECT_RULE -> {
                    editAdapter.removeRuleList()
                    manageRuleViewModel.initSelectShareOrRemove()
                }
                SHARE_SELECT_RULE -> {
                    manageRuleViewModel.selectCheckbox.value = editAdapter.getCheckboxMap()
                    manageRuleViewModel.initSelectShareOrRemove()
                }
            }
        })

        manageRuleViewModel.selectAllOrNor.observe(viewLifecycleOwner, {
            when (it) {
                ALL_SELECT -> editAdapter.setCheckboxAll()
                NOR_SELECT -> editAdapter.setCheckboxNor()
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            SAVE_RULE_CODE -> {
                manageRuleViewModel.editChange(true)
                manageRuleViewModel.changRuleListVale()
                manageRuleViewModel.setRuleList()
            }
        }
    }
}