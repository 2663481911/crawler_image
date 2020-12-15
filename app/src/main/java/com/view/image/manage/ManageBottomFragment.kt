package com.view.image.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.view.image.R
import com.view.image.analyzeRule.Rule
import com.view.image.databinding.FragmentManageBottomBinding
import com.view.image.fileUtil.RuleFile
import com.view.image.manage.ManageRuleViewModel.Companion.ALL_SELECT
import com.view.image.manage.ManageRuleViewModel.Companion.NOR_SELECT
import com.view.image.prompt.PromptBox


class ManageBottomFragment : Fragment() {

    lateinit var binding: FragmentManageBottomBinding
    lateinit var manageRuleViewModel: ManageRuleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentManageBottomBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageRuleViewModel =
            ViewModelProvider(activity ?: this).get(ManageRuleViewModel::class.java)

        binding.allSelect.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                manageRuleViewModel.selectAllOrNor.value = ALL_SELECT
            } else {
                manageRuleViewModel.selectAllOrNor.value = NOR_SELECT
            }
        }

        binding.menuGroup.setOnClickListener {
            // View当前PopupMenu显示的相对View的位置
            val popupMenu = PopupMenu(requireContext(), it)
            // menu布局
            popupMenu.menuInflater.inflate(R.menu.manage_bottom_menu, popupMenu.menu)
            // menu的item点击事件
            popupMenu.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener,
                PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    when (item!!.itemId) {
                        R.id.remove_select_rule -> {
                            PromptBox.removeDialog(requireContext(), object : PromptBox.DialogCall {
                                override fun positiveButton() {
                                    manageRuleViewModel.selectRemoveRule()
                                }

                                override fun cancel() {}
                            })
                        }

                        R.id.share_select_rule -> {
                            manageRuleViewModel.selectShareRule()
                            shareSelectRule()
                        }
                    }
                    return false
                }
            })
            popupMenu.show()
        }
    }

    // 分享选中规则
    fun shareSelectRule() {
        val selectCheckbox = manageRuleViewModel.selectCheckbox.value
        val ruleList = ArrayList<Rule>()
        for (pos in selectCheckbox?.keys!!) {
            val rule = manageRuleViewModel.ruleListLiveData.value?.get(pos)
            if (rule != null) {
                ruleList.add(rule)
            }
        }
        val ruListString = Gson().toJson(ruleList).toString()
        val shareName = "share.rule"
        RuleFile.saveFile(requireContext(), ruListString, shareName)
        RuleFile.shareRule(requireContext(), shareName)
    }

}