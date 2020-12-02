package com.view.image.fragment

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
import com.view.image.activity.AdjustmentRuleActivity
import com.view.image.activity.RuleActivity
import com.view.image.databinding.FragmentToolbarBinding
import com.view.image.fileUtil.ClipBoar
import com.view.image.model.*


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
                Intent(context, RuleActivity::class.java).apply {
                    putExtra("rule", homeRuleViewModel.ruleLive.value)
                    putExtra("code", EDIT_RULE_CODE)
                    startActivityForResult(this, EDIT_RULE_CODE)
                }
            }
            R.id.add_rule -> {
                Intent(context, RuleActivity::class.java).apply {
                    putExtra("rule", Rule())
                    putExtra("code", ADD_RULE_CODE)
                    startActivityForResult(this, ADD_RULE_CODE)
                }
            }

            R.id.add_net_rule -> {
                inputTitleDialog(requireContext())
            }

            R.id.add_local_rule -> {
                activity?.let { MyPermissions.askPermissions(it) }
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                startActivityForResult(intent, 2)
            }

            R.id.copy_rule -> {
                val rule = homeRuleViewModel.ruleLive.value
                ClipBoar.putTextIntoClip(requireContext(), Gson().toJson(rule).toString())
                Toast.makeText(requireContext(), "复制成功", Toast.LENGTH_SHORT).show()
            }

            R.id.edit_rule -> {
                Intent(context, AdjustmentRuleActivity::class.java).also {
                    startActivityForResult(it, EDIT_RULE_CODE)
                }
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


}