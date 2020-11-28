package com.view.image.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.view.image.databinding.ActivityRuleBinding


class RuleActivity : AppCompatActivity() {
    lateinit var binding: ActivityRuleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRuleBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}