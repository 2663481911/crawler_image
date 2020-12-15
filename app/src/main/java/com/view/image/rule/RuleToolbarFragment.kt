package com.view.image.rule


import android.app.Activity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.view.image.R
import com.view.image.analyzeRule.Rule
import com.view.image.databinding.FragmentToolbarBinding
import com.view.image.debug.DebugActivity
import com.view.image.fileUtil.ClipBoar
import com.view.image.fileUtil.RuleFile
import com.view.image.model.ADD_RULE_CODE
import com.view.image.model.EDIT_RULE_CODE
import com.view.image.model.SAVE_RULE_CODE


class RuleToolbarFragment : Fragment() {
    lateinit var binding: FragmentToolbarBinding
    lateinit var viewModel: RuleActivityViewModel
    private var requestCode: Int = -1
    private var rulePosition: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentToolbarBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rulePosition = activity?.intent!!.getIntExtra("rulePosition", 0)
        requestCode = activity?.intent!!.getIntExtra("code", -1)
        viewModel = ViewModelProvider(activity ?: this).get(RuleActivityViewModel::class.java)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.let {
                it.setDisplayHomeAsUpEnabled(true)
                it.setHomeAsUpIndicator(R.drawable.back)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.rule_toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                activity?.setResult(Activity.RESULT_CANCELED)
                activity?.finish()
            }

            R.id.save_rule -> {
                viewModel.isGetRuleLive.value = true
                val rule = viewModel.ruleLiveData.value
                when (requestCode) {
                    ADD_RULE_CODE -> {
                        rule?.let { RuleFile.addRule(requireContext(), it) }
                    }

                    EDIT_RULE_CODE -> {
                        rule?.let { RuleFile.editRule(requireContext(), it, rulePosition) }
                    }
                }
                Toast.makeText(requireContext(), "保存成功", Toast.LENGTH_SHORT).show()
                activity?.setResult(SAVE_RULE_CODE)
                activity?.finish()
            }

            R.id.paste_rule -> {
                val textFromClip = ClipBoar.getTextFromClip(requireContext())
                try {
                    val rule = Gson().fromJson(textFromClip, Rule::class.java)
                    viewModel.ruleLiveData.value = rule
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "格式错误", Toast.LENGTH_SHORT).show()
                }

            }
            R.id.copy_rule -> {
                val rule = activity?.intent?.getSerializableExtra("rule") as Rule
                ClipBoar.putTextIntoClip(requireContext(), Gson().toJson(rule).toString())
                Toast.makeText(requireContext(), "复制成功", Toast.LENGTH_SHORT).show()
            }

            R.id.debug_rule -> {
                viewModel.isGetRuleLive.value = true
                val rule = viewModel.ruleLiveData.value
                rule?.let {
                    DebugActivity.actionStart(requireContext(), it)
                }
            }
        }
        return true
    }

}