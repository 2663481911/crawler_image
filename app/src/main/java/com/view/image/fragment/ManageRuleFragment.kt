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

        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireContext(),
            DividerItemDecoration.VERTICAL))

        manageRuleViewModel.editChange.observe(viewLifecycleOwner, {
            if (it) {
                manageRuleViewModel.editPosition.value?.let { position ->
                    editAdapter.updateItem(manageRuleViewModel.ruleListLiveData.value!![position],
                        position)
                }
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            SAVE_RULE_CODE -> {
                manageRuleViewModel.editChange(true)
                manageRuleViewModel.changRuleVale()
                manageRuleViewModel.setRuleList()
            }
        }
    }
}