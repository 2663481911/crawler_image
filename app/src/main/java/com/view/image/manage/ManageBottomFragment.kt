package com.view.image.manage

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import com.view.image.prompt.PromptBox
import com.view.image.setting.Setting.TAG
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


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
            when {
                isChecked -> manageRuleViewModel.allSelect()
                else -> manageRuleViewModel.norSelect()
            }
        }

        // 选中数量
        manageRuleViewModel.selectCheckBoxSize.observe(viewLifecycleOwner, {
            Log.d(TAG, "onViewCreated: $it")
            when (it) {
                manageRuleViewModel.ruleListLiveData.value!!.size -> binding.allSelect.isChecked =
                    true
                else -> {
                    manageRuleViewModel.partSelect()
                    binding.allSelect.isChecked = false
                }
            }
        })


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
    @SuppressLint("SimpleDateFormat")
    fun shareSelectRule() {
        val selectCheckbox = manageRuleViewModel.selectCheckbox.value
        Log.d(TAG, "shareSelectRule: $selectCheckbox")
        val ruleList = ArrayList<Rule>()
        for (pos in selectCheckbox?.keys!!) {
            val rule = manageRuleViewModel.ruleListLiveData.value?.get(pos)
            rule?.let { ruleList.add(it) }
        }

        val dateFormat = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss") //创建一个data format对象
        val date2 = Date() //利用Date()获取当前时间
        val name = dateFormat.format(date2) //格式化时间,并用String对象存储

        val ruListString = Gson().toJson(ruleList).toString()
        val shareName = "$name$-${ruleList.size}.json"
        RuleFile.saveFile(requireContext(), ruListString, shareName)
        RuleFile.shareRule(requireContext(), shareName)
    }
}