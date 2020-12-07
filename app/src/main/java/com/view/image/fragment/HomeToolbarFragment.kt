package com.view.image.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.view.image.R
import com.view.image.activity.ManageRuleActivity
import com.view.image.activity.RuleActivity
import com.view.image.analyzeRule.Rule
import com.view.image.databinding.FragmentToolbarBinding
import com.view.image.fileUtil.ClipBoar
import com.view.image.fileUtil.RuleFile
import com.view.image.model.ADD_RULE_CODE
import com.view.image.model.EDIT_RULE_CODE
import com.view.image.model.HomeRuleViewModel
import com.view.image.model.PICK_FILE


class HomeToolbarFragment : Fragment() {

    lateinit var binding: FragmentToolbarBinding
    lateinit var homeRuleViewModel: HomeRuleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        homeRuleViewModel = ViewModelProvider(activity ?: this).get(HomeRuleViewModel::class.java)
        binding = FragmentToolbarBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.let {
                it.setDisplayHomeAsUpEnabled(true)
                it.setHomeAsUpIndicator(R.drawable.menu)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> activity?.findViewById<DrawerLayout>(R.id.home_drawer_layout)
                ?.openDrawer(GravityCompat.START)
            R.id.edit_cur_rule -> {
                RuleActivity.actionStart(requireActivity(), EDIT_RULE_CODE,
                    homeRuleViewModel.ruleLive.value!!,
                    homeRuleViewModel.curRulePosition.value!!)
            }
            R.id.add_rule -> {
                RuleActivity.actionStart(requireActivity(), ADD_RULE_CODE, Rule(), 0)
            }

            R.id.add_net_rule -> {
                inputTitleDialog(requireContext())
            }

            R.id.add_local_rule -> {
                pickFile()
            }

            R.id.copy_rule -> {
                val rule = homeRuleViewModel.ruleLive.value
                ClipBoar.putTextIntoClip(requireContext(), Gson().toJson(rule).toString())
                Toast.makeText(requireContext(), "复制成功", Toast.LENGTH_SHORT).show()
            }

            R.id.edit_rule -> {
                ManageRuleActivity.actionStart(requireActivity(), EDIT_RULE_CODE)
            }

        }
        return true
    }


    //弹出输入框
    private fun inputTitleDialog(context: Context) {
        val inputServer = EditText(context)
        inputServer.isFocusable = true
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setTitle(getString(R.string.net_add_rule_title))
//            .setIcon(R.drawable.save)
            .setView(inputServer)
            .setNegativeButton(getString(R.string.net_add_rule_cancel), null)
            .setPositiveButton(getString(R.string.net_add_rule_ok)) { _, _ ->
                val inputText = inputServer.text.toString()
                Log.d("inputText", inputText)
            }
        builder.show()
    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, PICK_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_FILE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val uri = data.data
                    Log.d("uri", uri.toString())
                    if (uri != null) {
                        val str = StringBuffer()
                        val inputStream = requireContext().contentResolver.openInputStream(uri)
                        // 执行文件读写操作
                        val buffer = ByteArray(1024)
                        var length: Int
                        if (inputStream != null) {
                            while (inputStream.read(buffer).also { length = it } != -1) {
                                str.append(String(buffer, 0, length))
                            }
                        }
                        val ruleList = RuleFile.ruleStrToArrayRule(str.toString())
                        val curRuleList =
                            RuleFile.ruleStrToArrayRule(RuleFile.readRule(requireContext()))
                                .toMutableList()
                        for (rule in ruleList) {
                            curRuleList.add(rule)
                        }
                        RuleFile.saveRule(requireContext(), curRuleList)
                        homeRuleViewModel.loadRuleList(true)
                    }
                }
            }
        }
    }


}