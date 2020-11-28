package com.view.image.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.view.image.databinding.FragmentRuleBinding
import com.view.image.model.Rule

class RuleFragment : Fragment() {
    lateinit var binding: FragmentRuleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRuleBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val rule = activity?.intent?.getSerializableExtra("rule") as Rule
        showRule(rule)
    }

    private fun showRule(rule: Rule) {
        binding.apply {
            ruleName.setText(rule.sourceName)
            ruleSourceUrl.setText(rule.sourceUrl)
            ruleReqMethod.setText(rule.reqMethod)
            ruleSort.setText(rule.sortUrl)

            ruleHomeList.setText(rule.homeList)
            ruleHomeHref.setText(rule.homeHref)
            ruleHomeSrc.setText(rule.homeSrc)
            ruleHomeTitle.setText(rule.homeTitle)
            replaceHomeSrc.setText(rule.homeSrcReplaceByJS)

            ruleImgPageList.setText(rule.imagePageList)
            ruleImgPageSrc.setText(rule.imagePageSrc)
            replacePageSrc.setText(rule.imageUrlReplaceByJS)

            ruleCookie.setText(rule.cookie)
            ruleAddJs.setText(rule.js)
            ruleJsMethod.setText(rule.jsMethod)
        }

    }

    private fun getEditRule() {
        val rule = Rule()
        rule.apply {
            sourceName = binding.ruleName.text.toString()
            sourceUrl = binding.ruleSourceUrl.text.toString()
            reqMethod = binding.ruleReqMethod.text.toString()
            sortUrl = binding.ruleSort.text.toString()

            homeList = binding.ruleHomeList.text.toString()
            homeSrc = binding.ruleHomeSrc.text.toString()
            homeHref = binding.ruleHomeHref.text.toString()
            homeTitle = binding.ruleHomeTitle.text.toString()
            homeSrcReplaceByJS = binding.replaceHomeSrc.text.toString()

            imagePageList = binding.ruleImgPageList.text.toString()
            imagePageSrc = binding.ruleImgPageSrc.text.toString()
            imageUrlReplaceByJS = binding.replacePageSrc.text.toString()

            cookie = binding.ruleCookie.text.toString()
            js = binding.ruleAddJs.text.toString()
            jsMethod = binding.ruleJsMethod.text.toString()
        }
    }

}