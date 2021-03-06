package com.view.image.rule

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.view.image.analyzeRule.Rule
import com.view.image.databinding.ActivityRuleBinding


class RuleActivity : AppCompatActivity() {
    lateinit var binding: ActivityRuleBinding

    companion object {
        /**
         * 启动当前activity
         * @param context Activity
         * @param code 请求状态码
         * @param rule 当前编辑规则
         * @param rulePosition 规则在当前规则列表的位置
         */
        fun actionStart(context: Activity, code: Int, rule: Rule, rulePosition: Int) {
            Intent(context, RuleActivity::class.java).apply {
                putExtra("rule", rule)
                putExtra("code", code)
                putExtra("rulePosition", rulePosition)
                context.startActivityForResult(this, code)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRuleBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}