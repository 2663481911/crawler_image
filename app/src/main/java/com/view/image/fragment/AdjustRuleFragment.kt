package com.view.image.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.view.image.activity.RuleActivity
import com.view.image.adapter.EditRuleAdapter
import com.view.image.databinding.FragmentAdjustRuleBinding
import com.view.image.model.AdjustRuleViewModel
import com.view.image.model.EDIT_RULE_CODE
import com.view.image.model.SAVE_RULE_CODE


class AdjustRuleFragment : Fragment() {
    lateinit var binding: FragmentAdjustRuleBinding
    lateinit var adjustRuleViewModel: AdjustRuleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAdjustRuleBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adjustRuleViewModel =
            ViewModelProvider(activity ?: this).get(AdjustRuleViewModel::class.java)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adjustRuleViewModel.ruleListLiveData.observe(viewLifecycleOwner, {
            val editAdapter = EditRuleAdapter(adjustRuleViewModel, it)
            binding.recyclerView.adapter = editAdapter
        })

        adjustRuleViewModel.editPosition.observe(viewLifecycleOwner, {
            Intent(requireContext(), RuleActivity::class.java).apply {
                putExtra("rule", adjustRuleViewModel.ruleListLiveData.value!![it])
                putExtra("code", EDIT_RULE_CODE)
                startActivityForResult(this, EDIT_RULE_CODE)
            }
        })
        adjustRuleViewModel.setRuleList()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            SAVE_RULE_CODE -> {
                adjustRuleViewModel.changRuleVale()
                adjustRuleViewModel.setRuleList()
            }
        }
    }
}