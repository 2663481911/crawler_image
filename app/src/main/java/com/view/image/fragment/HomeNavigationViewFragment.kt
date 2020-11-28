package com.view.image.fragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.view.image.databinding.FragmentHomeNavigationViewBinding
import com.view.image.model.Rule
import com.view.image.model.RuleViewModel
import org.json.JSONArray
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


class HomeNavigationViewFragment : Fragment() {
    lateinit var viewBinding: FragmentHomeNavigationViewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        viewBinding = FragmentHomeNavigationViewBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val ruleViewModel = ViewModelProvider(activity ?: this).get(RuleViewModel::class.java)
        val ruleList = readJson()
        val ruleNameList = ArrayList<String>()
        for (rule in ruleList) {
            ruleNameList.add(rule.sourceName)
        }

        initNavigationView(ruleNameList)
        viewBinding.navigation.setNavigationItemSelectedListener {
            val title = it.title
            for (rule in ruleList) {
                if (title == rule.sourceName) {
                    ruleViewModel.setRule(rule)
                }
            }
            true
        }
        if (ruleList.isNotEmpty())
            ruleViewModel.ruleLive.value ?: ruleViewModel.setRule(ruleList[0])
    }

    private fun initNavigationView(ruleNameList: List<String>) {
        viewBinding.navigation.menu.clear()
        Log.d("initNavigationView", "initNavigationView")
        viewBinding.navigation.run {
            for (sum in ruleNameList.indices) {
                Log.d("name", ruleNameList[sum])
                this.menu.add(1, sum, sum, ruleNameList[sum])
            }
        }
    }

    // 读取规则
    private fun readJson(): List<Rule> {
        val newStringBuilder = StringBuilder()
        var inputStream: InputStream? = null
        var isr: InputStreamReader? = null
        var reader: BufferedReader? = null
        try {
            inputStream = context?.assets?.open("rule.json")
            isr = InputStreamReader(inputStream)
            reader = BufferedReader(isr)
            var jsonLine: String?
            while (reader.readLine().also { jsonLine = it } != null) {
                newStringBuilder.append(jsonLine)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
            reader?.close()
            isr?.close()
        }
        val result = newStringBuilder.toString()
        val jsonArray = JSONArray(result)
        val typeOf = object : TypeToken<List<Rule>>() {}.type
        return Gson().fromJson(jsonArray.toString(), typeOf)
    }
}