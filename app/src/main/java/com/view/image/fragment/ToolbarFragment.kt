package com.view.image.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.view.image.R
import com.view.image.activity.RuleActivity
import com.view.image.databinding.FragmentToolbarBinding
import com.view.image.model.Rule
import com.view.image.model.RuleViewModel


class ToolbarFragment : Fragment() {

    lateinit var binding: FragmentToolbarBinding
    lateinit var ruleViewModel: RuleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        ruleViewModel = ViewModelProvider(activity ?: this).get(RuleViewModel::class.java)
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
            R.id.edit_rule -> {
                Intent(context, RuleActivity::class.java).apply {
                    putExtra("rule", ruleViewModel.ruleLive.value)
                    startActivity(this)
                }
            }
            R.id.add_rule -> {
                Intent(context, RuleActivity::class.java).apply {
                    putExtra("rule", Rule())
                    startActivity(this)
                }
            }

            R.id.add_net_rule -> {
                inputTitleDialog()
            }
        }
        return true
    }

    //弹出输入框
    private fun inputTitleDialog() {
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