package com.view.image.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.view.image.R
import com.view.image.databinding.FragmentToolbarBinding
import com.view.image.model.ManageRuleViewModel
import com.view.image.model.SAVE_RULE_CODE


class ManageRuleToolbarFragment : Fragment() {
    lateinit var binding: FragmentToolbarBinding
    lateinit var manageRuleViewModel: ManageRuleViewModel

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
        inflater.inflate(R.menu.adjust_rule_toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageRuleViewModel =
            ViewModelProvider(activity ?: this).get(ManageRuleViewModel::class.java)
        manageRuleViewModel.changRule.observe(viewLifecycleOwner, {
            if (it == true)
                activity?.setResult(SAVE_RULE_CODE)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                Log.d("Manage", "saveHome")
                manageRuleViewModel.saveRuleList()
                activity?.finish()
            }
        }
        return true
    }

}